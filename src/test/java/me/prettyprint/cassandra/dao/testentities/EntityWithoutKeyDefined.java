package me.prettyprint.cassandra.dao.testentities;

import me.prettyprint.cassandra.annotations.Column;
import me.prettyprint.cassandra.annotations.ColumnFamily;

/**
 * Date: 2010-06-20
 * Time: 11:31:41
 */
@ColumnFamily(name = "EntityWithoutKeyDefined")
public class EntityWithoutKeyDefined {
    
    @Column
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
