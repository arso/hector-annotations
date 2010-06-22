package me.prettyprint.cassandra.dao.testentities;

import me.prettyprint.cassandra.annotations.Column;
import me.prettyprint.cassandra.annotations.ColumnFamily;
import me.prettyprint.cassandra.annotations.Key;

/**
 * Date: 2010-06-19
 * Time: 19:11:21
 */
@ColumnFamily(name = "EntityWithKey")
public class EntityWithKeyAnnotationDefined {

    @Key
    private String id ;

    @Column
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
