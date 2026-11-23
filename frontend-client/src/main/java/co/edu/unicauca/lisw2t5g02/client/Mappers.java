package co.edu.unicauca.lisw2t5g02.client;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import co.edu.unicauca.lisw2t5g02.model.Notification;
import co.edu.unicauca.lisw2t5g02.model.Question;
import co.edu.unicauca.lisw2t5g02.model.QuestionOption;
import co.edu.unicauca.lisw2t5g02.model.QuestionPage;
import co.edu.unicauca.lisw2t5g02.model.QuestionStatus;
import co.edu.unicauca.lisw2t5g02.model.Review;
import co.edu.unicauca.lisw2t5g02.model.ReviewAssignment;
import co.edu.unicauca.lisw2t5g02.model.ReviewStatus;
import co.edu.unicauca.lisw2t5g02.model.Role;
import co.edu.unicauca.lisw2t5g02.model.User;
import co.edu.unicauca.lisw2t5g02.model.VersionedQuestion;

/** Converts JSON documents of the microservices to and from the model. */
final class Mappers {

    private static final JsonNodeFactory JSON = JsonNodeFactory.instance;

    private Mappers() {
    }

    static User toUser(JsonNode node) {
        return new User(
                node.path("id").asLong(),
                node.path("fullName").asText(""),
                node.path("email").asText(""),
                Role.valueOf(node.path("role").asText()));
    }

    static List<User> toUsers(JsonNode array) {
        List<User> users = new ArrayList<>();
        array.forEach(node -> users.add(toUser(node)));
        return users;
    }

    static Question toQuestion(JsonNode node) {

        List<QuestionOption> options = new ArrayList<>();
        node.path("options").forEach(option -> options.add(
                new QuestionOption(
                        option.path("text").asText(""),
                        option.path("correct").asBoolean())));

        return new Question(
                node.path("id").asLong(),
                node.path("authorId").asLong(),
                node.path("statement").asText(""),
                node.path("context").asText(""),
                node.path("difficulty").asText(""),
                node.path("category").asText(""),
                node.path("topic").asText(""),
                node.path("subtopic").asText(""),
                node.path("competence").asText(""),
                node.path("justification").asText(""),
                node.path("source").asText(""),
                QuestionStatus.valueOf(node.path("status").asText()),
                options);
    }

    /** Previous versions of a question (GET /api/questions/{id}/versions). */
    static List<VersionedQuestion> toVersions(
            JsonNode node, long questionId, long authorId) {

        List<VersionedQuestion> versions = new ArrayList<>();

        node.forEach(item -> {
            List<QuestionOption> options = new ArrayList<>();
            item.path("options").forEach(option -> options.add(
                    new QuestionOption(
                            option.path("text").asText(""),
                            option.path("correct").asBoolean())));

            // Una versión anterior siempre terminó rechazada.
            Question content = new Question(
                    questionId,
                    authorId,
                    item.path("statement").asText(""),
                    item.path("context").asText(""),
                    item.path("difficulty").asText(""),
                    item.path("category").asText(""),
                    item.path("topic").asText(""),
                    item.path("subtopic").asText(""),
                    item.path("competence").asText(""),
                    item.path("justification").asText(""),
                    item.path("source").asText(""),
                    QuestionStatus.REJECTED,
                    options);

            versions.add(new VersionedQuestion(
                    item.path("versionNumber").asInt(),
                    item.path("createdAt").asText(""),
                    content));
        });

        return versions;
    }

    static QuestionPage toQuestionPage(JsonNode node) {

        List<Question> questions = new ArrayList<>();
        node.path("content").forEach(item -> questions.add(toQuestion(item)));

        return new QuestionPage(
                questions,
                node.path("page").asInt(),
                node.path("size").asInt(),
                node.path("totalElements").asLong(),
                node.path("totalPages").asInt(),
                node.path("first").asBoolean(),
                node.path("last").asBoolean());
    }

    /** Body for create and update; the id is never sent in the body. */
    static ObjectNode toQuestionBody(Question question, boolean includeAuthor) {

        ObjectNode body = JSON.objectNode();

        if (includeAuthor) {
            body.put("authorId", question.authorId());
        }

        body.put("statement", question.statement());
        body.put("context", question.context());
        body.put("difficulty", question.difficulty());
        body.put("category", question.category());
        body.put("topic", question.topic());
        body.put("subtopic", question.subtopic());
        body.put("competence", question.competence());
        body.put("justification", question.justification());
        body.put("source", question.source());

        ArrayNode options = body.putArray("options");
        for (QuestionOption option : question.options()) {
            ObjectNode item = options.addObject();
            item.put("text", option.text());
            item.put("correct", option.correct());
        }

        return body;
    }

    static ReviewAssignment toAssignment(JsonNode node) {

        List<Review> reviews = new ArrayList<>();
        node.path("reviews").forEach(item -> reviews.add(toReview(item)));
        List<Review> history = new ArrayList<>();
        node.path("history").forEach(item -> history.add(toReview(item)));

        return new ReviewAssignment(
                node.path("id").asLong(), node.path("questionId").asLong(),
                node.path("authorId").asLong(), reviews,
                ReviewStatus.valueOf(node.path("consolidatedStatus").asText()), history);
    }


    private static Review toReview(JsonNode item) {
        return new Review(item.path("reviewerId").asLong(),
                ReviewStatus.valueOf(item.path("status").asText()),
                item.path("observations").isNull() ? "" : item.path("observations").asText(""),
                item.path("reviewedAt").isNull() ? "" : item.path("reviewedAt").asText(""),
                item.path("roundNumber").asInt(1));
    }

    static List<ReviewAssignment> toAssignments(JsonNode array) {
        List<ReviewAssignment> assignments = new ArrayList<>();
        array.forEach(node -> assignments.add(toAssignment(node)));
        return assignments;
    }

    static Notification toNotification(JsonNode node) {
        return new Notification(
                node.path("id").asLong(),
                node.path("recipientId").asLong(),
                node.path("type").asText(""),
                node.path("message").asText(""),
                node.path("createdAt").asText(""),
                node.path("read").asBoolean());
    }

    static List<Notification> toNotifications(JsonNode array) {
        List<Notification> notifications = new ArrayList<>();
        array.forEach(node -> notifications.add(toNotification(node)));
        return notifications;
    }
}
