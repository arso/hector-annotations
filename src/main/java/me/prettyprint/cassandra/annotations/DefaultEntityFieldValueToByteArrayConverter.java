package me.prettyprint.cassandra.annotations;

import me.prettyprint.cassandra.utils.StringTimeStamp;

import java.lang.reflect.Field;
import java.util.Date;

import static me.prettyprint.cassandra.utils.StringUtils.bytes;

/**
 * Date: 2010-06-19
 * Time: 10:30:30
 *
 * @author Artur Socha (artur.socha.mailbox@gmail.com)
 */

public class DefaultEntityFieldValueToByteArrayConverter implements EntityFieldValueToByteArrayConverter {
    public DefaultEntityFieldValueToByteArrayConverter() {
    }

    @Override
    public byte[] convertEntityFieldValueToBytesArray(Field f, Object value) {
        if (value != null) {
            if (value instanceof Date) {
                value = handleDate(f, value);
            }
        }

        return objectToByteArray(value);
    }

    private Object handleDate(Field f, Object value) {
        //check if additional conversion needed
        if (f.isAnnotationPresent(DateToString.class)) {
            // convert date to string
            StringTimeStamp ts = new StringTimeStamp((Date) value);
            value = ts.getDateString();
        }
        return value;
    }

    private byte[] objectToByteArray(Object obj) {
        if (obj == null) return null;

        if (obj.getClass().equals(byte[].class)) {
            return (byte[]) obj;
        }
        return bytes(obj.toString());
    }
}