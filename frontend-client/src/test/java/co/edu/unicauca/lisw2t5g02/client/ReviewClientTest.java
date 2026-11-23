package co.edu.unicauca.lisw2t5g02.client;

import java.util.List;

import org.junit.jupiter.api.Test;

import co.edu.unicauca.lisw2t5g02.model.ReviewAssignment;
import co.edu.unicauca.lisw2t5g02.model.ReviewStatus;

import static org.junit.jupiter.api.Assertions.*;

class ReviewClientTest {

    private static final String ASSIGNMENT =
            "{\"id\":1,\"questionId\":100,\"authorId\":2,\"reviews\":["
                    + "{\"reviewerId\":4,\"status\":\"REJECTED\","
                    + "\"observations\":\"Falta fuente\","
                    + "\"reviewedAt\":\"2026-11-05T10:00:00\"},"
                    + "{\"reviewerId\":5,\"status\":\"PENDING\","
                    + "\"observations\":null,\"reviewedAt\":null}],"
                    + "\"consolidatedStatus\":\"PENDING\"}";

    @Test
    void shouldListPendingQuestions() throws Exception {
        try (MockServer server = new MockServer()
                .on("GET", "/api/reviews/pending", 200, "[" + ASSIGNMENT + "]")) {

            List<ReviewAssignment> pending =
                    new ReviewClient(new RestClient(), server.url()).findPending();

            assertEquals(100L, pending.get(0).questionId());
        }
    }

    @Test
    void shouldAssignReviewersSendingTheirIdentifiers() throws Exception {
        try (MockServer server = new MockServer()
                .on("POST", "/api/reviews/assign", 201, ASSIGNMENT)) {

            ReviewAssignment result =
                    new ReviewClient(new RestClient(), server.url())
                            .assignReviewers(100, 2, List.of(4L, 5L));

            assertEquals(2, result.reviews().size());
            assertTrue(server.lastBody.contains("\"reviewerIds\":[4,5]"));
            assertTrue(server.lastBody.contains("\"questionId\":100"));
        }
    }

    @Test
    void shouldListAssignmentsOfReviewer() throws Exception {
        try (MockServer server = new MockServer()
                .on("GET", "/api/reviews/reviewer/4", 200, "[" + ASSIGNMENT + "]")) {

            ReviewAssignment assignment =
                    new ReviewClient(new RestClient(), server.url())
                            .findByReviewer(4).get(0);

            assertEquals(ReviewStatus.REJECTED,
                    assignment.reviewOf(4).orElseThrow().status());
            assertEquals("Falta fuente",
                    assignment.reviewOf(4).orElseThrow().observations());
            assertEquals("", assignment.reviewOf(5).orElseThrow().observations());
            assertTrue(assignment.reviewOf(9).isEmpty());
        }
    }

    @Test
    void shouldSendDecision() throws Exception {
        try (MockServer server = new MockServer()
                .on("POST", "/api/reviews/100/decision", 200, ASSIGNMENT)) {

            new ReviewClient(new RestClient(), server.url())
                    .decide(100, 4, false, "Falta fuente");

            assertTrue(server.lastBody.contains("\"approved\":false"));
            assertTrue(server.lastBody.contains("\"observations\":\"Falta fuente\""));
            assertTrue(server.lastBody.contains("\"reviewerId\":4"));
        }
    }

    @Test
    void shouldReturnEmptyWhenQuestionHasNoAssignment() throws Exception {
        try (MockServer server = new MockServer()) {

            assertTrue(new ReviewClient(new RestClient(), server.url())
                    .find(999).isEmpty());
        }
    }

    @Test
    void shouldFindAssignmentOfQuestion() throws Exception {
        try (MockServer server = new MockServer()
                .on("GET", "/api/reviews/100", 200, ASSIGNMENT)) {

            assertEquals(ReviewStatus.PENDING,
                    new ReviewClient(new RestClient(), server.url())
                            .find(100).orElseThrow().consolidatedStatus());
        }
    }

    @Test
    void shouldPropagateOtherErrors() throws Exception {
        try (MockServer server = new MockServer()
                .on("GET", "/api/reviews/100", 500, "boom")) {

            assertThrows(ApiException.class,
                    () -> new ReviewClient(new RestClient(), server.url())
                            .find(100));
        }
    }

    @Test
    void shouldExposeConflictMessage() throws Exception {
        try (MockServer server = new MockServer().on("POST",
                "/api/reviews/100/decision", 409,
                "This review has already been completed")) {

            ApiException error = assertThrows(ApiException.class,
                    () -> new ReviewClient(new RestClient(), server.url())
                            .decide(100, 4, true, ""));

            assertEquals(409, error.getStatus());
        }
    }
}
