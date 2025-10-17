package com.example.visualmatcher.visual_product_matcher.service;

import com.example.visualmatcher.visual_product_matcher.model.Product;
import com.example.visualmatcher.visual_product_matcher.repository.ProductRepository;
import dev.brachtendorf.jimagehash.hash.Hash;
import dev.brachtendorf.jimagehash.hashAlgorithms.PerceptiveHash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ImageMatchingService {

    private final ProductRepository productRepository;
    private final Path rootLocation = Paths.get("uploads");
    private final PerceptiveHash pHash = new PerceptiveHash(32); // 32-bit resolution

    @Autowired
    public ImageMatchingService(ProductRepository productRepository) {
        this.productRepository = productRepository;
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage directory!", e);
        }
    }

    // Overloaded method for seeding
    public Product createAndSaveProduct(String name, String category, InputStream inputStream, String originalFilename) throws IOException {
        String uniqueFilename = saveFile(originalFilename, inputStream);
        BufferedImage image = ImageIO.read(this.rootLocation.resolve(uniqueFilename).toFile());
        Hash imageHash = pHash.hash(image);

        String base64Hash = Base64.getEncoder().encodeToString(imageHash.getHashValue().toByteArray());

        Product product = new Product();
        product.setName(name);
        product.setCategory(category);
        product.setImageUrl(uniqueFilename);
        product.setImageHash(base64Hash);

        return productRepository.save(product);
    }


    // ===========================
    // For seeding (InputStream from resources)
    // ===========================
    public Product createAndSaveProduct(String name, String category, InputStream imageStream) throws IOException {
        String uniqueFilename = UUID.randomUUID() + ".jpg";
        Files.copy(imageStream, this.rootLocation.resolve(uniqueFilename));
        BufferedImage image = ImageIO.read(this.rootLocation.resolve(uniqueFilename).toFile());
        return createProduct(name, category, uniqueFilename, image);
    }

    // Shared logic to save product
    private Product createProduct(String name, String category, String filename, BufferedImage image) {
        Hash imageHash = pHash.hash(image);
        String base64Hash = Base64.getEncoder().encodeToString(imageHash.getHashValue().toByteArray());

        Product product = new Product();
        product.setName(name);
        product.setCategory(category);
        product.setImageUrl(filename);
        product.setImageHash(base64Hash);

        return productRepository.save(product);
    }

    // Find similar products
    public List<Product> findSimilarProducts(MultipartFile file) throws IOException {
        BufferedImage uploadedImage = ImageIO.read(file.getInputStream());
        Hash uploadedHash = pHash.hash(uploadedImage);

        List<Product> allProducts = productRepository.findAll();

        return allProducts.stream()
                .peek(product -> {
                    try {
                        byte[] dbHashBytes = Base64.getDecoder().decode(product.getImageHash());
                        Hash dbHash = new Hash(new java.math.BigInteger(1, dbHashBytes), 32, 1);
                        double similarity = 100 - uploadedHash.normalizedHammingDistance(dbHash) * 100;
                        product.setSimilarityScore(similarity);
                    } catch (Exception e) {
                        product.setSimilarityScore(0);
                    }
                })
                .filter(product -> product.getSimilarityScore() > 60) // threshold
                .sorted(Comparator.comparing(Product::getSimilarityScore).reversed())
                .collect(Collectors.toList());
    }

    // Serve uploaded file
    public ResponseEntity<Resource> loadFileAsResource(String filename) {
        try {
            Path file = rootLocation.resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                String contentType = Files.probeContentType(file);
                if (contentType == null) contentType = "application/octet-stream";

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            return ResponseEntity.status(500).build();
        }
    }

    // Save uploaded file to disk
    private String saveFile(String originalFilename, InputStream inputStream) throws IOException {
        String uniqueFilename = UUID.randomUUID().toString() + "_" + originalFilename;
        Files.copy(inputStream, this.rootLocation.resolve(uniqueFilename));
        return uniqueFilename;
    }

    // For DataSeeder check
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
}
