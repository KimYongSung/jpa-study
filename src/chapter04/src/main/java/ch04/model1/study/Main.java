package ch04.model1.study;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import ch04.model1.study.entity.Member;
import ch04.model1.study.entity.Orders;
import ch04.model1.study.entity.code.MemberStatus;

public class Main {

    public static void main(String[] args) {
        
      //엔티티 매니저 팩토리 생성
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("kysStudy");
        EntityManager em = emf.createEntityManager(); //엔티티 매니저 생성

        EntityTransaction tx = em.getTransaction(); //트랜잭션 기능 획득

        try {


            tx.begin(); //트랜잭션 시작
            logic(em);  //비즈니스 로직
            tx.commit();//트랜잭션 커밋

        } catch (Exception e) {
            e.printStackTrace();
            tx.rollback(); //트랜잭션 롤백
        } finally {
            em.close(); //엔티티 매니저 종료
        }

        emf.close(); //엔티티 매니저 팩토리 종료
        
    }
    
    public static void logic(EntityManager em) {
        Member member = new Member();
        member.setId("kys0213");
        member.setPwd("kys0213");
        member.setName("김용성");
        member.setStatus(MemberStatus.A);
        em.persist(member);
        
        Member findMember = em.find(Member.class, member.getMemberNo());
        
        System.out.println(member == findMember);
        
        Orders order = new Orders();
        order.setOrderAddr("우리집");
        em.persist(order);
        System.out.println("orderNo : " + order.getOrderNo());
        
    }
}
