package atmproject;

import java.sql.*;
import java.util.HashMap;

public class UserDataStore {

    public static HashMap<Integer, User> loadUsers() {
        HashMap<Integer, User> users = new HashMap<>();
        try (Connection con = DBUtil.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM users")) {

            while (rs.next()) {
                User u = new User(rs.getInt("customerNumber"), rs.getInt("pinNumber"));
                u.getCheckingAccount().balance = rs.getDouble("checkingBalance");
                u.getSavingAccount().balance = rs.getDouble("savingBalance");
                users.put(u.getCustomerNumber(), u);
            }
        } catch (Exception e) {
            System.out.println("⚠️ Error loading users: " + e.getMessage());
        }
        return users;
    }

    public static void saveUser(User u) {
        try (Connection con = DBUtil.getConnection()) {
            String sql = """
                INSERT INTO users(customerNumber, pinNumber, checkingBalance, savingBalance)
                VALUES (?, ?, ?, ?)
                ON CONFLICT (customerNumber)
                DO UPDATE SET
                    pinNumber = EXCLUDED.pinNumber,
                    checkingBalance = EXCLUDED.checkingBalance,
                    savingBalance = EXCLUDED.savingBalance
                """;

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, u.getCustomerNumber());
            ps.setInt(2, u.getPinNumber());
            ps.setDouble(3, u.getCheckingAccount().getBalance());
            ps.setDouble(4, u.getSavingAccount().getBalance());
            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("⚠️ Error saving user: " + e.getMessage());
        }
    }

    public static void saveUsers(HashMap<Integer, User> users) {
        for (User u : users.values()) {
            saveUser(u); // reuse existing method
        }
    }


    public static void logTransaction(int customerNumber, String type, double amount) {
        try (Connection con = DBUtil.getConnection()) {
            String sql = "INSERT INTO transactions(customerNumber,type,amount) VALUES (?,?,?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, customerNumber);
            ps.setString(2, type);
            ps.setDouble(3, amount);
            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("⚠️ Error logging transaction: " + e.getMessage());
        }
    }

    public static boolean createUser(int customerNumber, int pin) {
        try (Connection con = DBUtil.getConnection()) {
            String sql = "INSERT INTO users(customerNumber,pinNumber,checkingBalance,savingBalance) VALUES (?,?,0,0)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, customerNumber);
            ps.setInt(2, pin);
            ps.executeUpdate();
            return true;
        } catch (SQLIntegrityConstraintViolationException dup) {
            return false;
        } catch (Exception e) {
            System.out.println("⚠️ Error creating user: " + e.getMessage());
            return false;
        }
    }
}
