package com.tdd.productsupport.feedback.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tdd.productsupport.feedback.model.Feedback;
import com.tdd.productsupport.feedback.service.FeedbackService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class FeedbackControllerTests {

    @MockBean
    private FeedbackService feedbackService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Feedback found for given id - GET /feedback/1")
    public void testGetFeedbackById() throws Exception {
        Feedback mockFeedback = new Feedback("1", 1, 1, "POSTED", "This product is great!");

        doReturn(Optional.of(mockFeedback)).when(feedbackService).findById(mockFeedback.getId());

        mockMvc.perform(MockMvcRequestBuilders.get("/feedback/{id}", 1))

                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                .andExpect(header().string(HttpHeaders.ETAG, "\"1\""))
                .andExpect(header().string(HttpHeaders.LOCATION, "/feedback/1"))

                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.productId", is(1)))
                .andExpect(jsonPath("$.userId", is(1)))
                .andExpect(jsonPath("$.status", is("POSTED")));
    }

    @Test
    @DisplayName("Feedback not found for given id - GET /feedback/1")
    public void testFeedbackFoundForProductId() throws Exception {
        // When
        doReturn(Optional.empty()).when(feedbackService).findById("1");

        // Then
        mockMvc.perform(MockMvcRequestBuilders.get("/feedback/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("All feedback found - GET /feedback")
    public void testAllFeedbackFound() throws Exception {
        Feedback firstFeedback = new Feedback("1", 1, 1, "POSTED", "This product is great!");
        Feedback secondFeedback = new Feedback("2", 1, 2, "PUBLISHED", "This product is awesome!");

        doReturn(Arrays.asList(firstFeedback, secondFeedback)).when(feedbackService).findAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/feedback"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                .andExpect(jsonPath("$[0].id", is("1")))
                .andExpect(jsonPath("$[1].id", is("2")))
                .andExpect(jsonPath("$[0].status", is("POSTED")))
                .andExpect(jsonPath("$[1].status", is("PUBLISHED")));
    }

    @Test
    @DisplayName("Save a new feedback - POST /feedback")
    public void testSavingNewFeedback() throws Exception{
        Feedback newFeedback = new Feedback("1", 1, 1, "POSTED", "This product is great!");
        Feedback mockedFeedback = new Feedback("1", 1, 1, "POSTED", 1, "This product is great!");

        doReturn(mockedFeedback).when(feedbackService).save(ArgumentMatchers.any());

        mockMvc.perform(MockMvcRequestBuilders.post("/feedback")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(new ObjectMapper().writeValueAsString(newFeedback)))

                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                .andExpect(header().string(HttpHeaders.ETAG, "\"1\""))
                .andExpect(header().string(HttpHeaders.LOCATION, "/feedback/1"))

                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.status", is("POSTED")))
                .andExpect(jsonPath("$.version", is(1)));
    }
}
