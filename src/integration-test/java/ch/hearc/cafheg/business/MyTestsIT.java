package ch.hearc.cafheg.business;

import ch.hearc.cafheg.business.allocations.Allocataire;
import ch.hearc.cafheg.business.allocations.AllocataireService;
import ch.hearc.cafheg.infrastructure.persistance.AllocataireMapper;
import ch.hearc.cafheg.infrastructure.persistance.Database;
import ch.hearc.cafheg.infrastructure.persistance.Migrations;
import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static ch.hearc.cafheg.infrastructure.persistance.Database.inTransaction;
import static org.junit.jupiter.api.Assertions.*;

public class MyTestsIT {
    private IDatabaseTester databaseTester;
    private AllocataireMapper allocataireMapper = new AllocataireMapper();
    AllocataireService allocataireService = new AllocataireService(allocataireMapper);

    @Test
    public void firstTest() {
        assertEquals(1, 1);
    }

    @BeforeEach
    public void setUp() throws Exception {
        Database database = new Database();
        Migrations migrations = new Migrations(database);

        database.start();
        migrations.start();

        databaseTester = new JdbcDatabaseTester(
                "org.h2.Driver",
                "jdbc:h2:mem:sample",
                "",
                ""
        );

        IDataSet dataSet = new FlatXmlDataSetBuilder().build(
                getClass().getClassLoader().getResourceAsStream("dataset.xml")
        );
        DatabaseOperation.CLEAN_INSERT.execute(databaseTester.getConnection(), dataSet);
    }

    @ParameterizedTest
    @ValueSource(longs = {101})
    public void deleteAllocataireWithoutVersements_GivenAllocataire_ShouldDelete(long allocataireId) {
        inTransaction(() -> {
            assertNotNull(allocataireService.findById(allocataireId));
            allocataireService.deleteAllocataire(allocataireId);
            assertNull(allocataireService.findById(allocataireId));
            return null;
        });
    }

    @ParameterizedTest
    @ValueSource(longs = {100})
    public void deleteAllocataireWithVersements_GivenAllocataire_ShouldException(long allocataireId) {
        inTransaction(() -> {
            assertNotNull(allocataireService.findById(allocataireId));
            assertThrows(RuntimeException.class, () -> allocataireService.deleteAllocataire(allocataireId));
            return null;
        });
    }

    @ParameterizedTest
    @ValueSource(longs = {100, 101})
    public void updateAllocataire_GivenNameAndFirstName_ShouldUpdate(long allocataireId) {
        inTransaction(() -> {
            Allocataire allocataire = allocataireService.findById(allocataireId);
            String nom = allocataire.getNom();
            String prenom = allocataire.getPrenom();
            String nouveauNom = "Toto";
            String nouveauPrenom = "Obama";

            allocataireService.updateNomPrenom(allocataireId, nouveauNom, nouveauPrenom);
            allocataire = allocataireService.findById(allocataireId);
            String nomRecupere = allocataire.getNom();
            String prenomRecupere = allocataire.getPrenom();

            assertNotEquals(nomRecupere, nom);
            assertNotEquals(prenomRecupere, prenom);

            return null;
        });
    }
}