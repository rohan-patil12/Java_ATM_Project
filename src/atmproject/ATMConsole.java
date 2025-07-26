package atmproject;

import java.util.HashMap;
import java.util.Scanner;

public class ATMConsole {

    // ✅ Transfer Funds Method
    public void transferFunds(User user, double amount, boolean fromCheckingToSaving) {
        if (fromCheckingToSaving) {
            if (user.getCheckingAccount().getBalance() >= amount) {
                user.getCheckingAccount().withdraw(amount);
                user.getSavingAccount().deposit(amount);
                System.out.println("✅ Transferred $" + amount + " from Checking to Saving.");
            } else {
                System.out.println("❌ Insufficient balance in Checking.");
            }
        } else {
            if (user.getSavingAccount().getBalance() >= amount) {
                user.getSavingAccount().withdraw(amount);
                user.getCheckingAccount().deposit(amount);
                System.out.println("✅ Transferred $" + amount + " from Saving to Checking.");
            } else {
                System.out.println("❌ Insufficient balance in Saving.");
            }
        }
    }

    public static void main(String[] args) {
        // Load user data
        HashMap<Integer, User> users = UserDataStore.loadUsers();
        Scanner sc = new Scanner(System.in);

        System.out.println("Welcome to the ATM!");
        System.out.print("Enter customer number: ");
        int cn = sc.nextInt();
        System.out.print("Enter PIN: ");
        int pin = sc.nextInt();

        if (users.containsKey(cn) && users.get(cn).getPinNumber() == pin) {
            User u = users.get(cn);
            System.out.println("✅ Login Successful!");
            ATMConsole console = new ATMConsole(); // to call non-static methods

            while (true) {
                System.out.println("\n--- Menu ---");
                System.out.println("1. View Checking Balance");
                System.out.println("2. Deposit to Checking");
                System.out.println("3. Withdraw from Checking");
                System.out.println("4. Mini Statement");
                System.out.println("5. Transfer Funds");
                System.out.println("6. Exit");
                System.out.print("Enter choice: ");
                int choice = sc.nextInt();

                switch (choice) {
                    case 1:
                        System.out.println("Balance: $" + u.getCheckingAccount().getBalance());
                        break;
                    case 2:
                        System.out.print("Enter amount: ");
                        u.getCheckingAccount().deposit(sc.nextDouble());
                        break;
                    case 3:
                        System.out.print("Enter amount: ");
                        u.getCheckingAccount().withdraw(sc.nextDouble());
                        break;
                    case 4:
                        u.getCheckingAccount().printMiniStatement();
                        break;
                    case 5:
                        System.out.println("Transfer from:");
                        System.out.println("1. Checking to Saving");
                        System.out.println("2. Saving to Checking");
                        int tChoice = sc.nextInt();
                        System.out.print("Enter amount: ");
                        double amount = sc.nextDouble();
                        if (tChoice == 1) {
                            console.transferFunds(u, amount, true);
                        } else if (tChoice == 2) {
                            console.transferFunds(u, amount, false);
                        } else {
                            System.out.println("❌ Invalid transfer choice.");
                        }
                        break;
                    case 6:
                        System.out.println("👋 Goodbye!");
                        UserDataStore.saveUsers(users);
                        sc.close();
                        return;
                    default:
                        System.out.println("❌ Invalid choice. Try again.");
                }
            }
        } else {
            System.out.println("❌ Invalid credentials.");
        }
    }
}
