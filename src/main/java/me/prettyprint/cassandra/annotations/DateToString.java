package me.prettyprint.cassandra.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Date: 2010-06-19
 * Time: 10:30:55
 * 
 * @author Artur Socha (artur.socha.mailbox@gmail.com)
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DateToString {
    public String converstionPattern() default "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
}
