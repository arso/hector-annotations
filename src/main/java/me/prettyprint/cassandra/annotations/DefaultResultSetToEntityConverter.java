package me.prettyprint.cassandra.annotations;

import me.prettyprint.cassandra.utils.StringTimeStamp;
import org.apache.cassandra.thrift.Column;

import java.math.BigDecimal;
import java.util.Date;

import static me.prettyprint.cassandra.utils.StringUtils.string;

/**
 * Handles converting cassandra column values to entity fields based on field type<n/>
 * WARN! Limited number of types supported
 *
 *
 * Date: 2010-06-19
 * Time: 10:30:30
 *
 * @author Artur Socha (artur.socha.mailbox@gmail.com)
 */
public class DefaultResultSetToEntityConverter implements ResultSetToEntityConverter {
    
    public DefaultResultSetToEntityConverter() {
    }

    @Override
    public <T> T convertFromByteArrayToFieldType(Column c, Class<T> fieldClass) {
        T val;
        if (fieldClass.equals(Long.class)) {
            val = (T) Long.valueOf(string(c.getValue()));
        } else if (fieldClass.equals(Integer.class)) {
            val = (T) Integer.valueOf(string(c.getValue()));
        } else if (fieldClass.equals(Double.class)) {
            val = (T) Double.valueOf(string(c.getValue()));
        } else if (fieldClass.equals(BigDecimal.class)) {
            val = (T) BigDecimal.valueOf(Long.valueOf(string(c.getValue())));
        } else if (fieldClass.equals(String.class)) {
            val = (T) string(c.getValue());
        } else if (fieldClass.equals(Boolean.class)) {
            val = (T) Boolean.valueOf(string(c.getValue()));
        } else if (fieldClass.equals(Date.class)) {
            val = (T) new StringTimeStamp(string(c.getValue())).getDate();
        } else if (fieldClass.equals(byte[].class)) {
            val = (T) c.getValue();
        } else {
            throw new HectorInvalidEntityException("Field type of: " + fieldClass.getCanonicalName() + " is not yet supported");
        }
        return val;
    }
}