package ch.hearc.cafheg.business;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import static ch.hearc.cafheg.infrastructure.persistance.Database.inTransaction;
import static org.junit.jupiter.api.Assertions.*;

public class MyTestsIT {
    private IDatabaseTester databaseTester;
    AllocataireMapper allocataireMapper = new AllocataireMapper();

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

        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("../../resources/MyDataSet.xml");
        if (is == null) {
            // Si le fichier n'est pas trouvé dans le classpath, essayer de le charger du système de fichiers
            File file = new File("../../resources/MyDataSet.xml");
            if (file.exists()) {
                is = new FileInputStream(file);
            } else {
                throw new IllegalArgumentException("Le fichier MyDataSet.xml est introuvable ni dans le classpath ni dans le système de fichiers.");
            }
        }

        IDataSet dataSet = new FlatXmlDataSetBuilder().build(is);

        DatabaseOperation.CLEAN_INSERT.execute(databaseTester.getConnection(), dataSet);
    }

    @ParameterizedTest
    @ValueSource(longs = {100, 101})
    public void deleteAllocataire_GivenAllocataire_ShouldDelete(long allocataireId) {
        inTransaction(() -> {
            assertNotNull(allocataireMapper.findById(allocataireId));
            allocataireMapper.deleteById(allocataireId);
            assertNull(allocataireMapper.findById(allocataireId));
            return null;
        });
    }

    @ParameterizedTest
    @ValueSource(longs = {100, 101})
    public void updateAllocataire_GivenNameAndFirstName_ShouldUpdate(long allocataireId) {
        inTransaction(() -> {
            String nom = allocataireMapper.findById(allocataireId).getNom();
            String prenom = allocataireMapper.findById(allocataireId).getPrenom();
            String nouveauNom = "Toto";
            String nouveauPrenom = "Obama";

            allocataireMapper.updateNomPrenom(allocataireId, nouveauNom, nouveauPrenom);
            String nomRecupere = allocataireMapper.findById(allocataireId).getNom();
            String prenomRecupere = allocataireMapper.findById(allocataireId).getPrenom();

            assertNotEquals(nomRecupere, nom);
            assertNotEquals(prenomRecupere, prenom);

            return null;
        });
    }
}