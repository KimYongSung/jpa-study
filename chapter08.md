# chapter08

  

## 1. 프록시

### 1.1 프록시 기초

```java
Member member = em.getReference(Member.class, "member1");
```

* find() 대신 getReference() 메소드를 사용하여 Proxy 객체를 반환.
* getReference() 를 사용하여 조회시 데이터베이스를 조회하지 않고 실제 엔티티 객체도 생성하지 않음.

* 프록시는 실제 클래스를 상속 받아서 만들어진다.
* 사용시 프록시 객체 유무를 구분하지 않고 사용함.
* 프록시 객체는 실제 객체에 대한 참조를 보관하고 있으며, 메소드 호출시 실제 객체의 메소드를 호출함.

```java
public void printOrderAndDelivery(Long orderNo){
    Orders order = em.find(Order.class, orderNo);
    Delivery delivery = order.getDelivery();
    
    System.out.println("주문금액 : " + order.getOrderPrice());
    System.out.println("배송지주소 : " + delivery.getAddr());
}
```

```java
public void printOrder(Long orderNo){
    Orders order = em.find(Order.class, orderNo);
    System.out.println("주문금액 : " + order.getOrderPrice());
}
```

* printOrderAndDelivery 메소드는 orderNo로 주문정보 및 배송지 정보를 모두 출력하지만, printOrder는 주문정보만 출력하고 Delivery 정보는 사용하지 않음.
* [프록시](https://ko.wikipedia.org/wiki/프록시_패턴)를 사용한 지연로딩으로 위의 케이스에서 성능향상이 가능함.



### 1.2 프록시 특징

* 프록시 객체는 처음 사용할 때 한 번만 초기화된다.
* 초기화를 통해서 프록시 객체가 실제 엔티티로 바뀌는 것이 아니다.
* 초기화되면 프록시 객체를 통해서 실제 엔티티로 접근할 수 있다.
* 프록시 객체는 원본객체를 상속받은 객체이므로 타입 체크 시에 주의해서 사용해야한다.
* 영속성 컨텍스트에 찾는 엔티티가 있으면 데이터를 조회할 필요가 없으므로 **em.getReference()** 를 호출하여도 실제 엔티티를 반환한다.
* 컬렉션의 경우도 래퍼 클래스를 지원한다.
* 초기화는 **영속성 컨텍스트**의 도움을 받아야 하며, 준영속 상태의 프록시를 초기화 할 경우 문제가 발생한다.
  * hibernate는 **org.hibernate.LazyInitializationException** 예외를 발생시킨다.

> JPA 표준 명세에서 지연로딩에 대한 내용은 구현체에 맡겼다. 초기화할 때 어떤 일이 발생할지 표준에는 정의되어 있지 않다.



### 1.3 프록시와 식별자

* 엔티티를 프록시로 조회할 때 프록시 객체는 식별자 값을 보관한다.
* 이미 식별자 값을 가지고 있으므로 식별자에 접근시 프록시 객체를 초기화하지 않는다. 단, 엔티티 접근 방식을 @Access(AccessType.PROPERTY) 로 설정한 경우에만 초기화하지 않는다.

```java
Delivery delivery = em.getReference(Delivery.class, "deliveryNo");
delivery.getDeliveryNo(); // 초기화되지 않는다.
```

* 연관관계를 맺을때 식별자 값만 사용하므로 프록시를 사용시 데이터베이스 접근 횟수를 줄일 수 있다.

```java
Orders order = em.find(Order.class, orderNo);
Delivery delivery = em.getReference(Delivery.class, "deliveryNo"); // SQL을 실행하지 않음
order.setDelivery(delivery);
```



## 2. 즉시 로딩과 지연 로딩

* 즉시 로딩 : 엔티티를 조회할 때 연관된 엔티티도 함께 조회한다.
  * ex) em.find(Order.class, "orderNo") 를 호출할 때 주문 엔티티와 연관된 배성 엔티티도 함께 조회한다.
  * 설정 방법 : @ManyToOne(fetch = FetchType.EAGER)
  * @ManyToOne, @OneToOne는 즉시로딩이 기본 설정 값이다.
  * 즉시로딩 설정과 조인 전략
    * @ManyToOne, @OneToOne
      * optional = false : 내부조인 ( INNER JOIN )
      * optional = true : 외부조인 (OUTER JOIN)
    * @OneToMany, @ManyToMany
      * optional = false : 외부 조인 ( OUTER JOIN )
      * optional = true : 외부 조인 ( OUTER JOIN )
* 지연 로딩 : 연관된 엔티티를 실제 사용할 때 조회한다.
  * ex) order.getDelivery().getAddr() 처럼 조회한 배송 엔티티를 실제 사용시 SQL을 호출하여 조회한다.
  * 설정 방법 : @ManyToOne(fetch = FetchType.LAZY)
  * @OneToMany, @ManyToMany 는 지연로딩이 기본 설정 값이다.

### 2.1 즉시 로딩

```java
@Entity
@Table(name = "ORDERS")
@SequenceGenerator(name = "ORDER_NO_SEQ_GENERATOR", sequenceName = "ORDER_NO_SEQ", initialValue = 1, allocationSize = 1)
@Setter
@Getter
@ToString(exclude = { "member", "orderGoods" })
@AttributeOverrides({
      @AttributeOverride(name = "regDt", column = @Column(name = "ORDER_DT"))
    , @AttributeOverride(name = "chgDt", column = @Column(name = "ORDER_CHG_DT"))
})
public class Orders extends DateBaseEntity{

    @Id
    @Column(length = 20, unique = true)
    @GeneratedValue(generator = "ORDER_NO_SEQ_GENERATOR", strategy = GenerationType.SEQUENCE)
    private Long orderNo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MEMBER_NO")
    private Member member;

    @OneToMany(mappedBy = "orders", fetch = FetchType.LAZY)
    private List<OrderGoods> orderGoods = new ArrayList<>();

    public void setMember(Member member) {

        if (this.member != null) {
            this.member.getOrders().remove(this);
        }

        this.member = member;

        if (member != null) {
            member.getOrders().add(this);
        }
    }
}
```

* Member와 Delivery에 즉시로딩을 설정함.
* Orders를 조회하는 시점에 Member도 즉시 조회한다.
  * JPA 구현체는 즉시 로딩 최적화를 위해 조인 쿼리를 사용하여 조회한다.

```java
Orders order = em.find(Orders.class, 1);
```

```sql
Hibernate: 
    select
        orders0_.order_no as order_no1_12_0_,
        orders0_.order_chg_dt as order_ch2_12_0_,
        orders0_.order_dt as order_dt3_12_0_,
        orders0_.member_no as member_n4_12_0_,
        member1_.member_no as member_n1_10_1_,
        member1_.chg_dt as chg_dt2_10_1_,
        member1_.reg_dt as reg_dt3_10_1_,
        member1_.age as age4_10_1_,
        member1_.id as id5_10_1_,
        member1_.name as name6_10_1_,
        member1_.pwd as pwd7_10_1_,
        member1_.status as status8_10_1_ 
    from
        orders orders0_ 
    left outer join // outer join
        member member1_ 
            on orders0_.member_no=member1_.member_no 
    where
        orders0_.order_no=?
```

#### 2.1.1 NULL 제약 조건과 JPA 조인 전략

* 엔티티상 외래키로 매핑된 컬럼에 NULL 제약조건인 경우 JPA는 OUTER  JOIN을 사용하여 조회한다.
  * Order 테이블에 memberNo 는 NULL을 허용하고 있다. inner join을 사용하여 비회원 주문을 조회시 주문 정보도 조회가 되지 않으므로 JPA에서 OUTER JOIN을 사용하여 조회한다.
* 엔티티상 외래키로 매핑된 컬럼에 NOT NULL 제약조건 또는 `@ManyToOne.optional = false` 를 추가할 경우 INNER JOIN을 사용하여 조회한다.



##### 2.1.1.1 NOT NULL 조건설정

```java
@ManyToOne(fetch = FetchType.EAGER)
@JoinColumn(name = "MEMBER_NO",nullable = false) // not null 설정 추가
private Member member;
```

```sql
Hibernate: 
    select
        orders0_.order_no as order_no1_12_0_,
        orders0_.order_chg_dt as order_ch2_12_0_,
        orders0_.order_dt as order_dt3_12_0_,
        orders0_.member_no as member_n4_12_0_,
        member1_.member_no as member_n1_10_1_,
        member1_.chg_dt as chg_dt2_10_1_,
        member1_.reg_dt as reg_dt3_10_1_,
        member1_.age as age4_10_1_,
        member1_.id as id5_10_1_,
        member1_.name as name6_10_1_,
        member1_.pwd as pwd7_10_1_,
        member1_.status as status8_10_1_ 
    from
        orders orders0_ 
    inner join
        member member1_ 
            on orders0_.member_no=member1_.member_no 
    where
        orders0_.order_no=?
```



##### 2.1.1.2 @ManyToOne.optional = false 설정

```java
@ManyToOne(fetch = FetchType.EAGER , optional = false)
@JoinColumn(name = "MEMBER_NO")
private Member member;
```

```sql
Hibernate: 
    select
        orders0_.order_no as order_no1_12_0_,
        orders0_.order_chg_dt as order_ch2_12_0_,
        orders0_.order_dt as order_dt3_12_0_,
        orders0_.member_no as member_n4_12_0_,
        member1_.member_no as member_n1_10_1_,
        member1_.chg_dt as chg_dt2_10_1_,
        member1_.reg_dt as reg_dt3_10_1_,
        member1_.age as age4_10_1_,
        member1_.id as id5_10_1_,
        member1_.name as name6_10_1_,
        member1_.pwd as pwd7_10_1_,
        member1_.status as status8_10_1_ 
    from
        orders orders0_ 
    inner join
        member member1_ 
            on orders0_.member_no=member1_.member_no 
    where
        orders0_.order_no=?
```



#### 2.1.2 컬렉션에 즉시로딩 사용시 주의점

* 컬렉션에 하나 이상 즉시 로딩하는 것은 권장하지 않음.
  * 컬렉션과 조인한다는 것은 테이블로 본다면 일대다 조인이다.
  * 서로 다른 테이블을 일대다 조인시 SQL 너무 많은 데이터를 반환할 수 있다.
* 컬릭션 즉시 로딩은 항상 외부 조인( OUTER JOIN ) 을 사용한다.
  * 다대일 관계에서 `not null 조건을 사용할 경우 INNER JOIN`이 가능하다.
  * 일대다 관계에서 다쪽에 `데이터가 없는 경우를 방지하기 위해서 OUTER JOIN`을 사용한다.



### 2.2 지연로딩

* FetchType.LAZY로 지정하여 사용한다.

```java
@Entity
@Table(name = "ORDERS")
@SequenceGenerator(name = "ORDER_NO_SEQ_GENERATOR", sequenceName = "ORDER_NO_SEQ", initialValue = 1, allocationSize = 1)
@Setter
@Getter
@ToString(exclude = { "member", "orderGoods" })
@AttributeOverrides({
      @AttributeOverride(name = "regDt", column = @Column(name = "ORDER_DT"))
    , @AttributeOverride(name = "chgDt", column = @Column(name = "ORDER_CHG_DT"))
})
public class Orders extends DateBaseEntity{

    @Id
    @Column(length = 20, unique = true)
    @GeneratedValue(generator = "ORDER_NO_SEQ_GENERATOR", strategy = GenerationType.SEQUENCE)
    private Long orderNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_NO")
    private Member member;

    @OneToMany(mappedBy = "orders", fetch = FetchType.LAZY)
    private List<OrderGoods> orderGoods = new ArrayList<>();

    public void setMember(Member member) {

        if (this.member != null) {
            this.member.getOrders().remove(this);
        }

        this.member = member;

        if (member != null) {
            member.getOrders().add(this);
        }
    }
}
```

```java
Orders order = em.find(Orders.class, 1l);
Member member = order.getMember();
System.out.println("member 로그 출력");
System.out.println(member.toString());
```

```sql
//1. Orders order = em.find(Orders.class, 1l); 호출
Hibernate: 
    select
        orders0_.order_no as order_no1_12_0_,
        orders0_.order_chg_dt as order_ch2_12_0_,
        orders0_.order_dt as order_dt3_12_0_,
        orders0_.member_no as member_n4_12_0_ 
    from
        orders orders0_ 
    where
        orders0_.order_no=?

// 2. System.out.println("member 로그 출력"); 호출
member 로그 출력

// 3. member.toString() 호출
Hibernate: 
    select
        member0_.member_no as member_n1_10_0_,
        member0_.chg_dt as chg_dt2_10_0_,
        member0_.reg_dt as reg_dt3_10_0_,
        member0_.age as age4_10_0_,
        member0_.id as id5_10_0_,
        member0_.name as name6_10_0_,
        member0_.pwd as pwd7_10_0_,
        member0_.status as status8_10_0_ 
    from
        member member0_ 
    where
        member0_.member_no=?

// 4. System.out.println으로 member 출력
Member(memberNo=20191012143652000105, id=kys0213, pwd=kys0213, name=김용성, age=30, status=A)  
```

* Member proxy 객체를 사용하는 시점에 sql로 조회한다.



### 2.3 정리

* 처음부터 연관된 엔티티를 모두 영속성 컨텍스트에 올려두는 것은 현실적이지 않다.
* 처리하는 업무에 따라서 즉시로딩과 지연로딩을 효율적으로 사용해야 한다.
  * 특정 회원과 연관된 컬렉션 데이터를 수만건 등록된 경우 즉시 로딩으로 설정시 수만건이 한번에 로딩된다.



## 3. 영속성 전이 ( CASCADE )

* JPA에서 엔티티를 저장할 때 연관된 모든 엔티티는 영속 상태여야 한다.

```java
Parent parent = new Parent();
parent.setName("parentName");
em.persist(parent);

Child child = new Child();
child.setName("childName");
child.setParent(parent);
em.persist(child);

Child child2 = new Child();
child2.setName("childName2");
child2.setParent(parent);
em.persist(child2);
```

* 영속성 전이를 사용하여 부모만 영속상태로 만들 경우 연관된 자식도 한 번에 영속 상태로 만들 수 있다.
* 영속성 전이는 연관관계 매핑과 아무 관련이 없다. 



### 3.1 저장

* `cascade = CascadeType.PERSIST` 옵션을 설정시 간편하게 부모와 자식 엔티티를 한번에 영속화 할 수 있다.

```java
@OneToMany(mappedBy = "parent", cascade = CascadeType.PERSIST) // 영속성 전이 설정
private List<Child> childs = new ArrayList<>();
```

```java
Parent parent = new Parent();
parent.setName("parentName");

Child child = new Child();
child.setName("childName");
child.setParent(parent);

Child child2 = new Child();
child2.setName("childName");
child2.setParent(parent);

em.persist(parent); // 한번만 영속화
```

```sql
/* insert jpa.study.entity.Parent
        */ insert 
        into
            parent
            (name, parent_id) 
        values
            (?, ?)
Hibernate: 
    /* insert jpa.study.entity.Child
        */ insert 
        into
            child
            (name, parent_id, child_id) 
        values
            (?, ?, ?)
Hibernate: 
    /* insert jpa.study.entity.Child
        */ insert 
        into
            child
            (name, parent_id, child_id) 
        values
            (?, ?, ?)
```



### 3.2 삭제

* cascade = CascadeType.REMOVE 옵션을 설정시 간편하게 부모와 자식 엔티티를 한번에 삭제 할 수 있다.

```java
@OneToMany(mappedBy = "parent", cascade = { CascadeType.PERSIST, CascadeType.REMOVE})
private List<Child> childs = new ArrayList<>();
```

```java
public void remove(EntityManager em) {
    Parent parent = em.find(Parent.class, 1l);
    em.remove(parent);
}
```

```sql
Hibernate: 
    /* delete jpa.study.entity.Child */ delete 
        from
            child 
        where
            child_id=?
Hibernate: 
    /* delete jpa.study.entity.Child */ delete 
        from
            child 
        where
            child_id=?
Hibernate: 
    /* delete jpa.study.entity.Parent */ delete 
        from
            parent 
        where
            parent_id=?
```



### 3.3 CASCADE 종류

* ALL : 모두 적용
* PERSIST : 영속
* MERGE : 병합
* REMOVE : 삭제
* REFRESH : REFRESH
* DETACH : DETACH

> PERSIST와 REMOVE는 flush 호출시점에 전이가 발생한다.



## 4. 고아객체

* JPA에서 부모 엔티티와 연관관계가 끊어진 자식 엔티티를 자동으로 삭제하는 기능을 제공한다.
* 부모 엔티티의 컬렉션에서 자식 엔티티의 참조만 제거하면 자식 엔티티가 자동으로 삭제할 수 있다.

```java
@OneToMany(mappedBy = "parent", orphanRemoval = true)
private List<Child> childs = new ArrayList<>();
```

```java
Parent parent = em.find(Parent.class, 1l);
parent.getChilds().remove(0);
```

```sql
/* delete jpa.study.entity.Child */ delete 
        from
            child 
        where
            child_id=?
```

- 고아 객체란 참조가 제거된 엔티티이다.
- 고아 객체 제거란 참조가 제거된 엔티티를 다른 곳에서 참조하지 않는 고아 객체로 보고 삭제하는 기능이다.
- `@OneToOne`과 `@OneToMany`에서만 설정이 가능하다.
  - 참조하는 곳이 다른 곳에서도 참조한다면 문제가 발생할 수 있다.

* 개념적으로 볼때 부모를 제거하면 자식은 고아가 된다.
  * 영속성전이의 `CascadeType.REMOVE`를 설정한 것과 같다.



## 5. 영속성 전이 + 고아 객체, 생명주기

* 생명주기란 ?
  * `EntityManager.persisty()`를 통해 영속화되고, `EntityManager.remove()`를 통해 제거되는 상태
* CascadeType.ALL + orphanRemoval = true 설정을 동시에 사용할 경우 부모를 통해서 자식의 생명주기를 관리할 수 있다.



