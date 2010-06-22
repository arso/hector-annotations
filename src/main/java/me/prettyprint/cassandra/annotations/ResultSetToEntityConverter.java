package me.prettyprint.cassandra.annotations;

import org.apache.cassandra.thrift.Column;

/**
 * Date: 2010-06-20
 * Time: 11:47:16
 *
 * @author Artur Socha (artur.socha.mailbox@gmail.com)
 */
public interface ResultSetToEntityConverter {
    <T> Object convertFromByteArrayToFieldType(Column c, Class<T> fieldClass);
}
