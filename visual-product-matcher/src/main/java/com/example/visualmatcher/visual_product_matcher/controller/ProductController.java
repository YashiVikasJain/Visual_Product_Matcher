package com.example.visualmatcher.visual_product_matcher.controller;

import com.example.visualmatcher.visual_product_matcher.model.Product;
import com.example.visualmatcher.visual_product_matcher.service.ImageMatchingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ImageMatchingService imageMatchingService;

    @PostMapping("/upload")
    public Product uploadProduct(@RequestParam String name,
                                 @RequestParam String category,
                                 @RequestParam("file") MultipartFile file) throws IOException {
        return imageMatchingService.createAndSaveProduct(name, category, file);
    }

    @PostMapping("/match")
    public List<Product> matchProduct(@RequestParam("file") MultipartFile file) throws IOException {
        try {
            return imageMatchingService.findSimilarProducts(file);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return imageMatchingService.getAllProducts();
    }
}
