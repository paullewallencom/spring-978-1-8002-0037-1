package com.tdd.productsupport.feedback.repository;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation that extend the functionality of MongoSpringExtension which provides information about
 * the test MongoDB JSON file for this method as well as the collection name and type of objects stored in the test file.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MongoTestDataFile {
    /**
     * Name of the MongoDB JSON test file with extension
     */
    String value();

    /**
     * Class type of objects stored in the MongoDB test file.
     */
    Class classType();

    /**
     * Name of the MongoDB collection for the test objects.
     */
    String collectionName();
}
