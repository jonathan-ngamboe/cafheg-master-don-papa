package ch.hearc.cafheg.infrastructure.persistance;

import ch.hearc.cafheg.business.allocations.Allocataire;
import ch.hearc.cafheg.business.allocations.NoAVS;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AllocataireMapper extends Mapper {
  private static final Logger logger = LoggerFactory.getLogger(AllocataireMapper.class);
  private static final String QUERY_FIND_ALL = "SELECT NOM,PRENOM,NO_AVS FROM ALLOCATAIRES";
  private static final String QUERY_FIND_WHERE_NOM_LIKE = "SELECT NOM,PRENOM,NO_AVS FROM ALLOCATAIRES WHERE NOM LIKE ?";
  private static final String QUERY_FIND_WHERE_NUMERO = "SELECT NO_AVS, NOM, PRENOM FROM ALLOCATAIRES WHERE NUMERO=?";
  private static final String QUERY_DELETE_BY_ID = "DELETE FROM ALLOCATAIRES WHERE NUMERO=?";
  private static final String QUERY_UPDATE_NOM_PRENOM = "UPDATE ALLOCATAIRES SET NOM = ?, PRENOM = ? WHERE NUMERO = ?";
  private static final String QUERY_FIND_NUMBER_VERSEMENTS = "SELECT COUNT(*) FROM VERSEMENTS WHERE FK_ALLOCATAIRES = ?";

  public List<Allocataire> findAll(String likeNom) {
    logger.info("findAll() " + likeNom);
    Connection connection = activeJDBCConnection();
    try {
      PreparedStatement preparedStatement;
      if (likeNom == null) {
        logger.debug("SQL: " + QUERY_FIND_ALL);
        preparedStatement = connection
                .prepareStatement(QUERY_FIND_ALL);
      } else {
        logger.debug("SQL: " + QUERY_FIND_WHERE_NOM_LIKE);
        preparedStatement = connection
                .prepareStatement(QUERY_FIND_WHERE_NOM_LIKE);
        preparedStatement.setString(1, likeNom + "%");
      }
      logger.info("Allocation d'un nouveau tableau");
      List<Allocataire> allocataires = new ArrayList<>();

      logger.info("Exécution de la requête");
      try (ResultSet resultSet = preparedStatement.executeQuery()) {

        logger.info("Allocataire mapping");
        while (resultSet.next()) {
          logger.trace("ResultSet#next");
          allocataires
                  .add(new Allocataire(new NoAVS(resultSet.getString(3)), resultSet.getString(2),
                          resultSet.getString(1)));
        }
      }
      logger.info("Allocataires trouvés " + allocataires.size());
      return allocataires;
    } catch (SQLException e) {
      logger.error("SQL Error", e);
      throw new RuntimeException(e);
    }
  }

  public Allocataire findById(long id) {
    logger.info("findById() " + id);
    Connection connection = activeJDBCConnection();
    try {
      logger.debug("SQL:" + QUERY_FIND_WHERE_NUMERO);
      PreparedStatement preparedStatement = connection.prepareStatement(QUERY_FIND_WHERE_NUMERO);
      preparedStatement.setLong(1, id);
      ResultSet resultSet = preparedStatement.executeQuery();
      logger.trace("ResultSet#next");
      if(resultSet.next()) {
        logger.info("Allocataire mapping");
        return new Allocataire(new NoAVS(resultSet.getString(1)),
                resultSet.getString(2), resultSet.getString(3));
      } else {
        logger.info("Allocataire introuvable");
        return null;
      }
    } catch (SQLException e) {
      logger.error("SQL Error", e);
      throw new RuntimeException(e);
    }
  }

  public boolean hasVersements(long id) {
    logger.info("hasVersements() " + id);
    Connection connection = activeJDBCConnection();
    String query = QUERY_FIND_NUMBER_VERSEMENTS;
    try {
      PreparedStatement preparedStatement = connection.prepareStatement(QUERY_FIND_NUMBER_VERSEMENTS);
      preparedStatement.setLong(1, id);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        if (resultSet.next()) {
          int count = resultSet.getInt(1);
          return count > 0;
        }
      }
    } catch (SQLException e) {
      logger.error("SQL Error", e);
      throw new RuntimeException("Erreur lors de la vérification des versements de l'allocataire", e);
    }
    return false;
  }

  public boolean deleteById(long id) {
    logger.info("deleteById() " + id);
    Connection connection = activeJDBCConnection();
    try {
      logger.debug("SQL:" + QUERY_DELETE_BY_ID);
      PreparedStatement preparedStatement = connection.prepareStatement(QUERY_DELETE_BY_ID);
      preparedStatement.setLong(1, id);
      int rowsAffected = preparedStatement.executeUpdate();
      if (rowsAffected == 0) {
        throw new RuntimeException("Aucun allocataire avec l'ID : " + id + " n'a été trouvé pour la suppression.");
      }
      logger.info("Allocataire supprimé avec succès");
      return true;
    } catch (SQLException e) {
      logger.error("SQL Error", e);
      throw new RuntimeException(e);
    }
  }

  public void updateNomPrenom(long id, String nouveauNom, String nouveauPrenom) {
    logger.info("updateNomPrenom() " + id);
    Connection connection = activeJDBCConnection();
    try {
      PreparedStatement preparedStatement = connection.prepareStatement(QUERY_UPDATE_NOM_PRENOM);
      preparedStatement.setString(1, nouveauNom);
      preparedStatement.setString(2, nouveauPrenom);
      preparedStatement.setLong(3, id);
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      logger.error("SQL Error", e);
      //throw new RuntimeException("Erreur lors de la mise à jour du nom et du prénom de l'allocataire", e);
    }
  }
}
