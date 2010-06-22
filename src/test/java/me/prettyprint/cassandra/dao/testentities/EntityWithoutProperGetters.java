package me.prettyprint.cassandra.dao.testentities;

import me.prettyprint.cassandra.annotations.Column;
import me.prettyprint.cassandra.annotations.ColumnFamily;
import me.prettyprint.cassandra.annotations.Key;

/**
 * Date: 2010-06-19
 * Time: 13:21:39
 */
@ColumnFamily(name = "entityWithoutProperGetters")
public class EntityWithoutProperGetters {


    @Key
    private String key;

    @Column
    private String id;
    @Column
    private String name;

    private String getAnotherId(){
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
