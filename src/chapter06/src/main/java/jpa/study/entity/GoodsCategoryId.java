package jpa.study.entity;

import java.io.Serializable;

import lombok.EqualsAndHashCode;


@SuppressWarnings("serial")
@EqualsAndHashCode
public class GoodsCategoryId implements Serializable{

    private Long goodsCd;
    
    private Long categoryId;
}
