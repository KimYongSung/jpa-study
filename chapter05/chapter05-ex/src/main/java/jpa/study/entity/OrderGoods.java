package jpa.study.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import jpa.study.entity.key.DateAndSeqCombinationGenerator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ORDER_GOODS")
@NoArgsConstructor
@Getter
public class OrderGoods {

    @Id
    @Column(length = 20, unique = true)
    @GenericGenerator(name = "seqGenerator"
                    , strategy = "jpa.study.entity.key.DateAndSeqCombinationGenerator"
                    , parameters = @org.hibernate.annotations.Parameter(
                                  name = DateAndSeqCombinationGenerator.SEQUENCE_NAME_KEY
                                , value = "ORDER_GOODS_NO_SEQ"
                                )
                    )
    @GeneratedValue(generator = "seqGenerator")
    @Setter
    private String orderGoodsNo;
    
    @ManyToOne
    @JoinColumn(name="ORDER_NO")
    private Orders orders;

    // 주문상품 
    @ManyToOne
    @JoinColumn(name = "GOODS_CD")
    @Setter
    private Goods goods;

    // 상품 갯수 
    @Column(nullable = false)
    @Setter
    private Integer goodsCnt;
    
    public void setOrders(Orders orders) {
        if(this.orders != null) {
            this.orders.getOrderGoods().remove(this);
        }
        
        this.orders = orders;
        orders.getOrderGoods().add(this);
    }

    @Override
    public String toString() {
        return "OrderGoods [orderGoodsNo=" + orderGoodsNo + ", orders=" + orders + ", goods=" + goods + ", goodsCnt="
                + goodsCnt + "]";
    }
}
