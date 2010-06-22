package me.prettyprint.cassandra.dao;

/**
 * Date: 2010-06-07
 * Time: 07:44:05
 *
 * @author Artur Socha (artur.socha.mailbox@gmail.com)
 */
public class DAOCassandraConnectivityException extends RuntimeException {
    
    public DAOCassandraConnectivityException(Throwable e) {
        super(e);
    }
    public DAOCassandraConnectivityException(String msg) {
        super(msg);
    }
}
