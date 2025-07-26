package atmproject;

import java.text.DecimalFormat;
import java.util.ArrayList;

public abstract class Account {
    protected double balance;
    protected DecimalFormat moneyFormat = new DecimalFormat("'₹'###,##0.00");
    protected ArrayList<String> transactions = new ArrayList<>();

    public double getBalance() {
        return balance;
    }

    // ✅ Combined deposit method
    public void deposit(double amount) {
        if (amount <= 0) {
            System.out.println("❌ Deposit amount must be positive.");
            return;
        }
        balance += amount;
        transactions.add("Deposited: " + moneyFormat.format(amount));
        System.out.println("✅ Deposit successful. New Balance: " + moneyFormat.format(balance));
    }

    // ✅ Combined withdraw method
    public void withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("❌ Withdraw amount must be positive.");
            return;
        }
        if (balance - amount < 0) {
            System.out.println("❌ Insufficient funds.");
            return;
        }
        balance -= amount;
        transactions.add("Withdrew: " + moneyFormat.format(amount));
        System.out.println("✅ Withdraw successful. New Balance: " + moneyFormat.format(balance));
    }

    // ✅ Mini statement
    public void printMiniStatement() {
        System.out.println("📜 Last Transactions in " + getAccountType() + " Account:");
        int start = Math.max(0, transactions.size() - 5);
        for (int i = start; i < transactions.size(); i++) {
            System.out.println(transactions.get(i));
        }
    }

    // Abstract method for subclasses
    public abstract String getAccountType();
}
