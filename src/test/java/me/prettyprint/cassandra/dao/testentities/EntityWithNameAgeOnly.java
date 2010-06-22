package me.prettyprint.cassandra.dao.testentities;

import me.prettyprint.cassandra.annotations.Column;
import me.prettyprint.cassandra.annotations.ColumnFamily;
import me.prettyprint.cassandra.annotations.Key;


/**
 * Date: 2010-06-20
 * Time: 11:05:46
 */
@ColumnFamily(name = "EntityWithDifferentTypesOfFields")
public class EntityWithNameAgeOnly {

    @Key
    private String key;

    @Column
    private String name;
    @Column
    private Integer age;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
