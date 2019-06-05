# Chapter06

## 연관관계 종류

### 1. 다대일

* 다대일과 일대다는 서로의 반대 관계
* 외래 키는 항상 다 쪽에 존재함.

#### 1.1 다대일 단방향

![다대일단방향](./img/단방향_다대일_class_diagram.png)

![erd](./img/단방향_다대일_erd.png)



* 단방향 다대일 객체 매핑

```java
// Member 엔티티 클래스
@Entity
@Table(name = "MEMBER")
@Getter
@Setter
@ToString(exclude = "orders")
public class Member {
    
    @Id
    @Column(length = 20, unique = true)
    @GenericGenerator(name = "seqGenerator"
                    , strategy = "jpa.study.entity.key.DateAndSeqCombinationGenerator"
                    ,parameters =@org.hibernate.annotations.Parameter(
                                 name = DateAndSeqCombinationGenerator.SEQUENCE_NAME_KEY
                                ,value = "MEMBER_NO_SEQ"
                                )
                    )
    @GeneratedValue(generator = "seqGenerator")
    private String memberNo;

    @Column(nullable = false, length = 20)
    private String id;

    @Column(nullable = false, length = 20)
    private String pwd;

    @Column(nullable = false, length = 20)
    private String name;
    
    @Column(nullable = false)
    private Integer age;
    
    @Enumerated(EnumType.STRING)
    private MemberStatus status;
    
    ...
}

// Orders 엔티티 클래스
@Entity
@Table(name = "ORDERS")
@SequenceGenerator(
        name = "ORDER_NO_SEQ_GENERATOR",
        sequenceName = "ORDER_NO_SEQ",
        initialValue = 1, allocationSize = 1)
@Setter
@Getter
@ToString(exclude = { "member" , "orderGoods" })
public class Orders {

    @Id
    @Column(length = 20, unique = true)
    @GeneratedValue(generator = "ORDER_NO_SEQ_GENERATOR", strategy = GenerationType.SEQUENCE)
    private Long orderNo;

    // 단방향 N:1
    @ManyToOne
    @JoinColumn(name="MEMBER_NO")
    private Member member;

    public void setMember(Member member) {
        
        if(this.member != null) {
            this.member.getOrders().remove(this);
        }
        
        this.member = member;
        
        if(member != null) {
             member.getOrders().add(this);
        }
    }
    
    ...
}
```



#### 1.2 다대일 양방향

* 양방향은 외래 키가 있는 쪽인 연관관계의 주인이다.
* 양방향 연관관계는 항상 서로를 참조해야 한다.

![클래스 다이어그램](./img/양방향_다대일_class_diagram.png)

```java
// Member 엔티티 클래스
@Entity
@Table(name = "MEMBER")
@Getter
@Setter
@ToString(exclude = "orders")
public class Member {
    
    @Id
    @Column(length = 20, unique = true)
    @GenericGenerator(name = "seqGenerator"
                    , strategy = "jpa.study.entity.key.DateAndSeqCombinationGenerator"
                    ,parameters =@org.hibernate.annotations.Parameter(
                                 name = DateAndSeqCombinationGenerator.SEQUENCE_NAME_KEY
                                ,value = "MEMBER_NO_SEQ"
                                )
                    )
    @GeneratedValue(generator = "seqGenerator")
    private String memberNo;

    @Column(nullable = false, length = 20)
    private String id;

    @Column(nullable = false, length = 20)
    private String pwd;

    @Column(nullable = false, length = 20)
    private String name;
    
    @Column(nullable = false)
    private Integer age;
    
    @Enumerated(EnumType.STRING)
    private MemberStatus status;
    
    // 1:N 매핑
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private List<Orders> orders = new ArrayList<>();
    
    ...
}

// Orders 엔티티 클래스
@Entity
@Table(name = "ORDERS")
@SequenceGenerator(
        name = "ORDER_NO_SEQ_GENERATOR",
        sequenceName = "ORDER_NO_SEQ",
        initialValue = 1, allocationSize = 1)
@Setter
@Getter
@ToString(exclude = { "member" , "orderGoods" })
public class Orders {

    @Id
    @Column(length = 20, unique = true)
    @GeneratedValue(generator = "ORDER_NO_SEQ_GENERATOR", strategy = GenerationType.SEQUENCE)
    private Long orderNo;

    // N:1 매핑
    @ManyToOne
    @JoinColumn(name="MEMBER_NO")
    private Member member;

    public void setMember(Member member) {
        
        if(this.member != null) {
            this.member.getOrders().remove(this);
        }
        
        this.member = member;
        
        if(member != null) {
             // member에 객체 참조
             member.getOrders().add(this);
        }
    }
    
    ...
}
```



### 2. 일대다

* 일대다 관계는 다대일 관계의 반대 방향이다.
* 일대다 관계는 자바 Collection, List, Set, Meap 중에 하나를 사용해야함.



#### 2.1 일대다 단방향

* 보통과 달리 `반대쪽 테이블에 있는 외래 키를 관리`한다.
* 일대다 단방향의 경우 매핑시에 @JoinColumn을 명시해야 한다. 명시 하지 않을 경우 연관관계를 관리하는 조인 테이블 전략을 기본으로 사용한다.

![class diagram](./img/단방향_일대다_class_diagram.png)

```java
// Member 엔티티 클래스
@Entity
@Table(name = "MEMBER")
@Getter
@Setter
@ToString(exclude = "orders")
public class Member {
    
    @Id
    @Column(length = 20, unique = true)
    @GenericGenerator(name = "seqGenerator"
                    , strategy = "jpa.study.entity.key.DateAndSeqCombinationGenerator"
                    ,parameters =@org.hibernate.annotations.Parameter(
                                 name = DateAndSeqCombinationGenerator.SEQUENCE_NAME_KEY
                                ,value = "MEMBER_NO_SEQ"
                                )
                    )
    @GeneratedValue(generator = "seqGenerator")
    private String memberNo;

    // 1:N 단방향 매핑.
    @OneToMany
    @JoinColumn(name = "MEMBER_NO") // Orders의 MEMBER_NO 컬럼으로 JOIN
    private List<Orders> orders = new ArrayList<>();
    
    ...

}

// Orders 엔티티 클래스
@Entity
@Table(name = "ORDERS")
@SequenceGenerator(
        name = "ORDER_NO_SEQ_GENERATOR",
        sequenceName = "ORDER_NO_SEQ",
        initialValue = 1, allocationSize = 1)
@Setter
@Getter
@ToString
public class Orders {

    @Id
    @Column(length = 20, unique = true)
    @GeneratedValue(generator = "ORDER_NO_SEQ_GENERATOR", strategy = GenerationType.SEQUENCE)
    private Long orderNo;
    
    @OneToOne
    @JoinColumn(name="DELIVERY_NO")
    private Delivery delivery;
 
    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        
        if(delivery != null) {
            delivery.setOrder(this);
        }
    }
}
```



##### 2.1.1 일대다 단방향 매핑의 단점

* 매핑한 객체과 관리하는 외래 키가 다른 테이블에 존재.

* 본인 테이블에 외래 키가 있으면 연관관계 처리시 INSERT로 처리 가능하지만, 다른 테이블에 외래 키가 있을 경우 UPDATE 를 추가로 실행해야함.

```java
 public void logic(EntityManager em) {
        
     Member member = makeMember(em);

     Orders order = 주문요청(em, member, "우리집");
     Orders order2 = 주문요청(em, member, "우리집");
 }

private Orders 주문요청(EntityManager em, Member member, String addr) {
    Orders order = new Orders();
    order.setOrderDt(new Date());
    em.persist(order); // INSERT
    member.getOrders().add(order); // 1:N 연관관계 매핑 
    return order;
}

private static Member makeMember(EntityManager em) {
    Member member = new Member();
    member.setId("kys0213");
    member.setPwd("kys0213");
    member.setName("김용성");
    member.setAge(30);
    member.setStatus(MemberStatus.A);
    em.persist(member); // INSERT
    return member;
}
```

```sql
Hibernate: 
    /* insert jpa.study.entity.Member
        */ insert 
        into
            member
            (age, id, name, pwd, status, member_no) 
        values
            (?, ?, ?, ?, ?, ?)
Hibernate: 
    /* insert jpa.study.entity.Orders
        */ insert 
        into
            orders
            (delivery_no, order_dt, order_no) 
        values
            (?, ?, ?)
Hibernate: 
    /* insert jpa.study.entity.Orders
        */ insert 
        into
            orders
            (delivery_no, order_dt, order_no) 
        values
            (?, ?, ?)
Hibernate: 
    /* create one-to-many row jpa.study.entity.Member.orders */ update
        orders 
    set
        member_no=? 
    where
        order_no=?
Hibernate: 
    /* create one-to-many row jpa.study.entity.Member.orders */ update
        orders 
    set
        member_no=? 
    where
        order_no=?
```



#### 2.2 일대다 양방향

* 양방향 매핑에서는 @OneToMany는 연관관계 주인이 될 수 없다.
* 관계형 데이터 베이스 특성상 항상 다 쪽에 외래키가 존재하고, 해당 이유로 @ManyToOne에는 mappedBy 속성이 없다.
* 일대다 양방향 매핑은 `읽기 전용으로 다대일 단방향 매핑`을 추가해야한다.

![양방향 일대다 class diagram](./img/양방향_일대다_class_diagram.png)

```java
// Member 엔티티 클래스
@Entity
@Table(name = "MEMBER")
@Getter
@Setter
@ToString(exclude = "orders")
public class Member {
    
    @Id
    @Column(length = 20, unique = true)
    @GenericGenerator(name = "seqGenerator"
                    , strategy = "jpa.study.entity.key.DateAndSeqCombinationGenerator"
                    ,parameters =@org.hibernate.annotations.Parameter(
                                 name = DateAndSeqCombinationGenerator.SEQUENCE_NAME_KEY
                                ,value = "MEMBER_NO_SEQ"
                                )
                    )
    @GeneratedValue(generator = "seqGenerator")
    private String memberNo;
    
    // 1:N 단방향 매핑.
    @OneToMany
    @JoinColumn(name = "MEMBER_NO") // Orders의 MEMBER_NO 컬럼으로 JOIN
    private List<Orders> orders = new ArrayList<>();

    ...
}

// Orders 엔티티 클래스
@Entity
@Table(name = "ORDERS")
@SequenceGenerator(
        name = "ORDER_NO_SEQ_GENERATOR",
        sequenceName = "ORDER_NO_SEQ",
        initialValue = 1, allocationSize = 1)
@Setter
@Getter
@ToString(exclude = { "member" ,"orderGoods" })
public class Orders {

    @Id
    @Column(length = 20, unique = true)
    @GeneratedValue(generator = "ORDER_NO_SEQ_GENERATOR", strategy = GenerationType.SEQUENCE)
    private Long orderNo;
    
    // 읽기 전용으로 양방향 매핑
    @ManyToOne
    @JoinColumn(name="MEMBER_NO", insertable = false, updatable = false) 
    private Member member;
    
   ...
}
```

##### 2.2.1 일대다 양방향 매핑의 단점

* 일대다 단방향 매핑의 단점을 그대로 가진다.
* 될 수 있으면 **다대일 양방향 매핑을 사용**해야한다.



### 3. 일대일

* 일대일 관계는 서로 하나의 관계만 가진다.
* 일대일 관계는 그 반대도 일대일이다.
* 일대일 관계는 주 테이블이나 대상 테이블 둘 중 어느 곳이나 외래키를 가질 수 있다.
  * 주 테이블에 외래 키
    * 테이블이 외래 키를 가지고 있으므로 주 테이블만 확인해도 대상 테이블과 연관관계가 있는지 알 수 있다.
  * 대상 테이블에 외래 키
    * 테이블 관계를 일대일에서 일대다로 변경할 때 테이블 구조를 그대로 유지 가능하다.



#### 3.1 주 테이블에 외래 키

* 객체지향 개발자들은 주 테이블에 외래 키가 있는 것을 선호한다.
* JPA에서 주 테이블에 외래 키가 있으면 좀 더 편리하게 매핑할 수 있다.

![일대일 erd](./img/일대일_erd.png)

##### 3.1.1 단방향

![주 테이블 외래키 단방향 class diagram](./img/주_테이블_외래키_일대일_단방향_class_diagram.png)

* 일대일 관계이므로 @OneToOne을 사용하여 매핑한다.

```java
// Order 엔티티 클래스
@Entity
@Table(name = "ORDERS")
@SequenceGenerator(name = "ORDER_NO_SEQ_GENERATOR", sequenceName = "ORDER_NO_SEQ", initialValue = 1, allocationSize = 1)
@Setter
@Getter
@ToString(exclude = { "member", "orderGoods" })
public class Orders {

    @Id
    @Column(length = 20, unique = true)
    @GeneratedValue(generator = "ORDER_NO_SEQ_GENERATOR", strategy = GenerationType.SEQUENCE)
    private Long orderNo;

    @ManyToOne
    @JoinColumn(name = "MEMBER_NO")
    private Member member;

    // 1:1 단방향 매핑
    @OneToOne 
    @JoinColumn(name = "DELIVERY_NO")
    private Delivery delivery;

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

// Delivery 엔티티 클래스
@Entity(name = "DELIVERY")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "order")
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String deliveryNo;

    private String addr;

    private String zipcode;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

}
```



##### 3.1.2 양방향

![주 테이블 외래키 일대일 양방향 class diagram](./img/주_테이블_외래키_일대일_양방향_class_diagram.png)

* Orders에 Delivery 외래키가 존재하므로 Orders.delivery가 연관관계 주인이다.
* Delivery의 orders에 mappedBy를 사용하여 주인이 아니라고 설정한다.

```java
// Orders 엔티티 클래스
@Entity
@Table(name = "ORDERS")
@SequenceGenerator(name = "ORDER_NO_SEQ_GENERATOR", sequenceName = "ORDER_NO_SEQ", initialValue = 1, allocationSize = 1)
@Setter
@Getter
@ToString(exclude = { "member", "orderGoods" })
public class Orders {

    @Id
    @Column(length = 20, unique = true)
    @GeneratedValue(generator = "ORDER_NO_SEQ_GENERATOR", strategy = GenerationType.SEQUENCE)
    private Long orderNo;

    @ManyToOne
    @JoinColumn(name = "MEMBER_NO")
    private Member member;

    @OneToOne
    @JoinColumn(name = "DELIVERY_NO")
    private Delivery delivery;

    @OneToMany(mappedBy = "orders", fetch = FetchType.LAZY)
    private List<OrderGoods> orderGoods = new ArrayList<>();

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date orderDt;

    public void setMember(Member member) {

        if (this.member != null) {
            this.member.getOrders().remove(this);
        }

        this.member = member;

        if (member != null) {
            member.getOrders().add(this);
        }
    }

    // Delivery 연관관계 매핑 처리
    public void setDelivery(Delivery delivery) {
        
        this.delivery = delivery;

        if (delivery != null) {
            delivery.setOrder(this);
        }
    }
}

// Delivery 엔티티 클래스
@Entity(name = "DELIVERY")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "order")
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String deliveryNo;

    private String addr;

    private String zipcode;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    
    // 1:1 단방향 매핑
    // ERD상 Orders에 Delivery 외래 키가 존재하므로 mappedBy 속성을 지정
    @OneToOne(mappedBy = "delivery")
    private Orders order;

}
```

