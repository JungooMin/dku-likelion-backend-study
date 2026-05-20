public class SavingAccount extends BankAccount implements withdrawable{

    boolean isOverdraft;
    void transfer() {};
    public void withdraw() {
        System.out.println("Withdraw");
    };
}
