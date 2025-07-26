package atmproject;

public class User {
    private int customerNumber;
    private int pinNumber;
    private CheckingAccount checkingAccount = new CheckingAccount();
    private SavingAccount savingAccount = new SavingAccount();

    public User(int customerNumber, int pinNumber) {
        this.customerNumber = customerNumber;
        this.pinNumber = pinNumber;
    }

    public int getCustomerNumber() { return customerNumber; }
    public int getPinNumber() { return pinNumber; }
    public CheckingAccount getCheckingAccount() { return checkingAccount; }
    public SavingAccount getSavingAccount() { return savingAccount; }
}
