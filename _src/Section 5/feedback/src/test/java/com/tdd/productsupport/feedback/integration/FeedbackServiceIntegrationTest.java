package com.tdd.productsupport.feedback.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tdd.productsupport.feedback.model.Feedback;
import com.tdd.productsupport.feedback.repository.MongoSpringExtension;
import com.tdd.productsupport.feedback.repository.MongoTestDataFile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith({SpringExtension.class, MongoSpringExtension.class})
@SpringBootTest
@AutoConfigureMockMvc
public class FeedbackServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MongoTemplate mongoTemplate;

    // Will only be used by the annotation
    public MongoTemplate getMongoTemplate(){
        return mongoTemplate;
    }

    @Test
    @DisplayName("Feedback found for given id - GET /feedback/1")
    @MongoTestDataFile(value = "data.json", classType = Feedback.class, collectionName = "Feedback")
    public void testGetFeedbackById() throws Exception {
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
    @MongoTestDataFile(value = "data.json", classType = Feedback.class, collectionName = "Feedback")
    public void testFeedbackFoundForProductId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/feedback/10"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("All feedback found - GET /feedback")
    @MongoTestDataFile(value = "data.json", classType = Feedback.class, collectionName = "Feedback")
    public void testAllFeedbackFound() throws Exception {
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
    @MongoTestDataFile(value = "data.json", classType = Feedback.class, collectionName = "Feedback")
    public void testSavingNewFeedback() throws Exception{
        Feedback newFeedback = new Feedback("1", 1, 1, "POSTED", "This product is great!");

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
