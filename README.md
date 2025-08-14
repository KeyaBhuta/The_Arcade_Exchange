# Arcade Exchange Management System 

A desktop application for managing an "Arcade Exchange" store, built with **Java Swing** for the graphical user interface (GUI) and **JDBC** for database connectivity. The application features a retro, neon-themed UI and allows an administrator to perform various CRUD (Create, Read, Update, Delete) operations on customers, games, purchases, and reviews.


*(**Note:** Replace the image above with a real screenshot of your application!)*

---

## Table of Contents

- [Key Features](#-key-features)
- [Tech Stack](#-tech-stack)
- [Database Schema](#-database-schema)
  - [Tables](#tables)
  - [Triggers](#triggers)
- [Getting Started](#-getting-started)
  - [Prerequisites](#prerequisites)
  - [1. Database Setup](#1-database-setup)
  - [2. Project Setup](#2-project-setup)
  - [3. Configure Database Connection](#3-configure-database-connection)
- [How to Run](#-how-to-run)
- [Future Improvements](#-future-improvements)

---

## Key Features

-   **Retro-Styled GUI:** A visually appealing interface built with Java Swing, featuring a dark theme, neon colors, and custom fonts to evoke a classic arcade feel.
-   **Customer Management:** Add new customers and view a list of all existing customers.
-   **Game Inventory Management:** Add new games to the inventory and view all available games.
-   **Transaction Processing:**
    -   Record a new purchase, automatically using the game's current price.
    -   Generate a complete bill for a specific customer, detailing all their purchases and the total amount due.
-   **Review System:** Add and view customer reviews for games, including a rating and text.
-   **Comprehensive Data Views:** Easily display all data from the `customer`, `games`, `purchase`, `review`, and `transaction` tables in formatted JTables.
-   **Database Integration:** Directly connects to a MySQL database to persist all application data.

---

## Tech Stack

-   **Programming Language:** Java
-   **GUI Framework:** Java Swing
-   **Database Connectivity:** JDBC (Java Database Connectivity)
-   **Database:** MySQL

---

## Database Schema

The application relies on a MySQL database named `arcade_exchange_db`. Below is the required schema, including tables and triggers for automatic data management.

### Tables
You need to create the following tables in your database:

```sql
CREATE DATABASE IF NOT EXISTS arcade_exchange_db;
USE arcade_exchange_db;

CREATE TABLE customer (
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE,
    phone_number VARCHAR(20)
);

CREATE TABLE games (
    game_id INT AUTO_INCREMENT PRIMARY KEY,
    original_price DECIMAL(10, 2) NOT NULL,
    purchase_count INT DEFAULT 0,
    real_time_price DECIMAL(10, 2) NOT NULL
);

CREATE TABLE purchase (
    purchase_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT,
    game_id INT,
    purchase_price DECIMAL(10, 2),
    purchase_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customer(customer_id),
    FOREIGN KEY (game_id) REFERENCES games(game_id)
);

CREATE TABLE review (
    review_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT,
    game_id INT,
    rating INT CHECK (rating >= 1 AND rating <= 5),
    review TEXT,
    review_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customer(customer_id),
    FOREIGN KEY (game_id) REFERENCES games(game_id)
);

CREATE TABLE transaction (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    purchase_id INT,
    customer_id INT,
    game_id INT,
    amount DECIMAL(10, 2),
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (purchase_id) REFERENCES purchase(purchase_id)
);
```


---
### Triggers

These triggers automate important business logic directly within the database.

1.  **Update Game Price and Count on Purchase:**
    This trigger automatically increases a game's purchase count and its real-time price after each sale.

    ```sql
    DELIMITER $$
    CREATE TRIGGER after_purchase_trigger
    AFTER INSERT ON purchase
    FOR EACH ROW
    BEGIN
        -- Increment the purchase count for the game
        UPDATE games
        SET purchase_count = purchase_count + 1
        WHERE game_id = NEW.game_id;

        -- Increase the real-time price by 10% (as an example)
        UPDATE games
        SET real_time_price = real_time_price * 1.10
        WHERE game_id = NEW.game_id;
    END$$
    DELIMITER ;
    ```

2.  **Log Transaction on Purchase:**
    This trigger creates a record in the `transaction` table every time a new purchase is made.

    ```sql
    DELIMITER $$
    CREATE TRIGGER after_purchase_log_transaction
    AFTER INSERT ON purchase
    FOR EACH ROW
    BEGIN
        INSERT INTO transaction (purchase_id, customer_id, game_id, amount, transaction_date)
        VALUES (NEW.purchase_id, NEW.customer_id, NEW.game_id, NEW.purchase_price, NEW.purchase_date);
    END$$
    DELIMITER ;
    ```


---

## Getting Started

Follow these steps to get the project running on your local machine.

### Prerequisites

-   Java Development Kit (JDK) 8 or higher.
-   MySQL Server installed and running.
-   An IDE like IntelliJ IDEA, Eclipse, or VS Code.
-   **MySQL Connector/J:** The JDBC driver for MySQL. [Download the JAR file here](https://dev.mysql.com/downloads/connector/j/).

### 1. Database Setup

1.  Open your MySQL client (e.g., MySQL Workbench, DBeaver, or the command line).
2.  Execute the SQL scripts from the **Database Schema** section above to create the database, tables, and triggers.

### 2. Project Setup

1.  **Clone the repository:**
    ```sh
    git clone https://github.com/your-username/arcade-exchange-system.git
    cd arcade-exchange-system
    ```
2.  **Open the project in your IDE.**
3.  **Add the MySQL Connector/J to your project's build path:**
    -   **IntelliJ IDEA:** Go to `File` > `Project Structure` > `Modules` > `Dependencies`. Click the `+` icon, select `JARs or directories`, and navigate to the downloaded `mysql-connector-j-x.x.x.jar` file.
    -   **Eclipse:** Right-click the project in the Project Explorer, go to `Build Path` > `Configure Build Path`. In the `Libraries` tab, click `Add External JARs...` and select the connector JAR.

### 3. Configure Database Connection

**IMPORTANT:** The database credentials are hard-coded in the source file for simplicity. You must update them to match your local MySQL setup.

Open the `Main.java` file and modify these lines:

```java
// mini_proj_DBMS/Main.java

// ... inside the Main class
static final String DB_URL = "jdbc:mysql://localhost:3306/arcade_exchange_db";
static final String USER = "your_mysql_username"; // Change this
static final String PASS = "your_mysql_password"; // Change this

## How to Run

After completing the setup:
1.  Navigate to the `Main.java` file in your IDE.
2.  Right-click the file and select `Run 'Main.main()'`.
3.  The application window should appear.
```

---

## Future Improvements

-   **Refactor Database Credentials:** Move hard-coded credentials out of the source code and into a separate `config.properties` file for better security and configuration management.
-   **Error Handling:** Improve user-facing error messages for database and input validation errors.
-   **Update and Delete Functionality:** Implement UI components and logic to allow updating and deleting records (e.g., edit a customer's details or remove a game).
-   **Search/Filter:** Add search bars to allow filtering the data tables (e.g., find a customer by name).
-   **Maven/Gradle Integration:** Convert the project to use a build automation tool like Maven or Gradle to manage dependencies automatically.
