package me.prettyprint.cassandra.dao.testentities;

import me.prettyprint.cassandra.annotations.Column;
import me.prettyprint.cassandra.annotations.ColumnFamily;
import me.prettyprint.cassandra.annotations.Key;


/**
 * Date: 2010-06-19
 * Time: 23:00:31
 */
@ColumnFamily(name = "EntityWithDifferentTypesOfFields")
public class EntityWithDifferentTypesOfFields {

    @Key
    private String key;
    @Column
    private String name;
    @Column
    private Integer age;
    @Column
    private Long moneyAmount;
    @Column
    private byte[] pictureBlob;


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

    public Long getMoneyAmount() {
        return moneyAmount;
    }

    public void setMoneyAmount(Long moneyAmount) {
        this.moneyAmount = moneyAmount;
    }

    public byte[] getPictureBlob() {
        return pictureBlob;
    }

    public void setPictureBlob(byte[] pictureBlob) {
        this.pictureBlob = pictureBlob;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
