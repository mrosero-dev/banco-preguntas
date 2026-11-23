package co.edu.unicauca.lisw2t5g02.client;

import java.util.List;

import org.junit.jupiter.api.Test;

import co.edu.unicauca.lisw2t5g02.model.Question;
import co.edu.unicauca.lisw2t5g02.model.QuestionOption;
import co.edu.unicauca.lisw2t5g02.model.QuestionPage;
import co.edu.unicauca.lisw2t5g02.model.QuestionStatus;

import static org.junit.jupiter.api.Assertions.*;

class QuestionClientTest {

    private static final String QUESTION =
            "{\"id\":7,\"authorId\":2,\"statement\":\"Que es UML?\","
                    + "\"context\":\"Ctx\",\"difficulty\":\"Media\","
                    + "\"category\":\"Ingenieria\",\"topic\":\"Modelado\","
                    + "\"subtopic\":\"UML\",\"competence\":\"Disenar\","
                    + "\"justification\":\"Porque\",\"source\":\"Fowler\","
                    + "\"status\":\"DRAFT\",\"options\":["
                    + "{\"id\":1,\"text\":\"A\",\"correct\":true},"
                    + "{\"id\":2,\"text\":\"B\",\"correct\":false}]}";

    private static Question newQuestion(Long id) {
        return new Question(id, 2, "Que es UML?", "Ctx", "Media",
                "Ingenieria", "Modelado", "UML", "Disenar", "Porque",
                "Fowler", QuestionStatus.DRAFT,
                List.of(new QuestionOption("A", true),
                        new QuestionOption("B", false)));
    }

    @Test
    void shouldCreateQuestionAndMapResponse() throws Exception {
        try (MockServer server = new MockServer()
                .on("POST", "/api/questions", 201, QUESTION)) {

            Question created = new QuestionClient(new RestClient(), server.url())
                    .create(newQuestion(null));

            assertEquals(7L, created.id());
            assertEquals(QuestionStatus.DRAFT, created.status());
            assertEquals("A", created.correctText());
            assertTrue(server.lastBody.contains("\"authorId\":2"));
            assertTrue(server.lastBody.contains("\"source\":\"Fowler\""));
            assertTrue(server.lastBody.contains("\"correct\":true"));
        }
    }

    @Test
    void shouldUpdateWithAuthorInQueryAndNotInBody() throws Exception {
        try (MockServer server = new MockServer()
                .on("PUT", "/api/questions/7?authorId=2", 200, QUESTION)) {

            new QuestionClient(new RestClient(), server.url())
                    .update(newQuestion(7L));

            assertFalse(server.lastBody.contains("authorId"));
        }
    }

    @Test
    void shouldListWithFiltersAndPagination() throws Exception {
        String page = "{\"content\":[" + QUESTION + "],\"page\":1,\"size\":5,"
                + "\"totalElements\":6,\"totalPages\":2,"
                + "\"first\":false,\"last\":true}";

        try (MockServer server = new MockServer().on("GET",
                "/api/questions?authorId=2&page=1&size=5&status=DRAFT&search=uml%20x",
                200, page)) {

            // '+' is how URLEncoder writes a space; the server decodes it.
            server.on("GET",
                    "/api/questions?authorId=2&page=1&size=5&status=DRAFT&search=uml+x",
                    200, page);

            QuestionPage result = new QuestionClient(new RestClient(), server.url())
                    .listByAuthor(2, QuestionStatus.DRAFT, "uml x", 1, 5);

            assertEquals(1, result.content().size());
            assertEquals(6, result.totalElements());
            assertTrue(result.last());
        }
    }

    @Test
    void shouldOmitOptionalFiltersWhenNotGiven() throws Exception {
        try (MockServer server = new MockServer().on("GET",
                "/api/questions?authorId=2&page=0&size=5", 200,
                "{\"content\":[],\"page\":0,\"size\":5,\"totalElements\":0,"
                        + "\"totalPages\":0,\"first\":true,\"last\":true}")) {

            QuestionPage result = new QuestionClient(new RestClient(), server.url())
                    .listByAuthor(2, null, " ", 0, 5);

            assertTrue(result.content().isEmpty());
        }
    }

    @Test
    void shouldFindSubmitAndReturnToDraft() throws Exception {
        String pending = QUESTION.replace("DRAFT", "PENDING_REVIEW");

        try (MockServer server = new MockServer()
                .on("GET", "/api/questions/7", 200, QUESTION)
                .on("POST", "/api/questions/7/submit-for-review", 200, pending)
                .on("POST", "/api/questions/7/return-to-draft?authorId=2",
                        200, QUESTION)) {

            QuestionClient client = new QuestionClient(new RestClient(), server.url());

            assertEquals(QuestionStatus.DRAFT, client.find(7).status());
            assertEquals(QuestionStatus.PENDING_REVIEW,
                    client.submitForReview(7).status());
            assertEquals(QuestionStatus.DRAFT, client.returnToDraft(7, 2).status());
        }
    }

    @Test
    void shouldExposeValidationMessageFromService() throws Exception {
        try (MockServer server = new MockServer().on("POST", "/api/questions", 400,
                "Question cannot contain 'Todas las anteriores'")) {

            ApiException error = assertThrows(ApiException.class,
                    () -> new QuestionClient(new RestClient(), server.url())
                            .create(newQuestion(null)));

            assertTrue(error.getMessage().contains("Todas las anteriores"));
        }
    }
}
