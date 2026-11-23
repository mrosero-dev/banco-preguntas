package co.edu.unicauca.lisw2t5g02.client;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import co.edu.unicauca.lisw2t5g02.model.ReviewAssignment;

/** REST client of the Review Service. */
public class ReviewClient {

    private final RestClient http;
    private final String baseUrl;

    public ReviewClient(RestClient http, String baseUrl) {
        this.http = http;
        this.baseUrl = baseUrl + "/api/reviews";
    }

    /** Questions registered in Review that still have no reviewers. */
    public List<ReviewAssignment> findPending() {
        return Mappers.toAssignments(http.get(baseUrl + "/pending"));
    }

    public ReviewAssignment assignReviewers(
            long questionId, long authorId, List<Long> reviewerIds) {

        ObjectNode body = http.mapper().createObjectNode();
        body.put("questionId", questionId);
        body.put("authorId", authorId);

        ArrayNode ids = body.putArray("reviewerIds");
        reviewerIds.forEach(ids::add);

        return Mappers.toAssignment(http.post(baseUrl + "/assign", body));
    }

    public List<ReviewAssignment> findByReviewer(long reviewerId) {
        return Mappers.toAssignments(
                http.get(baseUrl + "/reviewer/" + reviewerId));
    }

    public ReviewAssignment decide(
            long questionId,
            long reviewerId,
            boolean approved,
            String observations) {

        ObjectNode body = http.mapper().createObjectNode();
        body.put("reviewerId", reviewerId);
        body.put("approved", approved);
        body.put("observations", observations);

        return Mappers.toAssignment(
                http.post(baseUrl + "/" + questionId + "/decision", body));
    }

    /** HU-2.3 owner-checked history endpoint. */
    public Optional<ReviewAssignment> history(long questionId) {
        try {
            return Optional.of(Mappers.toAssignment(
                    http.get(baseUrl + "/" + questionId + "/history")));
        } catch (ApiException exception) {
            if (exception.isNotFound()) return Optional.empty();
            throw exception;
        }
    }

    /** Assignment of a question, or empty when Review does not know it. */
    public Optional<ReviewAssignment> find(long questionId) {
        try {
            return Optional.of(Mappers.toAssignment(
                    http.get(baseUrl + "/" + questionId)));
        } catch (ApiException exception) {
            if (exception.isNotFound()) {
                return Optional.empty();
            }
            throw exception;
        }
    }
}
