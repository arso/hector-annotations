package me.prettyprint.cassandra.dao.testentities;

import me.prettyprint.cassandra.annotations.ColumnFamily;
import me.prettyprint.cassandra.annotations.Key;

/**
 * Date: 2010-06-19
 * Time: 13:19:47
 */
@ColumnFamily(name = "NoPersistableFields")
public class EntityWithoutPersistableFields {

    @Key
    private String key;

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
