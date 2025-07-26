package atmproject;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class ATMGui {
    private JFrame frame;
    private HashMap<Integer, User> users;

    public ATMGui() {
        users = UserDataStore.loadUsers();
        createLoginScreen();
    }

    private void createLoginScreen() {
        frame = new JFrame("ATM Login");
        frame.setSize(400, 250);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); // ✅ center on screen
        frame.setLayout(null);

        JLabel lblCust = new JLabel("Customer Number:");
        lblCust.setBounds(30, 40, 150, 25);
        JTextField txtCust = new JTextField();
        txtCust.setBounds(180, 40, 150, 25);

        JLabel lblPin = new JLabel("PIN:");
        lblPin.setBounds(30, 80, 150, 25);
        JPasswordField txtPin = new JPasswordField();
        txtPin.setBounds(180, 80, 150, 25);

        JButton loginBtn = new JButton("Login");
        loginBtn.setBounds(70, 130, 100, 30);
        JButton signupBtn = new JButton("Sign Up");
        signupBtn.setBounds(200, 130, 100, 30);

        frame.add(lblCust);
        frame.add(txtCust);
        frame.add(lblPin);
        frame.add(txtPin);
        frame.add(loginBtn);
        frame.add(signupBtn);

        loginBtn.addActionListener(e -> {
            try {
                int custNum = Integer.parseInt(txtCust.getText().trim());
                int pin = Integer.parseInt(new String(txtPin.getPassword()));
                if (users.containsKey(custNum) && users.get(custNum).getPinNumber() == pin) {
                    JOptionPane.showMessageDialog(frame, "✅ Login Successful!");
                    frame.dispose();
                    openMainMenu(users.get(custNum));
                } else {
                    JOptionPane.showMessageDialog(frame, "❌ Invalid credentials");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "❌ Please enter valid numbers");
            }
        });

        signupBtn.addActionListener(e -> {
            String cStr = JOptionPane.showInputDialog(frame, "Enter new Customer Number:");
            String pStr = JOptionPane.showInputDialog(frame, "Enter PIN:");
            try {
                int cNum = Integer.parseInt(cStr.trim());
                int pNum = Integer.parseInt(pStr.trim());
                if (UserDataStore.createUser(cNum, pNum)) {
                    users = UserDataStore.loadUsers();
                    JOptionPane.showMessageDialog(frame, "✅ Account created!");
                } else {
                    JOptionPane.showMessageDialog(frame, "❌ Customer Number already exists.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "❌ Invalid input.");
            }
        });

        frame.setVisible(true);
    }

    private void openMainMenu(User user) {
        JFrame menuFrame = new JFrame("ATM Menu");
        menuFrame.setSize(400, 400);
        menuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        menuFrame.setLocationRelativeTo(null); // ✅ center on screen
        menuFrame.setLayout(new GridLayout(6, 1, 10, 10));

        JButton viewBalanceBtn = new JButton("View Checking Balance");
        JButton depositBtn = new JButton("Deposit");
        JButton withdrawBtn = new JButton("Withdraw");
        JButton miniStatementBtn = new JButton("Mini Statement");
        JButton transferBtn = new JButton("Transfer Funds");
        JButton exitBtn = new JButton("Exit");

        Font btnFont = new Font("Arial", Font.BOLD, 16);
        for (JButton b : new JButton[]{viewBalanceBtn, depositBtn, withdrawBtn, miniStatementBtn, transferBtn, exitBtn}) {
            b.setFont(btnFont);
        }

        menuFrame.add(viewBalanceBtn);
        menuFrame.add(depositBtn);
        menuFrame.add(withdrawBtn);
        menuFrame.add(miniStatementBtn);
        menuFrame.add(transferBtn);
        menuFrame.add(exitBtn);

        viewBalanceBtn.addActionListener(e ->
            JOptionPane.showMessageDialog(menuFrame, "Balance: ₹" + user.getCheckingAccount().getBalance()));

        depositBtn.addActionListener(e -> {
            String amt = JOptionPane.showInputDialog(menuFrame, "Enter amount to deposit:");
            try {
                double val = Double.parseDouble(amt);
                user.getCheckingAccount().deposit(val);
                UserDataStore.logTransaction(user.getCustomerNumber(), "DEPOSIT", val);
                UserDataStore.saveUser(user);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(menuFrame, "❌ Invalid amount.");
            }
        });

        withdrawBtn.addActionListener(e -> {
            String amt = JOptionPane.showInputDialog(menuFrame, "Enter amount to withdraw:");
            try {
                double val = Double.parseDouble(amt);
                user.getCheckingAccount().withdraw(val);
                UserDataStore.logTransaction(user.getCustomerNumber(), "WITHDRAW", val);
                UserDataStore.saveUser(user);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(menuFrame, "❌ Invalid amount.");
            }
        });

        miniStatementBtn.addActionListener(e -> {
            // For simplicity, just show last DB transactions
            try (Connection con = DBUtil.getConnection()) {
                String sql = "SELECT * FROM transactions WHERE customerNumber=? ORDER BY id DESC LIMIT 5";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, user.getCustomerNumber());
                ResultSet rs = ps.executeQuery();

                StringBuilder sb = new StringBuilder("📜 Last Transactions:\n");
                while (rs.next()) {
                    sb.append(rs.getString("type")).append(" ₹").append(rs.getDouble("amount"))
                      .append(" on ").append(rs.getTimestamp("date")).append("\n");
                }
                JOptionPane.showMessageDialog(menuFrame, sb.toString());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(menuFrame, "❌ Error fetching transactions.");
            }
        });

        transferBtn.addActionListener(e -> {
            String amt = JOptionPane.showInputDialog(menuFrame, "Enter amount to transfer:");
            try {
                double val = Double.parseDouble(amt);
                if (user.getCheckingAccount().getBalance() >= val) {
                    user.getCheckingAccount().withdraw(val);
                    user.getSavingAccount().deposit(val);
                    UserDataStore.logTransaction(user.getCustomerNumber(), "TRANSFER", val);
                    UserDataStore.saveUser(user);
                    JOptionPane.showMessageDialog(menuFrame, "✅ Transferred ₹" + val);
                } else {
                    JOptionPane.showMessageDialog(menuFrame, "❌ Insufficient balance.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(menuFrame, "❌ Invalid amount.");
            }
        });

        exitBtn.addActionListener(e -> {
            UserDataStore.saveUser(user);
            menuFrame.dispose();
        });

        menuFrame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ATMGui::new);
    }
}
