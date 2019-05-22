package jpa.study.entity.key;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.type.Type;

import jpa.study.util.DateUtil;
import jpa.study.util.StringUtil;
import lombok.NoArgsConstructor;

/**
 * 시퀀스 생성
 * 
 * <li> 날짜(YYYYMMDDHH24MISS) + SEQ(6) 조합</li>
 * <br>  
 * @author kys0213
 * @date   2019. 5. 7.
 */
@NoArgsConstructor
public class DateAndSeqCombinationGenerator implements IdentifierGenerator, Configurable{
    
    public static final String SEQUENCE_NAME_KEY = "sequenceName";
    
    private String sequenceName;
    
    @Override
    public Serializable generate(SessionImplementor session, Object object) throws HibernateException {

        Connection connection = session.connection();
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT nextval ('"+sequenceName+"') as nextval");
 
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                int id = rs.getInt("nextval");
                return DateUtil.getCurrent() + StringUtil.lPad(String.valueOf(id), 6, "0");
            }
            
            throw new HibernateException(sequenceName + " is no rows");
        }catch (SQLException e) {
            throw new HibernateException("Unable to generate Sequence", e);
        }
    }

    @Override
    public void configure(Type type, Properties params, Dialect d) throws MappingException {
        this.sequenceName = ConfigurationHelper.getString(SEQUENCE_NAME_KEY, params);
    }

}
