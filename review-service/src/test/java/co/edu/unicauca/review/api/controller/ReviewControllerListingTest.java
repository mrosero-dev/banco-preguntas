package co.edu.unicauca.review.api.controller;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;

import co.edu.unicauca.review.api.NoSecurityWebTestConfig;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import co.edu.unicauca.review.application.ReviewApplicationService;
import co.edu.unicauca.review.domain.model.ReviewAssignment;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewController.class)
@Import(NoSecurityWebTestConfig.class)
class ReviewControllerListingTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewApplicationService service;

    @Test
    void shouldListAssignmentsOfReviewer() throws Exception {
        ReviewAssignment assignment = new ReviewAssignment(100L, 10L);
        assignment.assignReviewer(20L);
        when(service.findAssignmentsByReviewer(20L))
                .thenReturn(List.of(assignment));

        mockMvc.perform(get("/api/reviews/reviewer/20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].questionId").value(100))
                .andExpect(jsonPath("$[0].reviews[0].reviewerId").value(20));
    }

    @Test
    void shouldReturnEmptyListWhenReviewerHasNoQuestions() throws Exception {
        when(service.findAssignmentsByReviewer(21L)).thenReturn(List.of());

        mockMvc.perform(get("/api/reviews/reviewer/21"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldListPendingAssignments() throws Exception {
        when(service.findPendingAssignments()).thenReturn(
                List.of(new ReviewAssignment(100L, 10L)));

        mockMvc.perform(get("/api/reviews/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].questionId").value(100))
                .andExpect(jsonPath("$[0].reviews.length()").value(0));
    }
}
