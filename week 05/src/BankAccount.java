// 객체지향 프로그래밍에서는 관련된 속성과 기능을 하나의 클래스로 묶어 관리.
public class BankAccount {
    // 멤버변수(속성)
    //private => 동일 클래스
    private int bankCode;
    private int accountNo;
    private String owner;
    private int balance;
    private boolean isDormant;
    private int password;

    // 메소드(기능)
    public void inquiry(){}
    public void deposit(){}
    public void heldInDormant(){}
    public void changePassword(int password){
        this.password = password;
    }

    // 생성자
    // ** 클래스 내부에 정의, 생성자 메서드명은 클래스명과 일치해야함. **
    // new 연산자와 함께 사용해야함.

    // 기본생성자는 아무런 생성자가 선언되어있지 않을때만 자동으로 생성됨.
    BankAccount(){

    }

    BankAccount(
            int bankcode,
            int accountNo,
            String owner,
            int balance,
            int password,
            boolean isDoramnt
    ){
        //this -instance 자기자신을 가리키는 특수한 변수
        this.bankCode = bankcode;
        this.accountNo = accountNo;
        this.owner = owner;
        this.balance = balance ;
        this.password = password ;
        this.isDormant = isDoramnt ;
    }

    // getter와 setter를 통해 멤버 변수 조작.
    public int getBankCode() {
        return bankCode;
    }

    public void setBankCode(int bankCode) {
        this.bankCode = bankCode;
    }

    public int getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(int accountNo) {
        this.accountNo = accountNo;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public boolean isDormant() {
        return isDormant;
    }

    public void setDormant(boolean dormant) {
        isDormant = dormant;
    }

    public int getPassword() {
        return password;
    }

    public void setPassword(int password) {
        this.password = password;
    }
}
