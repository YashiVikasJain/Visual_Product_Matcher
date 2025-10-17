package com.example.visualmatcher.visual_product_matcher.repository;

import com.example.visualmatcher.visual_product_matcher.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
