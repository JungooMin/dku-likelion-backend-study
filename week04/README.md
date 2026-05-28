# Week04 - Java 객체지향 / 예외처리

## 학습 내용

이번 주차에는 Java 객체지향 프로그래밍과 예외 처리에 대해 학습하였다.

객체와 클래스의 개념을 이해하고,
클래스 설계, 상속, 접근제어자, 인터페이스 등의 객체지향 개념을 코드로 직접 구현해보았다.

또한 try-catch-finally 문을 활용하여 프로그램 실행 중 발생할 수 있는 예외를 처리하는 방법도 함께 학습하였다.

---

## 1. 객체지향 프로그래밍

객체(Object)는 속성과 기능을 가지는 대상이다.

속성은 객체가 가지는 정적인 정보이며 멤버 변수로 표현한다.

기능은 객체가 수행할 수 있는 동작이며 메서드로 표현한다.

이번 실습에서는 계좌 시스템을 예제로 객체지향 프로그래밍을 구현하였다.

```java
public class BankAccount {
````

```java
private int bankCode;
private int accountNo;
private String owner;
private int balance;
```

```java
public void inquiry(){}
public void deposit(){}
public void withdraw(){}
```

사물이 공통적으로 가지는 속성과 기능을 클래스로 구조화하여 설계하는 것이 객체지향 프로그래밍의 핵심이라는 점을 학습했고, 클래스를 기반으로 실제 객체를 생성하였다.

```java
BankAccount bankAccount = new BankAccount();
```

---

## 2. 클래스

클래스는 객체를 생성하기 위한 설계도 역할을 한다.

이번 실습에서는 BankAccount 클래스를 중심으로 계좌 정보를 저장하고,
계좌 기능을 수행하도록 구현하였다.

```java
public class BankAccount {
```

멤버 변수에는 계좌 정보가 저장되며,
메서드를 통해 계좌 기능을 수행할 수 있도록 구성하였다.

```java
private String owner;
private int balance;
```

```java
public void deposit(){}
public void inquiry(){}
```

또한 생성자를 사용하여 객체 생성 시 필요한 값을 초기화하였다.

```java
BankAccount(){}

BankAccount(
        int bankcode,
        int accountNo,
        String owner,
        int balance,
        int password,
        boolean isDormant
)
```

this 키워드를 사용하여 자기 자신의 멤버 변수에 접근하였다.

```java
this.bankCode = bankcode;
```

---

## 3. 상속

상속은 부모 클래스의 속성과 기능을 자식 클래스가 물려받는 개념이다.

```java
public class SavingAccount extends BankAccount
```

SavingAccount, DollarAccount, SubscriptionAccount 클래스는
BankAccount 클래스를 상속받아 구현하였다.

이를 통해 공통 기능을 재사용하고,
계좌 종류에 따라 서로 다른 기능을 추가할 수 있었다.

Java는 하나의 클래스만 상속받을 수 있다.(단일 상속)


---

## 4. 오버로딩과 오버라이딩

### 오버로딩

오버로딩은 같은 이름의 메서드를 매개변수만 다르게 여러 개 정의하는 방식이다.

```java
void inquiry(double currencyRate){}
```

기존 메서드와 이름은 같지만,
다른 매개변수를 사용하여 새로운 기능을 구현할 수 있다.

### 오버라이딩

오버라이딩은 부모 클래스의 메서드를 자식 클래스에서 재정의하는 방식이다.

```java
public void deposit(){

}
```

부모 클래스의 기능을 자식 클래스 상황에 맞게 변경하여 사용할 수 있다.

---

## 5. 접근제어자

접근제어자는 클래스와 클래스 내부 멤버 변수, 메서드에 대한 접근 범위를 제어하는 역할을 한다.

```java
private int password;
```

```java
public void inquiry(){}
```

private은 동일 클래스 내부에서만 접근 가능하며,
public은 외부 클래스에서도 접근 가능하다.

Getter와 Setter 메서드를 사용해서 멤버 변수 값을 조회하거나 변경하였다.

```java
public int getBalance() {
    return balance;
}

public void setBalance(int balance) {
    this.balance = balance;
}
```

비밀번호 변경 기능을 메서드로 구현하여 값을 수정하였다.

```java
public void changePassword(int password){
    this.password = password;
}
```

---

## 6. 인터페이스

인터페이스는 메서드 이름, 파라미터, 반환 타입만 정의할 수 있으며,
실제 동작 내용은 구현하지 않는다.

```java
public interface withdrawable {

    void withdraw();
}
```

SavingAccount 클래스에서 인터페이스를 구현하였다.

```java
public class SavingAccount extends BankAccount implements withdrawable
```

인터페이스를 통해 기능의 형식을 통일할 수 있다.

---

## 7. 예외 처리

프로그램 실행 중 발생할 수 있는 오류 상황을 예외(Exception)라고 한다.

이번 실습에서는 try-catch-finally 문을 사용하여 예외를 처리하였다.

```java
try {

} catch(Exception e) {

} finally {

}
```

0으로 나누는 연산을 통해 예외 발생 상황을 구현하였다.

```java
int a = 10;
int b = 0;
int c = a / b;
```

ArrayList 범위를 벗어난 인덱스 접근을 통해 예외가 발생하는 상황을 구현하였다.

```java
ArrayList arrayList = new ArrayList(3);
arrayList.get(10);
```

여러 catch 문을 사용하여 다양한 예외를 처리하였다.

```java
catch(IndexOutOfBoundsException ioe)
catch(IllegalArgumentException iae)
catch(Exception e)
```

마지막 Exception은 처리되지 않은 예외를 한 번 더 처리하기 위해 사용된다.

finally는 예외 발생 여부와 관계없이 항상 실행되는 영역이다.

---

## 느낀 점

이번 실습을 통해 객체지향 프로그래밍은 단순히 기능만 구현하는 것이 아니라,
공통된 속성과 기능을 클래스로 구조화하여 설계하는 과정이라는 점을 이해할 수 있었다.

또한 상속과 인터페이스를 활용하면 코드 재사용성과 기능 확장이 가능하다는 점을 학습하였다.

예외 처리를 통해 프로그램 실행 중 발생할 수 있는 오류 상황에 대응하는 방법도 함께 익힐 수 있었다.

```
