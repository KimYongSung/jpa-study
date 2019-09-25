

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

### 1.2 지연로딩

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
    
    @JoinColumn(name = "ORDER_PRICE")
    private Integer orderPrice;

    @ManyToOne
    @JoinColumn(name = "MEMBER_NO")
    private Member member;

    @OneToOne(mappedBy = "order")
    private Delivery delivery;

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
@Entity(name = "DELIVERY")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "order")
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String deliveryNo;
    
    @OneToOne
    @JoinColumn(name = "ORDER_NO")
    private Orders order;

    private String addr;

    private String zipcode;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    
    public void setOrder(Orders order) {
        
        this.order = order;
        
        if(order != null) {
            order.setDelivery(this);
        }
    }
}
```

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