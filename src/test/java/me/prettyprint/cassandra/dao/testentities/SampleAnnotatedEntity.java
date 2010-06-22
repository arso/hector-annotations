package me.prettyprint.cassandra.dao.testentities;


import me.prettyprint.cassandra.annotations.Column;
import me.prettyprint.cassandra.annotations.ColumnFamily;
import me.prettyprint.cassandra.annotations.DateToString;
import me.prettyprint.cassandra.annotations.Key;

import java.util.Date;


/**
 * Date: 2010-06-19
 * Time: 10:38:58
 */
@ColumnFamily(name="SampleCF")
public class SampleAnnotatedEntity {
    @Key
    private String key;

    @Column
    private String name;

    @Column
    private Integer id;

    @Column
    private Date createDate;
    
    @Column
    @DateToString
    private Date anotherDate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getAnotherDate() {
        return anotherDate;
    }

    public void setAnotherDate(Date anotherDate) {
        this.anotherDate = anotherDate;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
