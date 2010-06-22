package me.prettyprint.cassandra.annotations;

import java.lang.reflect.Field;

/**
 * Date: 2010-06-19
 * Time: 10:30:30
 *
 * @author Artur Socha (artur.socha.mailbox@gmail.com)
 */
public interface EntityFieldValueToByteArrayConverter {
    byte[] convertEntityFieldValueToBytesArray(Field f, Object value);
}
