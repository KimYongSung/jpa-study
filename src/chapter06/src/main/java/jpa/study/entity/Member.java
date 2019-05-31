package jpa.study.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;

import jpa.study.entity.code.MemberStatus;
import jpa.study.entity.key.DateAndSeqCombinationGenerator;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "MEMBER"
     , uniqueConstraints = {@UniqueConstraint(name = "", columnNames = {"NAME", "AGE"})}
)
@Getter
@Setter
@ToString(exclude = "orders")
public class Member {
    
    @Id
    @Column(length = 20, unique = true)
    @GenericGenerator(name = "seqGenerator"
                    , strategy = "jpa.study.entity.key.DateAndSeqCombinationGenerator"
                    ,parameters =@org.hibernate.annotations.Parameter(
                                 name = DateAndSeqCombinationGenerator.SEQUENCE_NAME_KEY
                                ,value = "MEMBER_NO_SEQ"
                                )
                    )
    @GeneratedValue(generator = "seqGenerator")
    private String memberNo;

    @Column(nullable = false, length = 20)
    private String id;

    @Column(nullable = false, length = 20)
    private String pwd;

    @Column(nullable = false, length = 20)
    private String name;
    
    @Column(nullable = false)
    private Integer age;
    
    @Enumerated(EnumType.STRING)
    private MemberStatus status;
    
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private List<Orders> orders = new ArrayList<>();

}
