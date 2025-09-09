# 🍽️ FoodWebsite

A **Spring Boot-based Food Ordering Web Application** with user and admin roles, authentication, and a Thymeleaf-powered frontend.

---

## 🛠 Project Configuration & Build Files

| File                    | Description                                                                 |
|-------------------------|-----------------------------------------------------------------------------|
| `.gitattributes`        | Git configuration for consistent file handling across different operating systems. |
| `.gitignore`            | Lists files/folders ignored by Git (e.g., build outputs, IDE configs).      |
| `pom.xml`               | Maven project descriptor — defines dependencies, plugins, and build configuration. |
| `mvnw` & `mvnw.cmd`     | Maven wrapper scripts for Unix and Windows. Allow running Maven without requiring it installed globally. |
| `.mvn/wrapper/maven-wrapper.properties` | Configuration for the Maven wrapper, including Maven version. |
| `HELP.md`               | Spring Boot project help reference generated initially by Spring Initializr. |
| `.idea/*`               | IntelliJ IDEA project settings (not required for deployment).               |
| `app.log`               | Application log file (runtime logs).                                        |

---

## 🖥 Backend – Java Source Code

| File                                                      | Description                                                                 |
|-----------------------------------------------------------|-----------------------------------------------------------------------------|
| `src/main/java/com/example/FoodWebsite/FoodWebsiteApplication.java` | Main Spring Boot application entry point. Initializes the application. |
| `controller/WebController.java`                           | Central controller handling HTTP requests, routing between user and admin dashboards, authentication, and food item operations. |
| `model/Admin.java`                                        | Represents admin entity (fields like username, password, role).             |
| `model/User.java`                                         | Represents a user entity with attributes for login and account management.  |
| `model/FoodItem.java`                                     | Represents a food item entity with fields like name, price, description, quantity. |
| `repository/UserRepository.java`                          | Spring Data JPA repository interface for managing `User` entities.          |
| `repository/FoodItemRepository.java`                      | Spring Data JPA repository interface for managing `FoodItem` entities.      |

---

## ⚙️ Configuration & Resources

| File                                    | Description                                   |
|----------------------------------------|-----------------------------------------------|
| `src/main/resources/application.properties` | Application configuration — database connection, server port, and other Spring Boot properties. |

---

## 🌐 Frontend Templates (Thymeleaf)

| File                                 | Description                                  |
|------------------------------------|----------------------------------------------|
| `templates/index.html`              | Landing page / Home page of the food ordering application. |
| `templates/login.html`              | User login page for customers.               |
| `templates/register.html`           | User registration page.                      |
| `templates/user-dashboard.html`     | User dashboard after successful login — browse food items, add to cart, etc. |
| `templates/my-account.html`         | User account page — view/update personal details. |
| `templates/admin-login.html`        | Admin login page.                            |
| `templates/admin-dashboard.html`    | Admin panel — overview of operations.        |
| `templates/manage-food-items.html`  | Admin interface to create, update, and delete food items. |
| `templates/manage-users.html`       | Admin interface to manage registered users.   |

---

## 🧪 Testing

| File                                                   | Description                                   |
|--------------------------------------------------------|-----------------------------------------------|
| `src/test/java/com/example/FoodWebsite/FoodWebsiteApplicationTests.java` | Basic Spring Boot test class — validates application context loading. |

---

## 📦 Build Outputs

| File / Folder           | Description                                         |
|-------------------------|-----------------------------------------------------|
| `target/classes/...`    | Compiled Java classes and packaged templates.        |
| `target/test-classes/...` | Compiled test classes.                             |

---

## 🚀 How It Works

1. **Users** can register, log in, view available food items, and manage their account.
2. **Admins** can log in separately, manage food items, and oversee users.
3. **Spring Boot** handles server logic, **Spring Data JPA** manages database interactions, and **Thymeleaf** renders dynamic pages.
