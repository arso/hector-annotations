package me.prettyprint.cassandra.dao;

import org.apache.cassandra.thrift.ConsistencyLevel;

import me.prettyprint.cassandra.model.HectorException;
import me.prettyprint.cassandra.service.CassandraClient;
import me.prettyprint.cassandra.service.CassandraClientPool;
import me.prettyprint.cassandra.service.Keyspace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SpringCommand<OUTPUT> {
  Logger log =  LoggerFactory.getLogger(SpringCommand.class);
  private CassandraClientPool cassandraClientPool;

  public SpringCommand(CassandraClientPool cassandraClientPool) {
    this.cassandraClientPool = cassandraClientPool;
  }

  /**
   * Implement this abstract method to operate on cassandra.
   *
   * the given keyspace is the keyspace referenced by {@link #execute(String, int, String)}.
   * The method {@link #execute(String, int, String)} calls this method internally and provides it
   * with the keyspace reference.
   *
   * @param ks
   * @return
   */
  public abstract OUTPUT execute(final Keyspace ks) throws HectorException;


  protected final OUTPUT execute(String keyspace, ConsistencyLevel consistency)
      throws HectorException {
    CassandraClient c = cassandraClientPool.borrowClient();
    Keyspace ks = c.getKeyspace(keyspace, consistency);
    try {
      return execute(ks);
    } finally {
      cassandraClientPool.releaseClient(ks.getClient());
    }
  }



   /**
   * Implement this abstract method to operate on cassandra.
   *
   * the given keyspace is the keyspace referenced by {@link #execute(String, int, String)}.
   * The method {@link #execute(String, int, String)} calls this method internally and provides it
   * with the keyspace reference.
   *
   * @param keyspace Keyspace
   * @param consistency Consistency level
   * @param connectionURLs  connection URLs to povide simple loadbalancing(pooling)
   * @return
   */
  protected final OUTPUT execute(String keyspace, ConsistencyLevel consistency, String[] connectionURLs)
              throws HectorException {

    CassandraClient c = null;
      try {
          c = cassandraClientPool.borrowClient(connectionURLs);
      } catch (Exception e) {
          log.error("Could not borrow connection from the pool because of:\n "+e.getMessage());
          throw new DAOCassandraConnectivityException(e);
      }

      if (c == null) {
          log.error("Could not borrow connection from the pool because of:\n ");
          throw new DAOCassandraConnectivityException("Cassandra client connection could not be borrowed from the pool");
      }

      Keyspace ks = c.getKeyspace(keyspace, consistency);
      try {
          return execute(ks);
      } finally {
          cassandraClientPool.releaseClient(ks.getClient());
      }
  }



}
