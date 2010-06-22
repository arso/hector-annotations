package me.prettyprint.cassandra.annotations;

/**
 * Date: 2010-06-19
 * Time: 15:33:49
 */
public class HectorInvalidEntityException extends RuntimeException {
    public HectorInvalidEntityException(String msg) {
        super(msg) ;
    }

    public HectorInvalidEntityException(String msg, Throwable cause) {
        super(msg,cause);
    }
}
