package com.tdd.productsupport.feedback.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tdd.productsupport.feedback.model.Feedback;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@DataMongoTest
public class FeedbackRepositoryTests {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private FeedbackRepository feedbackRepository;

    private static File DATA_JSON = Paths.get("src", "test", "resources", "data", "data.json").toFile();

    @BeforeEach
    public void beforeEach() throws IOException {
        // Deserialize feedback from JSON file to Feedback array
        Feedback[] feedback = new ObjectMapper().readValue(DATA_JSON, Feedback[].class);

        // Save each feedback to MongoDB
        Arrays.stream(feedback).forEach(mongoTemplate::save);
    }

    @AfterEach
    public void afterEach() {
        mongoTemplate.dropCollection("Feedback");
    }

    @Test
    @DisplayName("Find feedback by id")
    public void testFindFeedbackById(){
        Optional<Feedback> feedback = feedbackRepository.findById("1");

        Assertions.assertTrue(feedback.isPresent(), "A feedback with id 1 should exist");
        feedback.ifPresent(f -> {
            Assertions.assertEquals("1", f.getId());
            Assertions.assertEquals(1, f.getProductId().intValue());
            Assertions.assertEquals(1, f.getUserId().intValue());
            Assertions.assertEquals("POSTED", f.getStatus());
        });
    }

    @Test
    @DisplayName("Find all feedback")
    public void testFindAllFeedback(){
        List<Feedback> feedbackList = feedbackRepository.findAll();

        Assertions.assertEquals(3, feedbackList.size());
    }

    @Test
    @DisplayName("Find feedback by product id")
    public void testFindFeedbackByProductId(){
        Optional<Feedback> feedback = feedbackRepository.findByProductId(2);

        Assertions.assertTrue(feedback.isPresent(), "Feedback for product 2 should exist");
    }

    @Test
    @DisplayName("Fail to find feedback by product id")
    public void testFailToFindFeedbackByProductId(){
        Optional<Feedback> feedback = feedbackRepository.findByProductId(8);

        Assertions.assertFalse(feedback.isPresent(), "Feedback for product 8 should not exist");
    }

    @Test
    @DisplayName("Save a new feedback")
    public void testSavingNewFeedback(){
        Feedback newFeedback = new Feedback("4", 2, 1, "POSTED", "This product is great!");

        Feedback savedFeedback = feedbackRepository.save(newFeedback);

        Assertions.assertEquals(1, savedFeedback.getVersion().intValue());
    }

    @Test
    @DisplayName("Delete a feedback")
    public void testDeleteFeedback(){
        Feedback existingFeedback = new Feedback("1", 1, 1, "POSTED", 1, "This product is great!");

        feedbackRepository.delete(existingFeedback);

        Assertions.assertEquals(2, feedbackRepository.count());
    }
}
