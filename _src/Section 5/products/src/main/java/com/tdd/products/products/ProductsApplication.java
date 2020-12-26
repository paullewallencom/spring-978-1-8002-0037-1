package com.tdd.products.products;

import com.tdd.products.products.model.Product;
import com.tdd.products.products.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProductsApplication implements CommandLineRunner {

	@Autowired
	private ProductService productService;

	public static void main(String[] args) {
		SpringApplication.run(ProductsApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Product firstProduct = new Product("USB Thumb Drive", "128 GB USB Drive", 10);
		productService.save(firstProduct);
	}
}
