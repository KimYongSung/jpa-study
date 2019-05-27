# Chapte04

[TOC]

## 1. 객체와 테이블 매핑

### 1.1 @Entity

* 테이블과 매핑을 하기위해서는 필수로 선언되야함.
* 기본 생성자는 필수 ( public 또는 protected 생성자)
* final 클래스, enum, interface, inner 클래스는 사용하지 못함.
* 저장할 필드에 final 선언하면 안됨.



#### 1.1.1 @Entity 속성

* name : JPA에서 사용할 엔티티 이름을 지정. ( 기본값 : 클래스명을 그대로 사용)



### 1.2 @TABLE

* 엔티티와 매핑할 테이블을 지정.



#### 1.2.1 @Table 속성

* name : 매핑할 테이블 이름 ( 기본값 : 엔티티 이름을 사용 )

* catalog : catalog 기능이 있는 데이터베이스에서 catalog를 매핑

* schema : schema 기능이 있는 데이터베이스에서 schema를 매핑

* uniqueConstraints ( DDL ) : DDL 생성 시에 유니크 제약조건을 만듬. 해당 기능은 스키마 자동생성 기능을 사용할때만 적용.

  ```java
  @Entity
  @Table(name = "MEMBER"
       , uniqueConstraints = {@UniqueConstraint(name = "", columnNames = {"NAME", "AGE"})}
  )
  public class Member {
      ...
          
      @Column(nullable = false, length = 20)
      private String name;
      
      @Column(nullable = false)
      private Integer age;
      
      ...
  }
  ```

  ```sql
  alter table member 
  add constraint UK_7grc9xe7fcp7fwi54wsxhufk4  unique (name, age)
  ```

### 1.3 데이터베이스 스키마 자동생성

* JPA는 매핑정보와 데이터베이스 방언을 사용해서 데이터베이스 스키마를 생성함.
* 아래 속성 추가시 어플리케이션 실행 시점에 스키마 자동 생성 기능 사용가능.

```xml
<property name="hibernate.hbm2dll.auto" value="create"/>
```

* 기능적으로 완벽하지 않으므로 개발환경에서 사용하거나 학습용도로 사용해야함.



#### 1.3.1 hibernate.hbm2dll.auto 속성

* create : 기존 테이블을 삭제하고 생성한다. ( DROP + CREATE )
* create-drop : create 속성에 추가로 애플리케이션 종료시 생성한 DDL 제거한다. ( DROP + CREATE + DROP )
* update : 데이터베이스 테이블과 엔티티 매핑정보를 비교해서 변경 사항만 수정한다.
* validate : 데이터베이스 테이블과 엔티티 매핑정보를 비교 후 차이가 있을 경우 경고를 남기며 어플리케이션을 실행하지 않는다. ( DDL을 수정하지 않음. )
* none : 자동 생성 기능을 사용하지 않을 경우. none은 유효하지 않은 옵션값이다.



#### 1.3.2 이름 매핑 전략 변경

* 자바 언어는 관례상 카멜표기법을 사용하고, 데이터베이스는 관례상 언더스코어를 사용하여 이름을 표기한다.
* 아래 속성 추가시 일괄적으로 생성이 가능.

```xml
<property name="hibernate.ejb.naming_strategy"        value="org.hibernate.cfg.ImprovedNamingStrategy" /> 
```



## 2. 기본키 매핑

* JPA가 제공하는 데이터베이스 기본 키 생성 전략은 다음과 같다.
  + 직접할당 : 기본 키를 어플리케이션에서 직접 할당한다.
  + 자동생성 : 대리키 사용 방식
    + IDENTITY : 기본 키 생성을 데이터베이스에 위임한다.
    + SEQUENCE : 데이터베이스 시퀀스를 사용해서 기본 키를 할당한다.
    + TABLE : 키 생성 테이블을 사용한다. ( 해당 방식은 모든 데이터베이스에서 사용가능하다. )

* 기본 키 직접 할당시에는 @ID 만 사용하면 가능함.
* 자동 생성 전략을 사용시 @ID에 @GeneratedValue 설정 추가해야함.
* 키 생성 전략 사용시 아래 설정 필요함.
  + 하이버네이트의 구버전 하위호환성을 위하여 기본값이 'false'로 설정되어 있어서 'true'로 변경해야함.

```xml
<property name="hibernate.id.new_generator_mappings" value="true" />
```

* 아래 자바 타입에만 사용 가능함.
  * 자바 기본형
  * 자바 래퍼( wrapper ) 형
  * String
  * java.util.Date
  * java.sql.Date
  * java.math.BigDecimal
  * java.math.BigInteger



### 2.1 기본 키 직접 할당 전략

* @ID로 매핑하여 사용 가능


```java
Member member = new Member();
member.setId("kys0213"); // 기본 키 직접 할당
em.persist(member);
```



### 2.2 IDENTITY 전략

* 기본 키 생성을 데이터베이스에 위임하는 전략.
* MySQL, PostgreSQL, SQL Server, DB2 에서 사용함.

* @GenerateValue 어노테이션에서 strategy 속성 값을 GenerationType.IDENTITY 로 지정해야함.

```java
// 엔티티 클래스
@Entity
public class Goods {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long goodsCd;
    
    public Long getGoodsCd() {
        return goodsCd;
    }

    public void setGoodsCd(Long goodsCd) {
        this.goodsCd = goodsCd;
    }
}

// 저장시
Goods goods = new Goods();
em.persist(goods);
System.out.println("goodsCd : " + goods.getGoodsCd())

// 출력 : goodsCd : 1
```

* IDENTITY 설정시 em.persist 호출 시점에 SQL 쓰기 지연 저장소가 아니라 바로 데이터베이스에 전달된다.



### 2.3 SEQUENCE 전략

* 데이터베이스 시퀀스를 사용하여 기본키를 생성하는 전략.
* ORACLE, PostgreSQL, DB2, H2 데이터베이스에서 사용함.
* @SequenceGenerator를 사용하여 시퀀스 등록
* @GenerateValue 어노테이션에서 strategy 속성 값과 @SequenceGenerator설정으로 등록한 시퀀스 를 지정해야함.

```java
// 엔티티 클래스
@Entity
@Table(name = "ORDERS")
@SequenceGenerator(
        name = "ORDER_NO_SEQ_GENERATOR",
        sequenceName = "ORDER_NO_SEQ",
        initialValue = 1, allocationSize = 1)
public class Orders {

    @Id
    @Column(length = 20, unique = true)
    @GeneratedValue(generator = "ORDER_NO_SEQ_GENERATOR"
                    , strategy = GenerationType.SEQUENCE)
    private Long orderNo;

    private String orderAddr;

    public Long getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }
 
    public String getOrderAddr() {
        return orderAddr;
    }

    public void setOrderAddr(String orderAddr) {
        this.orderAddr = orderAddr;
    }
}

// 저장시
Orders order = new Orders();
order.setOrderAddr("우리집");
em.persist(order);
System.out.println("orderNo : " + order.getOrderNo());

// 출력  
// orderNo : 1
```



#### 2.3.1 @SequenceGenerator 속성

* name : 식별자 생성기 이름 `( 필수 설정 )`
* sequenceName : 데이터베이스에 등록되어 있는 시퀀스 이름
* initialValue : DDL 생성 시에만 사용됨. 시퀀스 DDL 생성시 처음 시작하는 수 지정 `( 기본값 : 1 )`

* allocationSize : 시퀀스 한 번 호출에 증가하는 수 ( 성능 최적화에 사용함. **기본값 : 50** )

  + 아래와 같은 순서로 시퀀스를 호출하여 기본키 생성
    1. 데이터베이스 시퀀스를 조회
    2. 조회한 시퀀스를 기본키 값으로 사용하여 저장 

  + 성능 최적화를 위하여 **한번에 1~50까지의 시퀀스를 조회하여 메모리에 캐싱**함.
  + 다만 실제 데이터베이스에서 시퀀스 증가 값이 1로 지정된 경우 1로 설정해야함.

* catalog, schema : 데이터베이스 catalog, schema 이름



#### 2.3.2 CustomSequenceGenerator 사용법

* @GenericGenerator 과 @GeneratedValue를 설정하여 사용 가능.

* hiberate가 제공하는 interface를 통하여 사용자 지정 시퀀스를 생성할 수 있음.
  + org.hibernate.id.IdentifierGenerator 
  + org.hibernate.id.Configurable

* IdentifierGenerator : 데이터베이스에서 시퀀스를 조회하여 사용자 지정 시퀀스 생성가능.
* Configurable : @GenericGenerator 를 통하여 sequence 생성시점에 파라미터도 지정할 수 있다.

```java
//날짜(YYYYMMDDHH24MISS) + SEQ(6) 조합 시퀀스 생성 클래스
public class DateAndSeqCombinationGenerator implements IdentifierGenerator, Configurable{
    
    public static final String SEQUENCE_NAME_KEY = "sequenceName";
    
    private String sequenceName;
    
    // IdentifierGenerator 구현 메소드
    @Override
    public Serializable generate(SessionImplementor session, Object object) throws HibernateException {

        Connection connection = session.connection();
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT nextval ('"+sequenceName+"') as nextval");
 
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                int id = rs.getInt("nextval");
                return DateUtil.getCurrent() + StringUtil.lPad(String.valueOf(id), 6, "0");
            }
            
            throw new HibernateException(sequenceName + " is no rows");
        }catch (SQLException e) {
            throw new HibernateException("Unable to generate Sequence", e);
        }
    }

    // Configurable 구현 메소드
    @Override
    public void configure(Type type, Properties params, Dialect d) throws MappingException {
        this.sequenceName = ConfigurationHelper.getString(SEQUENCE_NAME_KEY, params);
    }
}

// 엔티티 클래스
@Entity
@Table(name = "MEMBER")
public class Member {
    
    @Id
    @Column(length = 20, unique = true)
    @GenericGenerator(name = "seqGenerator"
                    , strategy = "패키지명.DateAndSeqCombinationGenerator"
                     // 시퀀스 생성시 사용할 파라미터 설정
                    , parameters =@org.hibernate.annotations.Parameter( 
                                 name = DateAndSeqCombinationGenerator.SEQUENCE_NAME_KEY
                                ,value = "MEMBER_NO_SEQ"
                                )
                    )
    @GeneratedValue(generator = "seqGenerator")
    private String memberNo;
    
    ...
}    
```



### 2.4 TABLE 전략

* 키 생성 전용 테이블을 생성하여 시퀀스를 흉내내는 전략.
* 키 생성을 위해서 데이터베이스에 SELECT, UPDATE 를 통한 두번 통신.
* 최적화 필요시 allocationSize  속성 값의 크기를 지정.
* 시쿼스 이름과 값 컬럼을 사용

```sql
CREATE TABLE MY_SEQUENCES (
	SEQUENCE_NAME VARCHAR(255) NOT NULL,
    NEXT_VAL BIGINT,
    PRIMARY KEY ( SEQUENCE_NAME )
)
```

```java
@Entity
@Table(name = "GOODS")
@NoArgsConstructor
@TableGenerator(
        name="GOODS_SEQ_GENERATOR",
        table = "MY_SEQUENCES",
        pkColumnValue = "goodsCd", allocationSize = 1
        )
public class Goods {

    @Id
    @Column(unique = true)
    @GeneratedValue(strategy = GenerationType.TABLE, generator="GOODS_SEQ_GENERATOR")
    private Long goodsCd;

    @Column(nullable = false, length = 200)
    private String goodsName;

    @Column(nullable = false)
    private Long goodsPrice;
    
    ...
}

Goods goods = new Goods();
goods.setGoodsPrice(1000l);
goods.setGoodsName("허쉬초코우유");
em.persist(goods);
System.out.println("goodsCd : " + goods.getGoodsCd());
// goodsCd : 1
```

> MY_SEQUENCES 테이블 

| SEQUENCE_NAME | NEXT_VAL |
| :------------ | :------- |
| goodsCd       | 2        |

#### 2.4.1 @TableGenerator 속성

* name : 식별자 생성기 이름 ( **필수** )

* table : 키생성 테이블명 ( 기본값 : hibernate_sequences )
* pkColumnName : 시퀀스 컬럼명 ( 기본값 : sequence_name )
* valueColumnName : 시퀀스 값 컬럼명 ( 기본값 : next_val )
* pkColumnValue : 키로 사용할 값 이름
* initialValue : 초기 값. 
* allocationSize : 시퀀스 한 번 호출에 증가하는 수
* catalog, schema : 데이터베이스 catalog, scheme 이름
* uinqueConstrationts : 유니크 제약 조건을 지정.



### 2.5 AUTO 전략

* 데이터베이스 방언에 따라서 자동으로 선택하여 키 생성 전략 사용.



## 3. 필드와 컬럼 매핑

### 3.1 @Column

* 객체 필드를 테이블 컬럼에 매핑한다.
* 설정 생략시 **자바 기본 타입일 경우 nullable = false 로 지정하는것이 안전**하다.
  * int 형으로 선언된 필드에 데이터가 없는 경우


#### 3.1.2 @Column 속성

* name : 필드와 매핑할 테이블의 컬럼 이름 ( 기본값 : 객체의 필드 이름 )

* insertable : 엔티티 저장 시 이 필드도 같이 저장. false 설정시 해당 필드는 저장하지 않는다. ( 기본값 : true )

* updatable : 엔티티 수정 시 이필드도 같이 수정한다. false 설정시 수정하지 않는다. ( 기본값 : true )

* table : 하나의 엔티티를 두개 이상의 테이블에 매핑할 때 사용.

* nullable ( DDL ) : null 값의 허용 여부를 설정. false 설정시 DDL 생성시에 NOT NULL 제약조건 생성. ( 기본값 : true )

  ```java
  @Column(nullable = false)
  private Long goodsPrice;
  ```

  ```sql
  //생성된 DDL
  goods_price bigint not null
  ```

* unique ( DDL ): 한 컬럼에 간단한 유니크 제약조건을 걸 때 사용. **두 컬럼 이상 지정**시 클래스 레벨에서 `@Table.uniqueConstraints` 를 지정해야함.

  ```java
  @Column(unique = true)
  private Long goodsCd;
  ```

  ```sql
  //생성된 DDL
  goods_cd bigint not null
  primary key (goods_cd)
  ```

* columnDefinition ( DDL ) : 데이터 베이스 컬럼 정보를 직접 줄 수 있다. 

  ```java
  @Column(columnDefinition = "varchar(100) default 'EMPTY'")
  private String desc;
  ```

  ```sql
  //생성된 DDL
  desc varchar(100) default 'EMPTY'
  ```

* precision,scale ( DDL ) : BigDecimal, BigInteger 타입에서 사용함.  precision은 소숫점을 포함한 전체 자릿수를, scale은 소수의 자릿수.

### 3.2 @Enumerated

* 자바의 enum 타입을 매핑할 때 사용한다.

#### 3.2.1 @Enumerated name 속성

* EnumType.ORDINAL :  enum 순서를 데이터베이스에 저장. ( 기본값 )
  * 데이터베이스에 저장되는 크기가 작다.
  * 이미 저장된 enum 순서를 변경할 수 없다.
* EnumType.STRING : enum 이름을 데이터베이스에 저장.
  * 저장된 enum 의 순서가 바귀거나 enum이 추가되어도 안전하다.
  * 데이터베이스에 저장되는 크기가 ORDINAL 보다 크다.

### 3.3 @Temporal

* 날짜 타입 `java.util.Date`, `java.util.Calendar` 매핑시 사용.
* java8 이상부터 등장한 `java.time.LocalDate, java.time.LocalDateTime` 매핑시 JPA2.2 이상 ( Hibernate 5 이상) 사용시 매핑 가능.

```java
@Temporal(TemporalType.DATE)
private Date regDate;

@Temporal(TemporalType.TIME)
private Date regTime;

@Temporal(TemporalType.TIMESTAMP)
private Date regTimeStamp;
```

```sql
reg_date date,
reg_time time,
reg_time_stamp timestamp
```

### 3.4 @Lob

* 데이터베이스 BLOB, CLOB 타입과 매핑.
* 지정할 수 있는 속성이 없음.
* 매핑하는 필드 타입이 문자인 경우 CLOB, 나머지는 BLOB으로 매핑.
  * CLOB : String, char[], java.sql.CLOB
  * BLOB : byte[], java.sql.BLOB

### 3.5 @Transient

* 데이터베이스에 저장, 조회시 매핑하지 않음.
* 임시로 값을 보관하는 용도로 사용함.

### 3.6 @Access

* JPA가 엔티티 데이터에 접근하는 방식을 지정.
* AccessType.FIELD : 필드로 직접 접근. private이어도 접근이 가능하다.
* AccessType.PROPERTY : getter로 접근한다.