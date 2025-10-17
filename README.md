# Visual Product Matcher

This is a full-stack web application built with Java and Spring Boot that allows users to find visually similar products from a catalog by uploading an image or providing an image URL. This project was built as a practical technical assessment for a Software Engineer position.

---

### Features 

* **Dual-Mode Search**: Supports searching via both local file uploads and direct image URLs.
* **Visual Analysis**: The backend analyzes images to generate a perceptual hash (a digital "fingerprint") representing its visual features.
* **Database Matching**: Compares the uploaded image's hash against a catalog of products stored in a PostgreSQL database to find matches.
* **Dynamic Results**: Displays a list of similar products, sorted by their calculated similarity score.
* **Automatic Data Seeding**: On first launch, the application automatically populates the database with 50+ sample products.
* **Responsive Frontend**: A clean interface built with vanilla HTML, CSS, and JavaScript that works on both desktop and mobile.

---

### Tech Stack 

* **Backend**: Java 17, Spring Boot, Spring Data JPA
* **Database**: PostgreSQL
* **Image Analysis**: JImageHash library for perceptual hashing
* **Frontend**: HTML5, CSS3, JavaScript (ES6+)
* **Build Tool**: Apache Maven

---

### Getting Started Locally 

To run this project on your local machine, follow these steps:

1.  **Prerequisites**: Make sure you have **Java 17**, **Apache Maven**, and **PostgreSQL** installed and running on your system.

2.  **Clone the repository**:
    ```bash
    git clone <your-repository-url>
    cd visual-product-matcher
    ```

3.  **Database Setup**:
    * Open `psql` or your preferred PostgreSQL client.
    * Create a new database named `visualmatcher`.
        ```sql
        CREATE DATABASE visualmatcher;
        ```

4.  **Configure Application**:
    * Open the file `src/main/resources/application.properties`.
    * Update the `spring.datasource.password` with your PostgreSQL password.

5.  **Build and Run the Project**: Use Maven to compile and run the application. The first time it runs, it will seed the database with sample products.
    ```bash
    mvn spring-boot:run
    ```

6.  **Open the Application**: Open `http://localhost:8080` in your web browser.

---

### Project Approach

The application follows a classic and robust client-server architecture.

The core of the project is a **Spring Boot REST API**, which handles all business logic. This choice provides a powerful, industry-standard foundation for building scalable web services. For the critical visual search functionality, the backend uses an image hashing library to perform perceptual hashing directly within the Java application. When a user provides an image, its hash is calculated and compared against pre-computed hashes stored in the **PostgreSQL** database.

The frontend is a lightweight, dependency-free application built with standard **HTML, CSS, and JavaScript**. It communicates asynchronously with the backend using `fetch` requests, providing a smooth single-page application experience with clear loading states and error handling, without the overhead of a large framework. This decoupled approach ensures a clean separation of concerns between the presentation and logic layers.
