package me.prettyprint.cassandra.dao;

import me.prettyprint.cassandra.dao.HectorTemplate;
import me.prettyprint.cassandra.dao.testentities.EntityWithKeyAnnotationDefined;
import me.prettyprint.cassandra.dao.testentities.SampleAnnotatedEntity;
import me.prettyprint.cassandra.model.HectorException;
import me.prettyprint.cassandra.service.CassandraClientPool;
import me.prettyprint.cassandra.testutils.EmbeddedServerHelper;
import org.apache.cassandra.contrib.utils.service.CassandraServiceDataCleaner;
import org.apache.cassandra.service.EmbeddedCassandraService;
import org.apache.thrift.transport.TTransportException;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Date: 2010-06-07
 * Time: 07:06:33
 */
//@RunWith(MockitoJUnitRunner.class)
public class HectorTemplateTest {

    private final static String CASSANDRA_KEYSPACE = "Keyspace1";
    private final static int CASSANDRA_PORT = 9160;
    private final static String CASSANDRA_HOST = "0.0.0.0";
    private final String CF_NAME = "Standard2";

    HectorTemplate template;
    @Mock
    CassandraClientPool pool;
    @Mock
    Logger log;

    static EmbeddedCassandraService cassandra;
    private static Thread cassandraThread;


    @BeforeClass
    public static void preSetUp() throws IOException, TTransportException, InterruptedException {
//    	   if (System.getProperty("user.dir").endsWith("hector-annotations")) {
               System.setProperty("storage-config", "./src/test/resources");
//           } else {
//               System.setProperty("storage-config", "./hector-annotations/src/test/resources");
//
//           }
        CassandraServiceDataCleaner cleaner = new CassandraServiceDataCleaner();
        cleaner.prepare();
        cassandra = new EmbeddedCassandraService();
        cassandra.init();
        cassandraThread = new Thread(cassandra);
        cassandraThread.setDaemon(true);
        cassandraThread.start();
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        template = new HectorTemplate();
        template.log = log;

    }

    @AfterClass
    public static void postTearDown() {
        cassandraThread.interrupt();
    }


    @Test
    public void shouldPoolOfConnectionsBeCreatedBasedOnListOfStrings() throws Exception {
        //given
        String conn1 = "192.168.1.1:9160";
        String conn2 = "192.168.1.2:9160";
        String conn3 = "192.168.1.3:9160";

        String[] connections = new String[]{conn1, conn2, conn3};

        template.setConnectionURLs(connections);
        HectorTemplate template = new HectorTemplate();
        assertNull(template.getCassandraClientPool());
        //when
        template.afterPropertiesSet();

        //then
        assertNotNull(template.getCassandraClientPool());
    }


    @Test(expected = DAOCassandraConnectivityException.class)
    public void shouldExceptionBeHandledWhenBorrowingConnectionFromThePool() throws Exception {
        //given
        String conn1 = "192.168.1.1:9160";
        String conn2 = "192.168.1.2:9160";
        String conn3 = "192.168.1.3:9160";
        String[] connections = new String[]{conn1, conn2, conn3};
        template.setConnectionURLs(connections);
        template.setCassandraClientPool(pool);

        when(pool.borrowClient(connections)).thenThrow(new HectorException("some error"));


        //when
        template.getObjectByKey("whatever",SampleAnnotatedEntity.class);

        //then
        //exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldExceptionBeThrownWhenNullableListOfConnections() {
        //when
        template.setConnectionURLs(null);

        //then
        verify(log).error(anyString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldExceptionBeThrownWhenEmptyListOfConnections() {
        //when
        template.setConnectionURLs(new String[]{});

        //then
        verify(log).error(anyString());
    }

    @Test
    public void testInsertEntity() throws Exception {
        //provided
        String conn1 = "localhost:9180";
        String[] connections = new String[]{conn1};
        HectorTemplate t = new HectorTemplate();
        t.afterPropertiesSet();
        t.setConnectionURLs(connections);
        t.setKeyspace("ForTests");

        EntityWithKeyAnnotationDefined e = new EntityWithKeyAnnotationDefined();
        e.setId("123id");
        e.setName("Artur");
        //when
        t.insert(e);

        //then
        String actualCol1 = t.getAsString("EntityWithKey", "name", "123id");
        Assert.assertEquals("Artur", actualCol1);
    }


    @Test
    public void testGetRowOfColumns() throws Exception {

        String conn1 = "localhost:9180";
        String[] connections = new String[]{conn1};
        HectorTemplate t = new HectorTemplate();
        t.afterPropertiesSet();
        t.setConnectionURLs(connections);
        t.setKeyspace("Keyspace1");
        Map<String, String> columns = new HashMap<String, String>();
        columns.put("col1", "AAA");
        columns.put("col2", Integer.valueOf(10).toString());
        t.insertRowOfColumns("Standard1", "testEntry", columns);

        //when

        Map<String, String> row = t.getRowOfColumns("Standard1", "testEntry");
        //then
        Assert.assertEquals("AAA", row.get("col1"));
        Assert.assertEquals("10", row.get("col2"));

    }

    @Test
    public void testGetObjectByKey() throws Exception {

        String conn1 = "localhost:9180";
        String[] connections = new String[]{conn1};
        HectorTemplate t = new HectorTemplate();
        t.afterPropertiesSet();
        t.setConnectionURLs(connections);
        t.setKeyspace("ForTests");


        EntityWithKeyAnnotationDefined e1 = new EntityWithKeyAnnotationDefined();
        e1.setId("10");
        e1.setName("Tomek");
        EntityWithKeyAnnotationDefined e2 = new EntityWithKeyAnnotationDefined();
        e2.setId("5");
        e2.setName("Artur");
        t.insert(e1);
        t.insert(e2);

        //when
        EntityWithKeyAnnotationDefined result =  t.getObjectByKey("5",EntityWithKeyAnnotationDefined.class);

        //then
        assertEquals("Artur", result.getName());
        assertEquals("5",result.getId());

    }

    @Test
    public void testGetObjectWithMultipleFieldTypesByKey() throws Exception {

        String conn1 = "localhost:9180";
        String[] connections = new String[]{conn1};
        HectorTemplate t = new HectorTemplate();
        t.afterPropertiesSet();
        t.setConnectionURLs(connections);
        t.setKeyspace("ForTests");


        EntityWithKeyAnnotationDefined e1 = new EntityWithKeyAnnotationDefined();
        e1.setId("10");
        e1.setName("Tomek");
        EntityWithKeyAnnotationDefined e2 = new EntityWithKeyAnnotationDefined();
        e2.setId("5");
        e2.setName("Artur");
        t.insert(e1);
        t.insert(e2);

        //when
        EntityWithKeyAnnotationDefined result =  t.getObjectByKey("5",EntityWithKeyAnnotationDefined.class);

        //then
        assertEquals("Artur", result.getName());
        assertEquals("5",result.getId());
    }

  
}
