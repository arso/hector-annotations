package me.prettyprint.cassandra.dao;

import me.prettyprint.cassandra.annotations.HectorInvalidEntityException;
import me.prettyprint.cassandra.annotations.HectorTemplateAnnotationProcessor;
import me.prettyprint.cassandra.dao.testentities.*;
import me.prettyprint.cassandra.utils.StringTimeStamp;
import me.prettyprint.cassandra.utils.UUIDUtils;
import org.apache.cassandra.thrift.Column;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static me.prettyprint.cassandra.utils.StringUtils.string;
import static org.junit.Assert.*;

/**
 * Date: 2010-06-19
 * Time: 10:38:25
 */
public class HectorTemplateAnnotationProcessorTest {

    HectorTemplateAnnotationProcessor processor;

    @Before
    public void setUp(){
        processor = new HectorTemplateAnnotationProcessor();    
    }

    @Test
    public void should4ColumnsDefinedByAnnotationBeIncluded(){
        //given
        SampleAnnotatedEntity e = new SampleAnnotatedEntity();
        e.setId(12);
        e.setName("artur");
//        e.setCreateDate(new Date());
        Date anotherDate = new Date();
        e.setAnotherDate(anotherDate);
        //when
       List<Column> results =  processor.getColumns(e);


        //then
        assertTrue(results.size() == 4);
        assertEquals("name",new String(results.get(0).getName()));
        assertEquals("artur",string(results.get(0).getValue()));
        assertEquals("id",new String(results.get(1).getName()));
        assertEquals(Integer.valueOf(12),Integer.valueOf(new String (results.get(1).getValue())));


        assertEquals("createDate",new String(results.get(2).getName()));
        assertNull(results.get(2).getValue());

        assertEquals("anotherDate",new String(results.get(3).getName()));
        assertEquals(anotherDate,new StringTimeStamp(new String(results.get(3).getValue())).getDate());

    }

    @Test(expected= HectorInvalidEntityException.class)
    public void shouldExceptionBeThrownWhenEntityDoesNotContainAnyPersistableFields(){
        //given
        EntityWithoutPersistableFields e = new EntityWithoutPersistableFields();
        e.setId("xx");
        
        //when
        processor.getColumns(e);


        //then
        //exception
    }
    

    @Test(expected=HectorInvalidEntityException.class)
    public void shouldExceptionBeThrownWhenPersistableFieldDoesNotHaveValidGetter(){
         //given
        EntityWithoutProperGetters e = new EntityWithoutProperGetters();
        e.setId("1");
        e.setName("artur");

        //when
        processor.getColumns(e);

        //then
       //exception
    }


    @Test(expected = HectorInvalidEntityException.class)
    public void shouldExceptionBeThrownWhenEntityIsNotAnnotated(){
        //given
        EntityWithoutAnnotation e = new EntityWithoutAnnotation();
        e.setId("12");
        e.setName("artur");

        //when
        processor.getColumns(e);

        //then
        //exception
    }


    @Test(expected = HectorInvalidEntityException.class)
    public void shouldExceptionBeThrownWhenEntityObjectIsNull(){
        //given

        //when
        processor.getColumns(null);

        //then
        //exception
    }



    @Test(expected = HectorInvalidEntityException.class)
    public void shouldExceptionBeThrownOnNullableEntityWhenDeterminingEntityName(){
        //when
        processor.extractColumnFamilyName(null);
    }

    @Test
    public void shouldNameFromAnnotationBeExtracted(){
        //given
        SampleAnnotatedEntity e = new SampleAnnotatedEntity();

        //when
        String name = processor.extractColumnFamilyName(e);

        //then
        assertEquals("SampleCF", name);
    }

    @Test(expected = HectorInvalidEntityException.class)
    public void shouldExceptionByThrownOnEmptyStringEntityName(){
      //given
        EntityWithEmptyName e = new EntityWithEmptyName();

        //when
        processor.extractColumnFamilyName(e);

        //then
        //exception

    }



    @Test
    public void shouldUUIDBeGeneratedWhenKeyNotFound(){
        //given
         SampleAnnotatedEntity e = new SampleAnnotatedEntity();

        //when
        String  key = processor.extractKey(e);

        //then
        assertFalse(key.isEmpty());
    }

    
    @Test(expected= HectorInvalidEntityException.class)
    public void shouldExceptionBeThrownWhenKeyNotDefined(){
        //given
         EntityWithoutKeyDefined e = new EntityWithoutKeyDefined();

        //when
        String  key = processor.extractKey(e);

        //then
        //exception
    }

    @Test
    public void shouldUUIDBeGeneratedWhenKeyIsNull(){
        //given
        EntityWithKeyAnnotationDefined e = new EntityWithKeyAnnotationDefined();
        e.setId(null);

        //when
        String  key = processor.extractKey(e);

        //then
        assertFalse(key.isEmpty());
    }

    @Test
    public void shouldKeyAnnotadedFieldValueBeUsedAsUUID(){
        //given
        EntityWithKeyAnnotationDefined e = new EntityWithKeyAnnotationDefined();

        String  uuid = UUIDUtils.generateUUIDString();
        e.setId(uuid);


        //when
        String  key= processor.extractKey(e);

        //then
        assertEquals(uuid,key);
    }

    @Test
    public void shouldKeyAnnotatedFieldBeExcludedFromColumnList(){
        //given
        EntityWithKeyAnnotationDefined e = new EntityWithKeyAnnotationDefined();
        String uuid = UUIDUtils.generateUUIDString();
        e.setId(uuid);
        //when
        List<Column> results =  processor.getColumns(e);

        //then
        assertTrue(results.size() == 1);
        assertNull(results.get(0).getValue());
    }


    @Test
    public void shouldMultipleTypeFieldBeHandledWhileMappingResultSet(){

        //given
        Column c1 = new Column("name".getBytes(),"Artur".getBytes(),12345L);
        Column c2 = new Column("age".getBytes(),Integer.valueOf(27).toString().getBytes(),12345L);
        Column c3 = new Column("moneyAmount".getBytes(),Long.valueOf(Long.MAX_VALUE).toString().getBytes(),12345L);
        Column c4 = new Column("pictureBlob".getBytes(),"12345679".getBytes(),12345L);

        List<Column> cols = Arrays.asList(c1,c2,c3,c4) ;

        //when
        EntityWithDifferentTypesOfFields entity = processor.getEntityWithResults(cols,EntityWithDifferentTypesOfFields.class);

        //then
        assertEquals("Artur",entity.getName());
        assertEquals(Integer.valueOf(27),entity.getAge());
        assertEquals(Long.valueOf(Long.MAX_VALUE),entity.getMoneyAmount());
        assertArrayEquals("12345679".getBytes(), entity.getPictureBlob());

    }

    
    @Test
    public void shouldMultipleTypeFieldBeHandledWhileMappingResultSetWhenNulls(){

        //given
        Column c1 = new Column("name".getBytes(),"Artur".getBytes(),12345L);
        Column c2 = new Column("age".getBytes(),null,12345L);
        Column c3 = new Column("moneyAmount".getBytes(),Long.valueOf(Long.MAX_VALUE).toString().getBytes(),12345L);
        Column c4 = new Column("pictureBlob".getBytes(),"12345679".getBytes(),12345L);

        List<Column> cols = Arrays.asList(c1,c2,c3,c4) ;

        //when
        EntityWithDifferentTypesOfFields entity = processor.getEntityWithResults(cols,EntityWithDifferentTypesOfFields.class);

        //then
        assertEquals("Artur",entity.getName());
        assertNull(entity.getAge());
        assertEquals(Long.valueOf(Long.MAX_VALUE),entity.getMoneyAmount());
        assertArrayEquals("12345679".getBytes(), entity.getPictureBlob());

    }

    @Test
    public void shouldOnlyASubsetOfAvailableColumsBeMappedToTheEntity(){

        //given
        Column c1 = new Column("name".getBytes(),"Artur".getBytes(),12345L);
        Column c2 = new Column("age".getBytes(),Integer.valueOf(27).toString().getBytes(),12345L);
        Column c3 = new Column("moneyAmount".getBytes(),Long.valueOf(Long.MAX_VALUE).toString().getBytes(),12345L);
        Column c4 = new Column("pictureBlob".getBytes(),"12345679".getBytes(),12345L);

        List<Column> cols = Arrays.asList(c1,c2,c3,c4) ;

        //when
        EntityWithNameAgeOnly entity = processor.getEntityWithResults(cols,EntityWithNameAgeOnly.class);

        //then
        assertEquals("Artur",entity.getName());
        assertEquals(Integer.valueOf(27),entity.getAge());

    }



}
