package jpa.study.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "GOODS")
@NoArgsConstructor
@TableGenerator(
        name="GOODS_SEQ_GENERATOR",
        table = "MY_SEQUENCES",
        pkColumnValue = "goodsCd", allocationSize = 1
        )
@Getter
@Setter
@ToString(exclude = "goodsCategorys")
public class Goods {

    @Id
    @Column(unique = true)
    @GeneratedValue(strategy = GenerationType.TABLE, generator="GOODS_SEQ_GENERATOR")
    private Long goodsCd;

    @Column(nullable = false, length = 200)
    private String goodsName;

    @Column(nullable = false)
    private Long goodsPrice;
    
    @OneToMany(mappedBy = "goods", fetch = FetchType.LAZY)
    private List<GoodsCategory> goodsCategorys = new ArrayList<>();

}
