package com.tdd.productsupport.feedback.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

public class MongoSpringExtension implements BeforeEachCallback, AfterEachCallback {

    // Path to where our test JSON files are stored.
    private static Path JSON_FILE_PATH = Paths.get("src", "test", "resources", "data");

    // Will use to load JSON file as a list of objects
    private ObjectMapper mapper = new ObjectMapper();

    /**
     * This callback method will be called before each test execution.
     * It is responsible for importing the JSON document, defined by the MongoDataFile annotation,
     * into the embedded MongoDB, through the provided MongoTemplate.
     * @param context       The ExtensionContext, which provides access to the test method.
     */
    @Override
    public void beforeEach(ExtensionContext context) {
        context.getTestMethod().ifPresent(method -> {
            // Load test file using the annotation argument
            MongoTestDataFile mongoTestDataFile = method.getAnnotation(MongoTestDataFile.class);

            // Load the MongoTemplate to import test data
            getMongoTemplate(context).ifPresent(mongoTemplate -> {
                try {
                    // Load a list of objects using Jackson object mapper
                    List objects = mapper.readValue(JSON_FILE_PATH.resolve(mongoTestDataFile.value()).toFile(),
                            mapper.getTypeFactory().constructCollectionType(List.class, mongoTestDataFile.classType()));

                    // Save each object into MongoDB
                    objects.forEach(mongoTemplate::save);
                } catch (IOException io) {
                    io.printStackTrace();
                }
            });
        });
    }

    /**
     * This callback method will be called after each test execution.
     * It is responsible for dropping the test's MongoDB collection
     * so that the next test that runs is clean.
     *
     * @param context       The ExtensionContext, which provides access to the test method.
     */
    @Override
    public void afterEach(ExtensionContext context){
        context.getTestMethod().ifPresent(method -> {
            // Load the MongoDataFile annotation value from the test method
            MongoTestDataFile mongoTestDataFile = method.getAnnotation(MongoTestDataFile.class);

            // Get MongoTemplate from context to drop the test collection
            Optional<MongoTemplate> mongoTemplate = getMongoTemplate(context);
            mongoTemplate.ifPresent(t -> t.dropCollection(mongoTestDataFile.collectionName()));
        });
    }

    /**
     * Helper method that uses reflection to invoke the getMongoTemplate() method on the test instance.
     * @param context   The ExtensionContext, which provides access to the test instance.
     * @return          An optional MongoTemplate, if it exists.
     */
    private Optional<MongoTemplate> getMongoTemplate(ExtensionContext context) {
        Optional<Class<?>> testClass = context.getTestClass();
        if (testClass.isPresent()) {
            Class<?> c = testClass.get();
            try {
                // Find the getMongoTemplate method on the test class
                Method method = c.getMethod("getMongoTemplate", null);

                // Invoke the getMongoTemplate method on the test class
                Optional<Object> testInstance = context.getTestInstance();
                if (testInstance.isPresent()) {
                    return Optional.of((MongoTemplate)method.invoke(testInstance.get(), null));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return Optional.empty();
    }
}