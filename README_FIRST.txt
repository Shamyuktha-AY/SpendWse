SPENDSENSE - JAVA + MYSQL DESKTOP PROJECT
=========================================

This is a college-level Java Swing desktop application. It is NOT just a web app.
It demonstrates OOP concepts and connects to your existing MySQL database.

REQUIREMENTS
------------
1. JDK 17 or later
2. VS Code with Extension Pack for Java
3. Maven (VS Code can detect/open a Maven project)
4. MySQL Server and MySQL Workbench
5. Existing database: spendsense_db

IMPORTANT DATABASE ASSUMPTIONS
------------------------------
The app expects these existing tables/columns:
- users(user_id, username, email, password, created_at)
- categories(category_id, category_name, category_type)
- transactions(transaction_id, user_id, category_id, amount,
  transaction_type, expense_nature, description, transaction_date)

It uses user_id = 1 by default. Your sample user should be the first row
in users. If your sample user's ID is different, change USER_ID in
src/main/java/edu/spendsense/config/AppConfig.java.

DATABASE CONNECTION
-------------------
Open:
src/main/java/edu/spendsense/database/DBConnection.java

Set DB_USER and DB_PASSWORD to your own MySQL username and password.
Example:
private static final String DB_USER = "root";
private static final String DB_PASSWORD = "your_mysql_password";

Do not share your real database password in screenshots or submissions.

HOW TO OPEN IN VS CODE
---------------------
1. Extract this ZIP.
2. Open VS Code.
3. File -> Open Folder -> choose the extracted SpendSense_Java_Project folder.
4. Wait for Java and Maven dependencies to load.
5. Open DBConnection.java and set your MySQL password.
6. Make sure MySQL Server is running.
7. Open the VS Code terminal (Terminal -> New Terminal).
8. Run:
      mvn clean compile
      mvn exec:java
   If the `mvn` command is not recognized, install Maven or use the Maven
   extension's Lifecycle -> package, then run the Main class using VS Code.

WHAT THE APP DOES
-----------------
- Shows transactions stored in MySQL.
- Adds income and expense records.
- Updates a selected record.
- Deletes a selected record.
- Displays a table of transaction history.
- Uses categories already stored in the categories table.

OOP CONCEPTS FOR YOUR VIVA
--------------------------
1. Encapsulation:
   FinancialTransaction fields are private. Getters/setters control access.
2. Inheritance:
   Income and Expense extend FinancialTransaction.
3. Polymorphism:
   Both subclasses override getTransactionType(). The service works with
   the parent type and calls the overridden method.
4. Abstraction:
   FinancialTransaction is abstract. TransactionRepository is an interface
   that hides the details of database operations.
5. Packages:
   Classes are organized into model, dao, service, database, config,
   exception and ui packages.
6. Exception handling:
   InvalidAmountException handles invalid amounts. SQLException is caught
   and shown to the user. UI actions use try/catch/finally where appropriate.
7. Constructors, method overloading and overriding:
   Model classes use constructors; service has overloaded validation helpers;
   subclass methods override the abstract parent method.
8. Collections:
   ArrayList is used to hold rows read from the database.
9. JDBC:
   DriverManager, Connection, PreparedStatement and ResultSet connect Java
   to MySQL and safely execute SQL statements.
10. CRUD:
   INSERT = Create, SELECT = Read, UPDATE = Update, DELETE = Delete.

DEMO ORDER
----------
1. Run the app and show transaction records from MySQL.
2. Add a new expense and show it in Workbench.
3. Select that row, update its amount, and show the change.
4. Delete that row and show it is removed.
5. Explain the model classes and repository interface for OOP concepts.

NOTE
----
This is a working starter project, not a finished polished commercial app.
It assumes your existing database schema matches the column names above.
