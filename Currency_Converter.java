import java.sql.*;
import java.util.*;

public class Currency_Converter {
    static Scanner sc = new Scanner(System.in);
    static final String DB_URL = "jdbc:sqlite:currency_converter.db";
    static String adminId = "admin";
    static String adminPass = "admin123";

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            createTables(conn);
            insertSampleData(conn);

            while (true) {
                System.out.println("\n--- Currency Converter ---");
                System.out.println("1. Login as User");
                System.out.println("2. Login as Admin");
                System.out.println("3. Create New User");
                System.out.println("4. Exit");
                System.out.print("Choose: ");
                int ch = sc.nextInt();
                sc.nextLine();
                if (ch == 1) userLogin(conn);
                else if (ch == 2) adminLogin(conn);
                else if (ch == 3) createNewUser(conn);
                else break;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void createTables(Connection conn) throws SQLException {
        Statement st = conn.createStatement();
        st.execute("CREATE TABLE IF NOT EXISTS users (userId TEXT PRIMARY KEY, password TEXT, accountNo TEXT UNIQUE, balance REAL)");
        st.execute("CREATE TABLE IF NOT EXISTS rates (currency TEXT PRIMARY KEY, rate REAL)");
        st.execute("CREATE TABLE IF NOT EXISTS transactions (id INTEGER PRIMARY KEY AUTOINCREMENT, userId TEXT, details TEXT, time TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
    }

    static void insertSampleData(Connection conn) throws SQLException {
        // Insert sample users if not exists
        PreparedStatement ps = conn.prepareStatement("INSERT OR IGNORE INTO users (userId, password, accountNo, balance) VALUES (?, ?, ?, ?)");
        ps.setString(1, "user1"); ps.setString(2, "pass1"); ps.setString(3, "1001"); ps.setDouble(4, 1000.0); ps.executeUpdate();
        ps.setString(1, "user2"); ps.setString(2, "pass2"); ps.setString(3, "1002"); ps.setDouble(4, 2000.0); ps.executeUpdate();

        // Insert sample rates if not exists
        PreparedStatement psr = conn.prepareStatement("INSERT OR IGNORE INTO rates (currency, rate) VALUES (?, ?)");
        insertRate(psr, "INR", 1.0);
        insertRate(psr, "USD", 0.012);
        insertRate(psr, "EUR", 0.011);
        insertRate(psr, "GBP", 0.0095);
        insertRate(psr, "JPY", 1.7);
    }

    static void insertRate(PreparedStatement psr, String code, double rate) throws SQLException {
        psr.setString(1, code); psr.setDouble(2, rate); psr.executeUpdate();
    }

    static void userLogin(Connection conn) throws SQLException {
        System.out.print("User ID: ");
        String uid = sc.nextLine();
        System.out.print("Password: ");
        String pass = sc.nextLine();
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE userId=? AND password=?");
        ps.setString(1, uid); ps.setString(2, pass);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            System.out.println("Login successful!");
            userMenu(conn, uid);
        } else {
            System.out.println("Invalid credentials.");
        }
    }

    static void createNewUser(Connection conn) throws SQLException {
        System.out.print("Choose a User ID: ");
        String uid = sc.nextLine();
        System.out.print("Choose a Password: ");
        String pass = sc.nextLine();
        String accNo = String.valueOf(1000 + new Random().nextInt(9000));
        System.out.print("Enter initial deposit (INR): ");
        double bal = sc.nextDouble();
        sc.nextLine();
        PreparedStatement ps = conn.prepareStatement("INSERT OR IGNORE INTO users (userId, password, accountNo, balance) VALUES (?, ?, ?, ?)");
        ps.setString(1, uid);
        ps.setString(2, pass);
        ps.setString(3, accNo);
        ps.setDouble(4, bal);
        int rows = ps.executeUpdate();
        if (rows > 0) {
            System.out.println("User created! Your account number is: " + accNo);
        } else {
            System.out.println("User ID already exists. Try again.");
        }
    }

    static void userMenu(Connection conn, String uid) throws SQLException {
        while (true) {
            System.out.println("\nWelcome, " + uid);
            System.out.println("1. View Balance");
            System.out.println("2. Convert Currency");
            System.out.println("3. Transfer Money");
            System.out.println("4. View Exchange History");
            System.out.println("5. Add Money to Balance");
            System.out.println("6. Check Conversion (without deducting)");
            System.out.println("7. Logout");
            System.out.println("8. Show Currency Conversion Chart");
            System.out.print("Choose: ");
            int ch = sc.nextInt();
            sc.nextLine();
            if (ch == 1) {
                PreparedStatement ps = conn.prepareStatement("SELECT accountNo, balance FROM users WHERE userId=?");
                ps.setString(1, uid);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    System.out.printf("Account No: %s | Balance: %.2f INR\n", rs.getString("accountNo"), rs.getDouble("balance"));
                }
            } else if (ch == 2) {
                convertCurrency(conn, uid);
            } else if (ch == 3) {
                transferMoney(conn, uid);
            } else if (ch == 4) {
                showHistory(conn, uid);
            } else if (ch == 5) {
                addMoney(conn, uid);
            } else if (ch == 6) {
                checkConversion(conn, uid);
            } else if (ch == 8) {
                showConversionChart(conn);
            } else break;
        }
    }

    static void convertCurrency(Connection conn, String uid) throws SQLException {
        System.out.print("Enter amount in INR: ");
        double amount = sc.nextDouble();
        sc.nextLine();
        PreparedStatement psb = conn.prepareStatement("SELECT balance FROM users WHERE userId=?");
        psb.setString(1, uid);
        ResultSet rsb = psb.executeQuery();
        if (!rsb.next() || amount > rsb.getDouble("balance")) {
            System.out.println("Insufficient balance.");
            return;
        }
        System.out.print("Convert to (USD/EUR/GBP/JPY): ");
        String to = sc.nextLine().toUpperCase();
        PreparedStatement psr = conn.prepareStatement("SELECT rate FROM rates WHERE currency=?");
        psr.setString(1, to);
        ResultSet rsr = psr.executeQuery();
        if (!rsr.next()) {
            System.out.println("Unsupported currency.");
            return;
        }
        double rate = rsr.getDouble("rate");
        double converted = amount * rate;
        // Deduct balance
        PreparedStatement upd = conn.prepareStatement("UPDATE users SET balance = balance - ? WHERE userId=?");
        upd.setDouble(1, amount); upd.setString(2, uid); upd.executeUpdate();
        // Add transaction
        String entry = String.format("Converted %.2f INR to %.2f %s", amount, converted, to);
        PreparedStatement pst = conn.prepareStatement("INSERT INTO transactions (userId, details) VALUES (?, ?)");
        pst.setString(1, uid); pst.setString(2, entry); pst.executeUpdate();
        System.out.println(entry);
        // Show new balance
        ResultSet rsb2 = psb.executeQuery();
        if (rsb2.next()) {
            System.out.printf("New Balance: %.2f INR\n", rsb2.getDouble("balance") - amount);
        }
    }

    static void transferMoney(Connection conn, String uid) throws SQLException {
        System.out.print("Enter recipient account no: ");
        String acc = sc.nextLine();
        PreparedStatement psr = conn.prepareStatement("SELECT userId FROM users WHERE accountNo=?");
        psr.setString(1, acc);
        ResultSet rsr = psr.executeQuery();
        if (!rsr.next()) {
            System.out.println("Recipient not found.");
            return;
        }
        String recipientId = rsr.getString("userId");
        System.out.print("Enter amount to transfer (INR): ");
        double amt = sc.nextDouble();
        sc.nextLine();
        PreparedStatement psb = conn.prepareStatement("SELECT balance FROM users WHERE userId=?");
        psb.setString(1, uid);
        ResultSet rsb = psb.executeQuery();
        if (!rsb.next()) {
            System.out.println("User not found.");
            return;
        }
        double currentBalance = rsb.getDouble("balance");
        if (amt > currentBalance || amt <= 0) {
            System.out.println("Invalid money transfer. Insufficient balance or invalid amount.");
            return;
        }
        // Deduct from sender
        PreparedStatement updSender = conn.prepareStatement("UPDATE users SET balance = balance - ? WHERE userId=?");
        updSender.setDouble(1, amt); updSender.setString(2, uid); updSender.executeUpdate();
        // Add to recipient
        PreparedStatement updRec = conn.prepareStatement("UPDATE users SET balance = balance + ? WHERE userId=?");
        updRec.setDouble(1, amt); updRec.setString(2, recipientId); updRec.executeUpdate();
        // Add transaction for both
        String entry = String.format("Transferred %.2f INR to %s (Acc: %s)", amt, recipientId, acc);
        PreparedStatement pst = conn.prepareStatement("INSERT INTO transactions (userId, details) VALUES (?, ?)");
        pst.setString(1, uid); pst.setString(2, entry); pst.executeUpdate();
        String entry2 = String.format("Received %.2f INR from %s", amt, uid);
        pst.setString(1, recipientId); pst.setString(2, entry2); pst.executeUpdate();
        System.out.println(entry);
        // Show new balance
        PreparedStatement psb2 = conn.prepareStatement("SELECT balance FROM users WHERE userId=?");
        psb2.setString(1, uid);
        ResultSet rsb2 = psb2.executeQuery();
        if (rsb2.next()) {
            System.out.printf("Your New Balance: %.2f INR\n", rsb2.getDouble("balance"));
        }
    }

    static void showHistory(Connection conn, String uid) throws SQLException {
        System.out.println("--- Exchange & Transfer History ---");
        PreparedStatement ps = conn.prepareStatement("SELECT details, time FROM transactions WHERE userId=? ORDER BY time DESC");
        ps.setString(1, uid);
        ResultSet rs = ps.executeQuery();
        boolean found = false;
        while (rs.next()) {
            found = true;
            System.out.println(rs.getString("details") + " [" + rs.getString("time") + "]");
        }
        if (!found) System.out.println("No transactions yet.");
    }

    static void adminLogin(Connection conn) throws SQLException {
        System.out.print("Admin ID: ");
        String aid = sc.nextLine();
        System.out.print("Password: ");
        String pass = sc.nextLine();
        if (adminId.equals(aid) && adminPass.equals(pass)) {
            System.out.println("Admin login successful!");
            adminMenu(conn);
        } else {
            System.out.println("Invalid admin credentials.");
        }
    }

    static void adminMenu(Connection conn) throws SQLException {
        while (true) {
            System.out.println("\n--- Admin Menu ---");
            System.out.println("1. View All Users");
            System.out.println("2. View All Exchange Rates / Conversion Chart");
            System.out.println("3. Update Exchange Rate");
            System.out.println("4. Remove User");
            System.out.println("5. Logout");
            System.out.print("Choose: ");
            int ch = sc.nextInt();
            sc.nextLine();
            if (ch == 1) {
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery("SELECT userId, accountNo, balance FROM users");
                while (rs.next()) {
                    System.out.printf("User: %s | Account: %s | Balance: %.2f INR\n", rs.getString("userId"), rs.getString("accountNo"), rs.getDouble("balance"));
                }
            } else if (ch == 2) {
                showConversionChart(conn);
            } else if (ch == 3) {
                System.out.print("Currency code (e.g., USD): ");
                String code = sc.nextLine().toUpperCase();
                PreparedStatement ps = conn.prepareStatement("SELECT rate FROM rates WHERE currency=?");
                ps.setString(1, code);
                ResultSet rs = ps.executeQuery();
                if (!rs.next()) {
                    System.out.println("Currency not found.");
                    continue;
                }
                System.out.print("New rate (1 INR = ? " + code + "): ");
                double rate = sc.nextDouble();
                sc.nextLine();
                PreparedStatement upd = conn.prepareStatement("UPDATE rates SET rate=? WHERE currency=?");
                upd.setDouble(1, rate); upd.setString(2, code); upd.executeUpdate();
                System.out.println("Rate updated.");
            } else if (ch == 4) {
                removeUser(conn);
            } else break;
        }
    }

    static void removeUser(Connection conn) throws SQLException {
        System.out.print("Enter User ID to remove: ");
        String uid = sc.nextLine();
        if (uid.equals(adminId)) {
            System.out.println("Cannot remove admin user.");
            return;
        }
        // Remove user's transactions first (to maintain referential integrity)
        PreparedStatement pst1 = conn.prepareStatement("DELETE FROM transactions WHERE userId=?");
        pst1.setString(1, uid);
        pst1.executeUpdate();
        // Remove user
        PreparedStatement pst2 = conn.prepareStatement("DELETE FROM users WHERE userId=?");
        pst2.setString(1, uid);
        int rows = pst2.executeUpdate();
        if (rows > 0) {
            System.out.println("User '" + uid + "' removed successfully.");
        } else {
            System.out.println("User not found.");
        }
    }

    static void addMoney(Connection conn, String uid) throws SQLException {
        System.out.print("Enter amount to add (INR): ");
        double amt = sc.nextDouble();
        sc.nextLine();
        PreparedStatement upd = conn.prepareStatement("UPDATE users SET balance = balance + ? WHERE userId=?");
        upd.setDouble(1, amt);
        upd.setString(2, uid);
        upd.executeUpdate();
        System.out.println("Amount added to your balance.");
        PreparedStatement pst = conn.prepareStatement("INSERT INTO transactions (userId, details) VALUES (?, ?)");
        pst.setString(1, uid);
        pst.setString(2, "Added " + amt + " INR to balance");
        pst.executeUpdate();
    }

    static void checkConversion(Connection conn, String uid) throws SQLException {
        System.out.print("Enter amount in INR: ");
        double amount = sc.nextDouble();
        sc.nextLine();
        System.out.print("Convert to (USD/EUR/GBP/JPY): ");
        String to = sc.nextLine().toUpperCase();
        PreparedStatement psr = conn.prepareStatement("SELECT rate FROM rates WHERE currency=?");
        psr.setString(1, to);
        ResultSet rsr = psr.executeQuery();
        if (!rsr.next()) {
            System.out.println("Unsupported currency.");
            return;
        }
        double rate = rsr.getDouble("rate");
        double converted = amount * rate;
        System.out.printf("You will get %.2f %s for %.2f INR\n", converted, to, amount);
    }

    static void showConversionChart(Connection conn) throws SQLException {
        System.out.println("\n--- Currency Conversion Chart (1 INR = ?) ---");
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT currency, rate FROM rates");
        while (rs.next()) {
            System.out.printf("%-5s : %.4f\n", rs.getString("currency"), rs.getDouble("rate"));
        }
    }
}