package me.prettyprint.cassandra.dao.testentities;

import me.prettyprint.cassandra.annotations.ColumnFamily;
import me.prettyprint.cassandra.annotations.Key;

/**
 * Date: 2010-06-19
 * Time: 16:20:13
 */
@ColumnFamily(name=" ")
public class EntityWithEmptyName {
    @Key
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
