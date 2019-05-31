package jpa.study.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity(name = "GOODS_CATEGORY")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class GoodsCategory {
    
    @Id 
    @GeneratedValue
    @Column(name = "GOODS_CATEGORY_ID")
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "GOODS_CD")
    private Goods goods;
    
    @ManyToOne
    @JoinColumn(name = "CATEGORY_ID")
    private Category category;
    
    public void setGoods(Goods goods) {
        
        if(this.goods != null) {
            this.goods.getGoodsCategorys().remove(this);
        }
        
        this.goods = goods;
        
        if(goods != null) {
            goods.getGoodsCategorys().add(this);
        }
    }
    
    public void setCategory(Category category) {
        
        if(this.category != null) {
            this.category.getGoodsCategorys().remove(this);
        }
        
        this.category = category;
        
        if(category != null) {
            category.getGoodsCategorys().add(this);
        }
    }
}
