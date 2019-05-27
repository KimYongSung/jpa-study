package ch04.model1.study.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import ch04.model1.study.entity.code.MemberStatus;
import ch04.model1.study.entity.key.DateAndSeqCombinationGenerator;

@Entity
@Table(name = "MEMBER")
public class Member {
    
    @Id
    @Column(length = 20, unique = true)
    @GenericGenerator(name = "seqGenerator"
                    , strategy = "ch04.model1.study.entity.key.DateAndSeqCombinationGenerator"
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

    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    public String getMemberNo() {
        return memberNo;
    }

    public void setMemberNo(String memberNo) {
        this.memberNo = memberNo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MemberStatus getStatus() {
        return status;
    }

    public void setStatus(MemberStatus status) {
        this.status = status;
    }
}
