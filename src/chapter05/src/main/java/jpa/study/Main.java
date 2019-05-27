package jpa.study;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import jpa.study.entity.Goods;
import jpa.study.entity.Member;
import jpa.study.entity.OrderGoods;
import jpa.study.entity.Orders;
import jpa.study.entity.code.MemberStatus;

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
    
    public static void jpqlTest(EntityManager em, Member member) {
     
        String jpql = "select o from OrderGoods o join o.goods g where g.goodsCd = :goodsCd";
        
        List<OrderGoods> resultList = em.createQuery(jpql, OrderGoods.class)
                                        .setParameter("goodsCd", 1l)
                                        .getResultList();
        
        System.out.println(resultList);
    }
    
    
    public static void logic(EntityManager em) {
        Goods goods = make허쉬초코우유(em);
        Goods goods2 = make베지밀(em);
        
        Member member = makeMember(em);
        
        Orders order = 주문요청(em, member, "우리집");
        Orders order2 = 주문요청(em, member, "우리집");
        
        상품구매(em, goods, order, 3);
        상품구매(em, goods2, order, 1);
        
        상품구매(em, goods, order2, 3);
        상품구매(em, goods2, order2, 1);
        
        jpqlTest(em, member);
        
        Orders findOrder = em.find(Orders.class, order.getOrderNo());
        
        System.out.println(findOrder);
        
        List<OrderGoods> orderGoods = findOrder.getOrderGoods();
        
        System.out.println(orderGoods);
        
        Member findMember = em.find(Member.class, member.getMemberNo());
        
        System.out.println(findMember);
        
        System.out.println(findMember.getOrders());
    }

    private static Orders 주문요청(EntityManager em, Member member, String addr) {
        Orders order = new Orders();
        order.setOrderAddr(addr);
        order.setMember(member);
        order.setOrderDt(new Date());
        em.persist(order);
        return order;
    }

    private static OrderGoods 상품구매(EntityManager em, Goods goods, Orders order, int goodsCnt) {
        OrderGoods orderGoods = new OrderGoods();
        orderGoods.setGoods(goods);
        orderGoods.setOrders(order);
        orderGoods.setGoodsCnt(goodsCnt);
        em.persist(orderGoods);
        order.getOrderGoods().add(orderGoods);
        return orderGoods;
    }

    private static Goods make베지밀(EntityManager em) {
        Goods goods2 = new Goods();
        goods2.setGoodsPrice(1000l);
        goods2.setGoodsName("베지밀");
        em.persist(goods2);
        return goods2;
    }

    private static Goods make허쉬초코우유(EntityManager em) {
        Goods goods = new Goods();
        goods.setGoodsPrice(1000l);
        goods.setGoodsName("허쉬초코우유");
        em.persist(goods);
        return goods;
    }

    private static Member makeMember(EntityManager em) {
        Member member = new Member();
        member.setId("kys0213");
        member.setPwd("kys0213");
        member.setName("김용성");
        member.setAge(30);
        member.setStatus(MemberStatus.A);
        em.persist(member);
        return member;
    }
}
