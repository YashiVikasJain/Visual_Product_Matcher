package com.example.visualmatcher.visual_product_matcher;

import com.example.visualmatcher.visual_product_matcher.model.Product;
import com.example.visualmatcher.visual_product_matcher.repository.ProductRepository;
import com.example.visualmatcher.visual_product_matcher.service.ImageMatchingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private ImageMatchingService imageMatchingService;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public void run(String... args) throws Exception {
        if (productRepository.count() == 0) {
            System.out.println("Database is empty. Seeding sample products...");

            // Loop through 50 images
            for (int i = 1; i <= 50; i++) {
                String name = "Product " + i;
                String category = "Bag"; // You can modify category per product if needed
                String fileName = "seed" + i + ".jpg";
                seedProduct(name, category, fileName);
            }

            System.out.println("Finished seeding 50 products.");
        }
    }

    private void seedProduct(String name, String category, String fileName) throws Exception {
        ClassPathResource resource = new ClassPathResource("seed-images/" + fileName);

        if (!resource.exists()) {
            System.out.println("File not found: " + fileName);
            return;
        }

        Product product = imageMatchingService.createAndSaveProduct(
                name,
                category,
                resource.getInputStream(),
                fileName
        );
    }
}
