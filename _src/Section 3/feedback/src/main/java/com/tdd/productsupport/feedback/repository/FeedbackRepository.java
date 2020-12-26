package com.tdd.productsupport.feedback.repository;

import com.tdd.productsupport.feedback.model.Feedback;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface FeedbackRepository extends MongoRepository<Feedback, String> {

    Optional<Feedback> findByProductId(Integer id);
}
