# Chapte01

## 1. SQL을 직접 다룰 때 발생하는 문제점

> 일반적인 JAVA 서버개발시 관계형 데이터베이스\(RDB\)를 사용하여 데이터를 저장하는데, 서버 -&gt; JDBC API를 통한 SQL문 호출 -&gt; RDB 결과 반환 형식으로 사용함.
>
> * 반복적인 SQL 작성의 문제
> * SQL에 의존적인 개발
> * 객체지향과 관계형 데이터베이스의 페러다임 불일치
> * 페러다임 차이 극복을 위해 많은 리소스 낭비

## 2. JPA란?

* JAVA 진영의 ORM 표준 기술

  > ORM이란?
  >
  > * 객체와 관계형 데이터베이스 매핑을 하는 기술
  > * DML 처리시 ORM 프레임워크를 처리를 가능.

## 3. JPA의 필요성

* 생산성 증가
  * DML 처리를 위한 SQL 작성대신 JPA가 제공하는 기능으로 SQL 작성이 생략됨.
* 유지보수 비용 감소
  * SQL을 직접 핸들링할 경우 테이블 스키마가 변경시 SQL 문을 매핑하기 위한 JDBC API 코드를 모두 변경해야함.
  * JPA를 통하여 사용할 경우 JDBC API 코드가 존재하지 않음.
  * 객체지향적인 설계로 좋은 유지보수하기 용이한 도메인모델로 개발이 가능. 
    + 관계형 데이터베이스의 연관관계\(relationship\) 를 객체로 모델링이 가능
    + 실행되는 SQL문에 의존적인 대이터가 아니라, 객체를 이요한 그래프 탐색이 가능
    + JDBC API를 통하여 SQL을 직접 핸들링하는 경우 객체 자체를 비교할 경우 일치하지 않음.

> SQL 사용시
>
> ```text
>    ```java
>    class MemberDao {
>     public Member getMember(String memberId){
>       String sql = "SELECT * FROM MEMBER WHERE MEMBER_ID = ?";
>       ...
>       return new Member(...);
>     }
>    }
> ```

```text
   String memberId = "kys0213";
   Member member01 = memberDao.getMember(memberId);
   Member member02 = memberDao.getMember(memberId);
   member01 == member02; // java heap의 메모리 주소가 다름.
   ```




   > JPA 사용시
   ```java
   String memberId = "kys0213";
   Member member01 = jpa.find(Member.class, memberId);
   Member member02 = jpa.find(Member.class, memberId);
   member01 == member02; // 같다
   ```
```

* 성능상 이점
  * 어플리케이션과 데이터베이스 사이에서 다양한 성능 최적화 기회를 제공
  * 아래s는 회원을 두 번 조회하는 코드의 일부로 JDBC API 사용시 DB에 직접 각각 통신하여 조회하지만, JPA의 경우 두번째 조회한 회원 객체를 재사용함.

    ```java
    String memberId = "100";
    Member member01 = jpa.find(Member.class, memberId);
    Member member02 = jpa.find(Member.class, memberId);
    ```
* 데이터 접근 추상화와 벤더 독립성
  * 관계형 데이터베이스의 경우 같은 기능도 벤더마다 사용법이 다름. ex\) MySql, Oracle, H2, Tibero...
  * JPA 사용시 벤더별 추상화된 데이터 접근 계층을 통하여 특정 데이터베이스에 종속적이지 않다.

