package jpa.study.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import jpa.study.entity.code.OrderStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 배송 정보
 *
 * @author kys0213
 * @date   2019. 5. 31.
 */
@Entity(name = "DELIVERY")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "order")
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String deliveryNo;

    private String addr;

    private String zipcode;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    
    @OneToOne(mappedBy = "delivery")
    private Orders order;

}
