# Currency Converter (Java + SQLite)

A simple Java console application for currency conversion, user account management, and transaction history, using SQLite as the backend database.

## Features

- **User Registration:** Create a new user with a randomly assigned account number.
- **User Login:** Secure login with user ID and password.
- **Admin Login:** Admin can manage users and currency rates.
- **Balance Management:** View, add, and transfer money between users.
- **Currency Conversion:** Convert INR to USD, EUR, GBP, or JPY at current rates.
- **Conversion Chart:** View all available currency rates.
- **Transaction History:** See all your conversions and transfers.
- **Admin Controls:**
  - View all users and their balances.
  - View and update currency rates.
  - Remove users from the system.

## Getting Started

### Prerequisites

- **Java JDK** (version 8 or above)
- **SQLite JDBC Driver**  
  Download from: [https://github.com/xerial/sqlite-jdbc/releases](https://github.com/xerial/sqlite-jdbc/releases)

### Setup

1. **Clone or Download** this repository.
2. **Place the SQLite JDBC JAR** (e.g., `sqlite-jdbc-3.45.3.0.jar`) in your project folder.

### Compile

```sh
javac -cp ".;sqlite-jdbc-3.45.3.0.jar" Currency_Converter.java
```

### Run

```sh
java -cp ".;sqlite-jdbc-3.45.3.0.jar" Currency_Converter
```

*(On Mac/Linux, use `:` instead of `;` in the classpath.)*

## Usage

### Main Menu

- **1. Login as User:** Enter your user ID and password.
- **2. Login as Admin:** Enter admin credentials (default: `admin` / `admin123`).
- **3. Create New User:** Register a new user with a random account number.
- **4. Exit:** Quit the application.

### User Menu

- **View Balance:** See your account number and INR balance.
- **Convert Currency:** Convert INR to USD, EUR, GBP, or JPY.
- **Transfer Money:** Send INR to another user by account number.
- **View Exchange History:** See your conversion and transfer history.
- **Add Money to Balance:** Deposit more INR into your account.
- **Check Conversion:** See how much you’d get for a given INR amount (without deducting).
- **Show Currency Conversion Chart:** View all available currency rates.
- **Logout:** Return to the main menu.

### Admin Menu

- **View All Users:** List all users and their balances.
- **View All Exchange Rates / Conversion Chart:** See all currency rates.
- **Update Exchange Rate:** Change the conversion rate for any currency.
- **Remove User:** Delete a user and their transaction history.
- **Logout:** Return to the main menu.

## Database

- All data is stored in `currency_converter.db` (created automatically).
- Tables: `users`, `rates`, `transactions`.

## Notes

- All actions are persistent; your data remains after restarting the app.
- Admin cannot be removed from the system.
- Currency rates are relative to INR (1 INR = ?).

## License

This project is for educational purposes.

---
```# Currency Converter (Java + SQLite)

A simple Java console application for currency conversion, user account management, and transaction history, using SQLite as the backend database.

## Features

- **User Registration:** Create a new user with a randomly assigned account number.
- **User Login:** Secure login with user ID and password.
- **Admin Login:** Admin can manage users and currency rates.
- **Balance Management:** View, add, and transfer money between users.
- **Currency Conversion:** Convert INR to USD, EUR, GBP, or JPY at current rates.
- **Conversion Chart:** View all available currency rates.
- **Transaction History:** See all your conversions and transfers.
- **Admin Controls:**
  - View all users and their balances.
  - View and update currency rates.
  - Remove users from the system.

## Getting Started

### Prerequisites

- **Java JDK** (version 8 or above)
- **SQLite JDBC Driver**  
  Download from: [https://github.com/xerial/sqlite-jdbc/releases](https://github.com/xerial/sqlite-jdbc/releases)

### Setup

1. **Clone or Download** this repository.
2. **Place the SQLite JDBC JAR** (e.g., `sqlite-jdbc-3.45.3.0.jar`) in your project folder.

### Compile

```sh
javac -cp ".;sqlite-jdbc-3.45.3.0.jar" Currency_Converter.java
```

### Run

```sh
java -cp ".;sqlite-jdbc-3.45.3.0.jar" Currency_Converter
```

*(On Mac/Linux, use `:` instead of `;` in the classpath.)*

## Usage

### Main Menu

- **1. Login as User:** Enter your user ID and password.
- **2. Login as Admin:** Enter admin credentials (default: `admin` / `admin123`).
- **3. Create New User:** Register a new user with a random account number.
- **4. Exit:** Quit the application.

### User Menu

- **View Balance:** See your account number and INR balance.
- **Convert Currency:** Convert INR to USD, EUR, GBP, or JPY.
- **Transfer Money:** Send INR to another user by account number.
- **View Exchange History:** See your conversion and transfer history.
- **Add Money to Balance:** Deposit more INR into your account.
- **Check Conversion:** See how much you’d get for a given INR amount (without deducting).
- **Show Currency Conversion Chart:** View all available currency rates.
- **Logout:** Return to the main menu.

### Admin Menu

- **View All Users:** List all users and their balances.
- **View All Exchange Rates / Conversion Chart:** See all currency rates.
- **Update Exchange Rate:** Change the conversion rate for any currency.
- **Remove User:** Delete a user and their transaction history.
- **Logout:** Return to the main menu.

## Database

- All data is stored in `currency_converter.db` (created automatically).
- Tables: `users`, `rates`, `transactions`.

## Notes

- All actions are persistent; your data remains after restarting the app.
- Admin cannot be removed from the system.
- Currency rates are relative to INR (1 INR = ?).