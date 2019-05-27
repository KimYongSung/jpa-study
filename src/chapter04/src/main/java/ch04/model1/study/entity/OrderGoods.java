package ch04.model1.study.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import ch04.model1.study.entity.key.DateAndSeqCombinationGenerator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ORDER_GOODS")
@NoArgsConstructor
@Getter
@Setter
public class OrderGoods {

    @Id
    @Column(length = 20, unique = true)
    @GenericGenerator(name = "seqGenerator"
                    , strategy = "ch04.model1.study.entity.key.DateAndSeqCombinationGenerator"
                    , parameters = @org.hibernate.annotations.Parameter(
                                  name = DateAndSeqCombinationGenerator.SEQUENCE_NAME_KEY
                                , value = "ORDER_GOODS_NO_SEQ"
                                )
                    )
    @GeneratedValue(generator = "seqGenerator")
    private String orderGoodsNo;

    // 주문번호 
    @Column(nullable = false, length = 20)
    private String orderNo;

    // 주문상품 
    @Column(nullable = false)
    private Integer goodsCd;

    // 상품 갯수 
    @Column(nullable = false)
    private Integer goodsCnt;

}
