# Chapte02 

## 1. JPA 객체 매핑 방법
  1. @Entity
     * 클래스에 설정 가능.
     * 테이블과 매핑한다고 JPA에게 알려줌.
     * @Entity 어노테이션이 선언된 클래스를 엔티티 클래스라고 함.

  2. @Table
     * 엔티티 클래스에 매핑할 테이블 정보 설정.
     * @Table 어노테이션 생략시 테이블 이름을 매핑.

  3. @Id
     * 엔티티 클래스의 필드를 테이블 PK에 매핑하는 설정.
     * @Id 어노테이션이 설정된 필드를 식별자 필드라고 함.

  4. @Column
     * 클래스의 필드를 테이블 컬럼과 매핑.
     * @Column 생략된 필드의 경우 필드명으로 매핑.

## 2. persistence.xml 설정
  * JPA는 persistence.xml 파일을 통하여 설정가능.
  * 클래스패스 META-INF/persistence.xml에 존재할 경우 별다른 설정 없이 인식 가능.
    + 영속성 유닛(persistence unit) 설정부터 시작.
    + xml 상에 name attribute에는 고유의 이름을 부여해야함.
   
    <pre>
    <code>
      <persistence-unit name="jpa명">
    </code>
    </pre>
   
  > JPA 표준 속성
  * javax.persistence.jdbc.driver : JDBC 드라이버
  * javax.persistence.jdbc.user : 데이터베이스 접속 아이디
  * javax.persistence.jdbc.password : 데이터베이스 접속 비밀번호
  * javax.persistence.jdbc.url : 데이터베이스 접속 URL

  > 하이버네이트 속성
  * hibernate.dialect : 데이터베이스 방언 ( Dialect ) 설정

## 3. 데이터베이스 방언
  * JPA는 데이터베이스 벤더에 종속적이지 않은 기술.
  * Dialect 라는 추상 클래스를 통하여 벤더별로 구현된 방언이 존재함.
  * 데이터베이스를 손쉽게 교체할 수 있는 핵심.
  * 하이버네이트 4.3 final 기준 지원하는 방언 <http://docs.jboss.org/hibernate/orm/4.3/manual/en-US/html_single/#configuration-optional-dialects>

  > 대표적인 데이터베이스 별로 상이한 기능
    1. 데이터 타입
      - 가변 문자 타입으로 MySql은 VARCHAR, 오라클은 VARCHAR2를 사용한다.
    2. 다른 함수명
      - 문자열을 자르는 함수로 SQL 표준은 SUBSTRING()을 사용하지만 오라클은 SUBSTR()을 사용한다.
    3. 페이징 처리
      - MySql은 LIMIT를 사용하지만 오라클은 ROWNUM을 사용한다.

---------------------------------------

## 4. 엔티티 매니저 설정
  * EntityManagerFactory
    - 엔티티 매니저 팩토리는 내부적으로 데이터베이스 커넥션 풀 생성.
    - 어플리케이션 전체에서 한번만 생성되도록 싱글톤으로 관리해야함.
    - 어플리케이션 종료시 반드시 close() 호출.
    - persistence.xml 설정 정보를 사용하여 엔티티 매니저 팩토리 생성.
    - <persistence-unit name="jpa명"> 기준으로 아래와 같이 생성
    <pre>
        <code>
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa명");
        </code>
    </pre>

  * EntityManager
    - DML 처리 지원.
    - DB Connection과 밀접한 관계를 가지고 있어서 쓰레드간 자원 공유 or 재사용시 위험함.
    - 엔티티 매니저 사용 종료시 반드시 close() 호출.
    - 엔티티 매니저는 엔티티 매니저 팩토리에 의해서 생성.
    <pre>
        <code>
            EntityManager em = emf.createEntityManager();
        </code>
    </pre>

## 5. EntityManger를 이용한 DML 사용방법
1. INSERT ( 저장 )
<pre>
    <code>
        String id = "id1";
        Member member = new Member();
        member.setId(id);
        member.setUsername("용성");
        member.setAge(2);

        em.persist(member);
    </code>
</pre>

2. UPDATE ( 수정 )
<pre>
    <code>
        member.setAge(20);
    </code>
</pre>

3. DELETE ( 삭제 )
<pre>
    <code>
        em.remove(member);
    </code>
</pre>

4. SELECT ( 조회 )
<pre>
    <code>
        String id = "id1";
        Member findMember = em.find(Member.class, id);
    </code>
</pre>

5. JPQL
  * SQL을 추상화한 JPQL이라는 객체지향 쿼리 언어.
  * JPA는 엔티티 중심으로 개발하므로 테이블이 아닌 엔티티 객체를 대상으로 검색해야함.
  * 모든 데이터를 불러와서 검색하는것은 사실상 불가능하며, 검색조건이 포함될 경우 JPQL을 사용해야함.
<pre>
    <code>
        TypedQuery<Member> memberFindQuery = em.createQuery("select m from Member m", Member.class);
        List<Member> members = memberFindQuery.getResultList()
    </code>
</pre>
 
 
   
