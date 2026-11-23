package co.edu.unicauca.lisw2t5g02.client;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.StringJoiner;

import co.edu.unicauca.lisw2t5g02.model.Question;
import co.edu.unicauca.lisw2t5g02.model.QuestionPage;
import co.edu.unicauca.lisw2t5g02.model.QuestionStatus;
import co.edu.unicauca.lisw2t5g02.model.VersionedQuestion;

/** REST client of the Question Service. */
public class QuestionClient {

    private final RestClient http;
    private final String baseUrl;

    public QuestionClient(RestClient http, String baseUrl) {
        this.http = http;
        this.baseUrl = baseUrl + "/api/questions";
    }

    public Question create(Question question) {
        return Mappers.toQuestion(
                http.post(baseUrl, Mappers.toQuestionBody(question, true)));
    }

    public Question update(Question question) {
        return Mappers.toQuestion(http.put(
                baseUrl + "/" + question.id() + "?authorId=" + question.authorId(),
                Mappers.toQuestionBody(question, false)));
    }

    public Question find(long id) {
        return Mappers.toQuestion(http.get(baseUrl + "/" + id));
    }

    public QuestionPage listByAuthor(
            long authorId,
            QuestionStatus status,
            String search,
            int page,
            int size) {

        StringJoiner query = new StringJoiner("&", "?", "");
        query.add("authorId=" + authorId);
        query.add("page=" + page);
        query.add("size=" + size);

        if (status != null) {
            query.add("status=" + status.name());
        }

        if (search != null && !search.isBlank()) {
            query.add("search=" + URLEncoder.encode(
                    search.trim(), StandardCharsets.UTF_8));
        }

        return Mappers.toQuestionPage(http.get(baseUrl + query));
    }

    /** Previous versions of the question, most recent first. */
    public List<VersionedQuestion> versions(long id, long authorId) {
        return Mappers.toVersions(
                http.get(baseUrl + "/" + id + "/versions"), id, authorId);
    }

    public Question submitForReview(long id) {
        return Mappers.toQuestion(
                http.post(baseUrl + "/" + id + "/submit-for-review", null));
    }

    public Question returnToDraft(long id, long authorId) {
        return Mappers.toQuestion(http.post(
                baseUrl + "/" + id + "/return-to-draft?authorId=" + authorId,
                null));
    }
}
