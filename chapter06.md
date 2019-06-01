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