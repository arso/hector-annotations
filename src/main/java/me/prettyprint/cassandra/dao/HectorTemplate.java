package me.prettyprint.cassandra.dao;

import static me.prettyprint.cassandra.utils.StringUtils.bytes;
import static me.prettyprint.cassandra.utils.StringUtils.string;

import me.prettyprint.cassandra.annotations.HectorTemplateAnnotationProcessor;
import me.prettyprint.cassandra.model.HectorException;
import me.prettyprint.cassandra.service.CassandraClient;
import me.prettyprint.cassandra.service.CassandraClientPool;
import me.prettyprint.cassandra.service.CassandraClientPoolFactory;
import me.prettyprint.cassandra.service.Keyspace;

import org.apache.cassandra.thrift.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Date: 2010-06-09
 * Time: 20:15:47
 */
public class HectorTemplate implements InitializingBean {
    public static final String POOL_NOT_CREATED = "Cassandra connecion pool couldn't be obtained";
    public static final String LIST_OF_CONNECTIONS_IS_NULL = "List of cassandra connections must not be null!";
    public static final String LIST_OF_CONNECTIONS_IS_EMPTY = "List of cassandra connections must not be empty!";

    Logger log = LoggerFactory.getLogger(HectorTemplate.class);


    private String[] connectionURLs;

    private String keyspace = null;
    private ConsistencyLevel consistencyLevel = CassandraClient.DEFAULT_CONSISTENCY_LEVEL;
    // collaborator


    private CassandraClientPool cassandraClientPool;
    private HectorTemplateAnnotationProcessor annotationsProcessor  = new HectorTemplateAnnotationProcessor();


    /**
     * Must be constructed with a CassandraClientPool
     *
     * @param cassandraClientPool
     */
    public HectorTemplate(CassandraClientPool cassandraClientPool) {
        this.cassandraClientPool = cassandraClientPool;
    }

    public HectorTemplate() {
    }

    /**
     * Insert a new value keyed by key
     *
     * @param columnFamilyName Column family name
     * @param columnName       Column name
     * @param key              Key for the value
     * @param value            the String value to insert
     */
    public void insert(final String columnFamilyName, final String columnName, final String key, final String value) throws Exception {
        execute(new SpringCommand<Void>(cassandraClientPool) {
            public Void execute(final Keyspace ks) throws HectorException {
                ks.insert(key, createColumnPath(columnFamilyName, columnName), bytes(value));
                return null;
            }
        });
    }


    public void insert(final Object entity) throws Exception {
        execute(new SpringCommand<Void>(cassandraClientPool) {
            @Override
            public Void execute(Keyspace ks) throws HectorException {
                String columnFamilyName = annotationsProcessor.extractColumnFamilyName(entity);
                List<Column> columns = annotationsProcessor.getColumns(entity);
                String key = annotationsProcessor.extractKey(entity);

                Map<String, List<Column>> cmap = new HashMap<String, List<Column>>();
                cmap.put(columnFamilyName, columns);

                ks.batchInsert(key, cmap, null);
                return null;
            }
        });
    }

    public void insertRowOfColumns(final String columnFamilyName, final String key, final Map<String, String> columns) throws Exception {
        execute(new SpringCommand<Void>(cassandraClientPool) {
            @Override
            public Void execute(Keyspace ks) throws HectorException {
                Long timestamp = System.currentTimeMillis();
                Map<String, List<Column>> cmap = new HashMap<String, List<Column>>();
                List<Column> columnList = new ArrayList<Column>();
                for (Map.Entry<String, String> e : columns.entrySet()) {
                    Column c = new Column(e.getKey().getBytes(), e.getValue().getBytes(), timestamp);
                    columnList.add(c);
                }
                cmap.put(columnFamilyName, columnList);
                ks.batchInsert(key, cmap, null);
                return null;
            }
        });
    }



    public Map<String, String> getRowOfColumns(final String columnFamilyName, final String key) throws Exception {
        return execute(new SpringCommand<Map<String, String>>(cassandraClientPool) {
            @Override
            public Map<String, String> execute(Keyspace ks) throws HectorException {

                SlicePredicate slicePredicate = new SlicePredicate();
                SliceRange range = new SliceRange();
                range.setStart(new byte[0]);
                range.setFinish(new byte[0]);
                slicePredicate.setSlice_range(range);
                
                Map<String, String> returnMap = new HashMap<String, String>();
                List<Column> list = ks.getSlice(key, new ColumnParent(columnFamilyName), slicePredicate);
                for (Column c : list) {
                    returnMap.put(string(c.getName()), string(c.getValue()));
                }
                return returnMap;
            }
        });
    }


   public <T> T getObjectByKey(final String key , final Class<T> entityClass) throws Exception {
        return  execute(new SpringCommand<T>(cassandraClientPool) {
            @Override
            public  T execute(Keyspace ks) throws HectorException {
                String columnFamilyName = annotationsProcessor.extractColumnFamilyName(entityClass);


                SlicePredicate slicePredicate = new SlicePredicate();
                SliceRange range = new SliceRange();
                range.setStart(new byte[0]);
                range.setFinish(new byte[0]);
                slicePredicate.setSlice_range(range);

                List<Column> list = ks.getSlice(key, new ColumnParent(columnFamilyName), slicePredicate);
                T entity = annotationsProcessor.getEntityWithResults(list,entityClass);
                annotationsProcessor.setKeyToEntity(key, entity);

                return entity;

            }
        });
    }


     /**
     * Get a string value.
     *
     * @param columnFamilyName Column family name
     * @param columnName       Column name
     * @param key              Key for the value
     * @return The string value; null if no value exists for the given key.
     */
    public String getAsString(final String columnFamilyName, final String columnName, final String key) throws Exception {
        return execute(new SpringCommand<String>(cassandraClientPool) {
            public String execute(final Keyspace ks) throws HectorException {
                    return string(ks.getColumn(key, createColumnPath(columnFamilyName, columnName)).getValue());
            }
        });
    }




    protected ColumnPath createColumnPath(final String columnFamilyName, String columnName) {
        return new ColumnPath(columnFamilyName).setColumn(bytes(columnName));
    }

    protected <T> T execute(SpringCommand<T> command) throws Exception {
        return command.execute(keyspace, consistencyLevel, connectionURLs);
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        cassandraClientPool = CassandraClientPoolFactory.INSTANCE.get();
        if (cassandraClientPool == null) {
            log.error(POOL_NOT_CREATED);
            throw new Exception(POOL_NOT_CREATED);
        }
    }

    public void setKeyspace(String keyspace) {
        this.keyspace = keyspace;
    }

    public void setConsistencyLevel(ConsistencyLevel consistencyLevel) {
        this.consistencyLevel = consistencyLevel;
    }

    public String[] getConnectionURLs() {
        return connectionURLs;
    }

    public void setConnectionURLs(String[] connectionURLs) {
        if (connectionURLs == null) {
            log.error(LIST_OF_CONNECTIONS_IS_NULL);
            throw new IllegalArgumentException(LIST_OF_CONNECTIONS_IS_NULL);
        }

        if (connectionURLs.length == 0) {
            log.error(LIST_OF_CONNECTIONS_IS_EMPTY);
            throw new IllegalArgumentException(LIST_OF_CONNECTIONS_IS_EMPTY);
        }


        this.connectionURLs = connectionURLs;
    }

    public void setCassandraClientPool(CassandraClientPool cassandraClientPool) {
        this.cassandraClientPool = cassandraClientPool;
    }

    public CassandraClientPool getCassandraClientPool() {
        return cassandraClientPool;
    }
}
