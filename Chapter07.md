# Chapter07

## 1. 상속관계매핑

* 관계형 데이터베이스에는 상속이라는 개념 대신 슈퍼타입 서브타입 관계 모델링 기업이 객체의 상속 개념과 가장 유사하다.
* ORM에서 이야기 하는 상속 관계 매핑은 객체의 상속 구조와 데이터베이스의 슈퍼타입 서브타입 관계를 매핑하는 것.

### 1.1  슈퍼타입 서브타입 논리 모델 구현 방식

![객체 상속 모델](./img/객체 상속 모델.png)

* 각각의 테이블로 변환
* 통합 테이블로 변환
* 서브타입 테이블로 변환



### 1.2 조인전략

![조인전략 ERD](./img/객체상속모델_조인전략.png)

* 자식테이블이 부모테이블의 기본 키를 받아서 키 + 외래 키로 사용하는 전략이다.

* 조회할 때 조인이 자주 사용된다.

* 타입을 구분하는 컬럼을 추가하여 사용한다.

```java
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "GOODS")
@TableGenerator(
        name="GOODS_SEQ_GENERATOR",
        table = "MY_SEQUENCES",
        pkColumnValue = "goodsCd", allocationSize = 1
        )
@NoArgsConstructor
@Getter
@Setter
@ToString(exclude = "goodsCategorys")
public abstract class Goods {

    @Id
    @Column(unique = true)
    @GeneratedValue(strategy = GenerationType.TABLE, generator="GOODS_SEQ_GENERATOR")
    private Long goodsCd;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false)
    private Long price;
    
    @Enumerated
    private GoodsType goodsType;
    
    @OneToMany(mappedBy = "goods", fetch = FetchType.LAZY)
    private List<GoodsCategory> goodsCategorys = new ArrayList<>();

    public Goods(String name, Long price) {
        super();
        this.name = name;
        this.price = price;
    }
}

// Goods 를 상속받은 chicken 메뉴 정보 테이블
@Entity
@Table(name = "GOODS_CHICHEN")
@DiscriminatorValue("C")
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class Chicken extends Goods{

    @Column(nullable = false)
    private Long size;
    
}

// Goods 를 상속받은 Drink 메뉴 정보 테이블
@Entity
@Table(name = "GOODS_DRINK")
@DiscriminatorValue("D")
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class Drink extends Goods{

    @Column(nullable = false)
    private Long ml;
}

// Goods 를 상속받은 Hamburger 메뉴 정보 테이블
@Entity
@Table(name = "GOODS_DRINK")
@DiscriminatorValue("H")
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class Hamburger extends Goods{

    @Column(nullable = false)
    private Long ml;
}
```

#### 1.2.1 조인전략 설정

##### 1.2.1.1 @Inheritance

* 상속 매핑은 @Inheritance를 사용해야함.
* 매핑적략은 조인테이블 이므로 `InheritanceType.JOINED` 를 사용한다.

##### 1.2.1.2 @DiscriminatorColumn

* 부모 클래스에 구분 컬럼을 지정한다. 
* 해당 컬럼으로 저장된 자식 테이블을 구분할 수 있다.
* 기본값이 DTYPE이므로 @DiscriminatorColmun으로 줄여서 사용 가능하다.

##### 1.2.1.3 @DiscriminatorValue

* 엔티티를 저장할 때 구분 컬럼에 입력할 값을 지정한다.

##### 1.2.1.4 @PrimaryKeyJoinColumn

* 자식테이블의 기본키 컬럼병을 변경하고 싶을 경우 사용한다.

#### 1.2.2 조인전략 장단점

* 장점
  * 테이블이 정규화된다.
  * 외래 키 참조 무결성 제약조건을 활용할 수 있다.
  * 저장공간을 효율적으로 사용한다.
* 단점
  * 조회할 때 조인이 맣이 사용되므로 성능이 저하될 수 있다.
  * 조회 쿼리가 복잡하다.
  * 데이터를 등록할 INSERT SQL을 두 번 실행한다.