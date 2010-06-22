package me.prettyprint.cassandra.annotations;

import me.prettyprint.cassandra.utils.UUIDUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static me.prettyprint.cassandra.utils.StringUtils.string;

/**
 * Date: 2010-06-19
 * Time: 10:30:30
 *
 * @author Artur Socha (artur.socha.mailbox@gmail.com)
 */
public class HectorTemplateAnnotationProcessor {

    private Logger logger = LoggerFactory.getLogger(HectorTemplateAnnotationProcessor.class);

    private ResultSetToEntityConverter resultSetToEntityConverter ;
    private EntityFieldValueToByteArrayConverter entityFieldValueToByteArrayConverter;

    public HectorTemplateAnnotationProcessor(){
        this.resultSetToEntityConverter = new DefaultResultSetToEntityConverter();
        this.entityFieldValueToByteArrayConverter = new DefaultEntityFieldValueToByteArrayConverter();
    }


    public <T> T getEntityWithResults(List<org.apache.cassandra.thrift.Column> columns, Class<T> entityClass){
        T entity    = null;
        try {
            entity  = entityClass.newInstance();
            for (org.apache.cassandra.thrift.Column c : columns) {
                try {
                    Field f = entity.getClass().getDeclaredField(string(c.getName()));
                    Class fieldClass = f.getType();
                    if (c.getValue() == null){
                        setPropertyValue(entity,string(c.getName()),null);
                        continue;
                    }
                    Object val = resultSetToEntityConverter.convertFromByteArrayToFieldType(c, fieldClass);

                    logger.debug("Setting property "+f.getName()+" with value: "+val);
                    setPropertyValue(entity,string(c.getName()),val);
                } catch (NoSuchFieldException e) {
                    logger.debug("No field corresponding to column name"+string(c.getName())+" found. Entity of class: : "+ entityClass.getCanonicalName());
                }
            }
        } catch (Exception e) {
            throw new HectorInvalidEntityException("Could not instantiate entity of class "+entityClass.getCanonicalName(),e);
        }
        return entity;
    }

    public List<org.apache.cassandra.thrift.Column> getColumns(Object entity){
        validateEntity(entity);

        Class clazz = entity.getClass();
        ArrayList<org.apache.cassandra.thrift.Column> columns = new ArrayList<org.apache.cassandra.thrift.Column>();


        for (Field f : clazz.getDeclaredFields()){
            if (f.isAnnotationPresent(Column.class)){
                String fName = f.getName();
                Object value = getPopertyValue(entity, fName);

                byte [] valueBytes = entityFieldValueToByteArrayConverter.convertEntityFieldValueToBytesArray(f, value);

                Long timestamp = System.currentTimeMillis();
                org.apache.cassandra.thrift.Column c = new org.apache.cassandra.thrift.Column(fName.getBytes(),valueBytes, timestamp);
                columns.add(c);
            }
        }

        if (columns.isEmpty()){
            throw new HectorInvalidEntityException("Attempt to process value object of class: "+clazz.getCanonicalName()
                    +"  that does not have at leas a single persistable field defined (annotated with "+Column.class.getCanonicalName()+")");
        }
        return columns;
    }

    public String extractColumnFamilyName(Object entity) {
        validateEntity(entity);

        return extractColumnFamilyName(entity.getClass());
    }

    public String extractColumnFamilyName(Class entityClass) {
        validateEntity(entityClass);

        ColumnFamily cfAnnotation = (ColumnFamily) entityClass.getAnnotation(ColumnFamily.class);

        if (cfAnnotation.name().trim().isEmpty()) throw new  HectorInvalidEntityException("Entity name cannot be empty string. Class: "+entityClass.getCanonicalName());

        return cfAnnotation.name();
    }

    public String extractKey(Object entity){
        validateEntity(entity);
        for (Field f: entity.getClass().getDeclaredFields()){
            if (f.isAnnotationPresent(Key.class)){
                String  key = (String) getPopertyValue(entity,f.getName());
                if (key == null){
                    logger.debug("UUID has been generated as key for entity "+entity.getClass().getCanonicalName());
                    key = generateKey(entity, f);
                }
                logger.debug("UUID for "+entity.getClass().getCanonicalName() + " is: "+key);
                return  key;
            }
        }
        throw new HectorInvalidEntityException("Entity of type : "+entity.getClass().getCanonicalName()+ " does not have key defined. Annotate string typed field with @Key");
    }

    public <T> void setKeyToEntity(String key, T entity) {
        for (Field f: entity.getClass().getDeclaredFields()){
            if (f.isAnnotationPresent(Key.class)){
                setPropertyValue(entity, f.getName(), key);
            }
        }
    }

    private void validateEntity(Object entity) {
        if (entity == null) throw new HectorInvalidEntityException("Value Object cannot be null!");

        validateEntity(entity.getClass());
    }

    private void validateEntity(Class clazz) {
        if (clazz == null)  throw new HectorInvalidEntityException("Cannot validate nullable Value Object class");

        if(!clazz.isAnnotationPresent(ColumnFamily.class)){
            throw new HectorInvalidEntityException("Attempt to process object that is not a valid Value  Object(not annotated with @"+ColumnFamily.class.getCanonicalName()
                    +". Object class: "+clazz.getCanonicalName());
        }


    }



    private String generateKey(Object entity, Field f) {
        String key;
        key = UUIDUtils.generateUUIDString();
        setPropertyValue(entity,f.getName(),key);
        return key;
    }

    private void setPropertyValue(Object entity, String propName, Object value){
        try {
            PropertyUtils.setProperty(entity,propName,value);
        } catch (Exception e) {
            throw new  HectorInvalidEntityException("Cannot set  property: "+entity.getClass().getCanonicalName()+"."+propName+". Setter might not have been been defined. Details: ",e);
        }
    }


    private Object getPopertyValue(Object entity, String fName) {
        Object value = null;
        try {
            value = PropertyUtils.getSimpleProperty(entity,fName);
        } catch (Exception e) {
            throw new  HectorInvalidEntityException("Cannot access property: "+entity.getClass().getCanonicalName()+"."+fName+". Getter might not have been been defined. Details: ",e);
        }

        return value;
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public ResultSetToEntityConverter getResultSetToEntityConverter() {
        return resultSetToEntityConverter;
    }

    public void setResultSetToEntityConverter(ResultSetToEntityConverter resultSetToEntityConverter) {
        this.resultSetToEntityConverter = resultSetToEntityConverter;
    }

    public EntityFieldValueToByteArrayConverter getEntityFieldValueToByteArrayConverter() {
        return entityFieldValueToByteArrayConverter;
    }

    public void setEntityFieldValueToByteArrayConverter(EntityFieldValueToByteArrayConverter entityFieldValueToByteArrayConverter) {
        this.entityFieldValueToByteArrayConverter = entityFieldValueToByteArrayConverter;
    }


}
