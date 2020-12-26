package com.tdd.products.products.service;

import com.tdd.products.products.repository.ProductRepository;
import com.tdd.products.products.model.Product;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.AssertionErrors;

import java.util.Arrays;
import java.util.Collection;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @MockBean
    private ProductRepository productRepository;

    @Test
    @DisplayName("Find product with id successfully")
    public void testFindProductById(){
        Product mockProduct = new Product(1, "Product", "Description", 10, 1);

        doReturn(mockProduct).when(productRepository).findProductById(1);

        Product foundProduct = productService.findById(1);

        Assertions.assertNotNull(foundProduct);
        Assertions.assertSame("Product", foundProduct.getName());
    }

    @Test
    @DisplayName("Fail to find product with id")
    public void testFailToFindProductById(){
        doReturn(null).when(productRepository).findProductById(1);

        Product foundProduct = productService.findById(1);

        Assertions.assertNull(foundProduct);
    }

    @Test
    @DisplayName("Find all products")
    public void testFindAllProducts(){
        Product firstProduct = new Product(1, "1st Product", "Description", 10, 1);
        Product secondProduct = new Product(2, "2nd Product", "Description", 10, 1);

        doReturn(Arrays.asList(firstProduct, secondProduct)).when(productRepository).findAll();

        Iterable<Product> allProducts = productService.findAll();

        Assertions.assertEquals(2, ((Collection<?>) allProducts).size());
    }

    @Test
    @DisplayName("Save new product successfully")
    public void testSuccessfulProductSave(){
        Product mockProduct = new Product(1, "Product", "Description", 10, 1);

        doReturn(mockProduct).when(productRepository).save(any());

        Product savedProduct = productService.save(mockProduct);

        AssertionErrors.assertNotNull("Product should not be null", savedProduct);
        Assertions.assertSame("Product", mockProduct.getName());
        Assertions.assertSame(1, savedProduct.getVersion());
    }

    @Test
    @DisplayName("Update an existing product successfully")
    public void testUpdatingProductSuccessfully(){
        Product existingProduct = new Product(1, "Product", "Description", 10, 1);
        Product updatedProduct = new Product(1, "New Name", "Description", 20, 2);

        doReturn(existingProduct).when(productRepository).findProductById(1);
        doReturn(updatedProduct).when(productRepository).save(existingProduct);

        Product updateProduct = productService.update(existingProduct);

        Assertions.assertEquals("New Name", updateProduct.getName());
    }

    @Test
    @DisplayName("Fail to update an existing product")
    public void testFailToUpdateExistingProduct(){
        Product mockProduct = new Product(1, "Product", "Description", 10, 1);

        doReturn(null).when(productRepository).findProductById(1);

        Product updatedProduct = productService.update(mockProduct);

        AssertionErrors.assertNull("Product should be null", updatedProduct);
    }

}
