
package co.edu.unicauca.review.api.controller;

import co.edu.unicauca.review.application.ReviewApplicationService;
import co.edu.unicauca.review.domain.model.ReviewAssignment;
import co.edu.unicauca.review.domain.exception.ReviewAssignmentNotFoundException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;

import co.edu.unicauca.review.api.NoSecurityWebTestConfig;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

@WebMvcTest(ReviewController.class)
@Import(NoSecurityWebTestConfig.class)
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewApplicationService service;

    @Test
    void shouldAssignReviewersSuccessfully() throws Exception {

        ReviewAssignment assignment =
                new ReviewAssignment(100L, 10L);

        assignment.assignReviewer(20L);
        assignment.assignReviewer(30L);

        when(service.assignReviewers(
                100L, 10L, List.of(20L, 30L)
        )).thenReturn(assignment);

        mockMvc.perform(post("/api/reviews/assign")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "questionId": 100,
                            "authorId": 10,
                            "reviewerIds": [20, 30]
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.questionId").value(100))
                .andExpect(jsonPath("$.authorId").value(10))
                .andExpect(jsonPath("$.reviews.length()").value(2))
                .andExpect(jsonPath("$.reviews[0].reviewerId").value(20))
                .andExpect(jsonPath("$.reviews[0].status").value("PENDING"));

        verify(service).assignReviewers(
                100L, 10L, List.of(20L, 30L)
        );
    }

    @Test
    void shouldApproveReviewSuccessfully() throws Exception {

        ReviewAssignment assignment =
                new ReviewAssignment(100L, 10L);

        assignment.assignReviewer(20L);
        assignment.getReviews().get(0).approve();

        when(service.approveReview(100L, 20L))
                .thenReturn(assignment);

        mockMvc.perform(post("/api/reviews/100/decision")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "reviewerId": 20,
                            "approved": true
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviews[0].status").value("APPROVED"))
                .andExpect(jsonPath("$.consolidatedStatus").value("APPROVED"));

        verify(service).approveReview(100L, 20L);
    }

    @Test
    void shouldRejectReviewSuccessfully() throws Exception {

        ReviewAssignment assignment =
                new ReviewAssignment(100L, 10L);

        assignment.assignReviewer(20L);
        assignment.getReviews().get(0).reject(
                "The question has an unclear statement"
        );

        when(service.rejectReview(
                100L, 20L,
                "The question has an unclear statement"
        )).thenReturn(assignment);

        mockMvc.perform(post("/api/reviews/100/decision")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "reviewerId": 20,
                            "approved": false,
                            "observations": "The question has an unclear statement"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviews[0].status").value("REJECTED"))
                .andExpect(jsonPath("$.reviews[0].observations")
                        .value("The question has an unclear statement"))
                .andExpect(jsonPath("$.consolidatedStatus").value("REJECTED"));

        verify(service).rejectReview(
                100L, 20L,
                "The question has an unclear statement"
        );
    }

    @Test
    void shouldGetReviewAssignmentSuccessfully() throws Exception {

        ReviewAssignment assignment =
                new ReviewAssignment(100L, 10L);

        assignment.assignReviewer(20L);
        assignment.assignReviewer(30L);

        when(service.getAssignment(100L))
                .thenReturn(assignment);

        mockMvc.perform(get("/api/reviews/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.questionId").value(100))
                .andExpect(jsonPath("$.authorId").value(10))
                .andExpect(jsonPath("$.reviews.length()").value(2))
                .andExpect(jsonPath("$.consolidatedStatus").value("PENDING"));

        verify(service).getAssignment(100L);
    }

    @Test
    void shouldReturnBadRequestWhenReviewerListIsEmpty() throws Exception {

        mockMvc.perform(post("/api/reviews/assign")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "questionId": 100,
                            "authorId": 10,
                            "reviewerIds": []
                        }
                        """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void shouldReturnNotFoundWhenAssignmentDoesNotExist() throws Exception {

        when(service.getAssignment(999L))
                .thenThrow(new ReviewAssignmentNotFoundException(
                        "Review assignment not found for question: 999"
                ));

        mockMvc.perform(get("/api/reviews/999"))
                .andExpect(status().isNotFound());

        verify(service).getAssignment(999L);
    }

    @Test
    void shouldReturnBadRequestWhenMoreThanThreeReviewersAreAssigned()
            throws Exception {

        mockMvc.perform(post("/api/reviews/assign")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "questionId": 100,
                            "authorId": 10,
                            "reviewerIds": [20, 30, 40, 50]
                        }
                        """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void shouldReturnBadRequestWhenQuestionIdIsNull() throws Exception {

        mockMvc.perform(post("/api/reviews/assign")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "questionId": null,
                            "authorId": 10,
                            "reviewerIds": [20]
                        }
                        """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void shouldReturnBadRequestWhenAuthorIdIsNull() throws Exception {

        mockMvc.perform(post("/api/reviews/assign")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "questionId": 100,
                            "authorId": null,
                            "reviewerIds": [20]
                        }
                        """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void shouldReturnBadRequestWhenReviewerIdIsNull() throws Exception {

        mockMvc.perform(post("/api/reviews/100/decision")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "reviewerId": null,
                            "approved": true
                        }
                        """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void shouldReturnBadRequestWhenApprovedIsNull() throws Exception {

        mockMvc.perform(post("/api/reviews/100/decision")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "reviewerId": 20,
                            "approved": null
                        }
                        """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }
}
