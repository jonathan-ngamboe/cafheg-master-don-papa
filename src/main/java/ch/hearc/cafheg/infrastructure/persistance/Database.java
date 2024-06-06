package ch.hearc.cafheg.infrastructure.persistance;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Supplier;
import javax.sql.DataSource;

public class Database {
  private static final Logger logger = LoggerFactory.getLogger(Database.class);
  /** Pool de connections JDBC */
  private static DataSource dataSource;

  /** Connection JDBC active par utilisateur/thread (ThreadLocal) */
  private static final ThreadLocal<Connection> connection = new ThreadLocal<>();

  /**
   * Retourne la transaction active ou throw une Exception si pas de transaction
   * active.
   * @return Connection JDBC active
   */
  public static Connection activeJDBCConnection() {
    if(connection.get() == null) {
      logger.error ("Pas de connection JDBC active");
      //throw new RuntimeException("Pas de connection JDBC active");
    }
    return connection.get();
  }

  /**
   * Exécution d'une fonction dans une transaction.
   * @param inTransaction La fonction a éxécuter au travers d'une transaction
   * @param <T> Le type du retour de la fonction
   * @return Le résultat de l'éxécution de la fonction
   */
  public static <T> T inTransaction(Supplier<T> inTransaction) {
    logger.debug("inTransaction#start");
    try {
      logger.debug("inTransaction#getConnection");
      connection.set(dataSource.getConnection());
      return inTransaction.get();
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      try {
        logger.debug("inTransaction#closeConnection");
        connection.get().close();
      } catch (SQLException e) {
        logger.error("SQL Error", e);
        //throw new RuntimeException(e);
      }
      logger.info("inTransaction#end");
      connection.remove();
    }
  }

  DataSource dataSource() {
    return dataSource;
  }

  /**
   * Initialisation du pool de connections.
   */
  public void start() {
    logger.info("Initializing datasource");
    HikariConfig config = new HikariConfig();
    config.setJdbcUrl("jdbc:h2:mem:sample");
    config.setMaximumPoolSize(20);
    config.setDriverClassName("org.h2.Driver");
    dataSource = new HikariDataSource(config);
    logger.info("Datasource initialized");
  }
}
