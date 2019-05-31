package jpa.study.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "CATEGORY")
@Getter
@Setter
@ToString(exclude = "goodsCategorys")
public class Category {

    @Id
    @GeneratedValue
    @Column(name = "CATEGORY_ID")
    private Long id;
    
    private String name;
    
    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<GoodsCategory> goodsCategorys = new ArrayList<>();
    
}
