package jpa.study.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ORDERS")
@SequenceGenerator(
        name = "ORDER_NO_SEQ_GENERATOR",
        sequenceName = "ORDER_NO_SEQ",
        initialValue = 1, allocationSize = 1)
@Setter
@Getter
public class Orders {

    @Id
    @Column(length = 20, unique = true)
    @GeneratedValue(generator = "ORDER_NO_SEQ_GENERATOR", strategy = GenerationType.SEQUENCE)
    private Long orderNo;

    @ManyToOne
    @JoinColumn(name="MEMBER_NO")
    private Member member;
    
    @OneToMany(mappedBy = "orders")
    private List<OrderGoods> orderGoods = new ArrayList<>();

    private String orderAddr;
    
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date orderDt;

    public void setMember(Member member) {
        
        if(this.member != null) {
            this.member.getOrders().remove(this);
        }
        
        this.member = member;
        
        member.getOrders().add(this);
    }
    
    @Override
    public String toString() {
        return "Orders [orderNo=" + orderNo + ", member=" + member + ", orderAddr="
                + orderAddr + ", orderDt=" + orderDt + "]";
    }
}
