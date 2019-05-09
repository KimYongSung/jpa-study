package ch04.model1.study.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "GOODS")
@NoArgsConstructor
public class Goods {

    @Id
    @Column(unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long goodsCd;

    @Column(nullable = false, length = 20)
    private String goodsType;

    @Column(nullable = false)
    private Long goodsPrice;

    public Long getGoodsCd() {
        return goodsCd;
    }

    public void setGoodsCd(Long goodsCd) {
        this.goodsCd = goodsCd;
    }

    public String getGoodsType() {
        return goodsType;
    }

    public void setGoodsType(String goodsType) {
        this.goodsType = goodsType;
    }

    public Long getGoodsPrice() {
        return goodsPrice;
    }

    public void setGoodsPrice(Long goodsPrice) {
        this.goodsPrice = goodsPrice;
    }

    
}
