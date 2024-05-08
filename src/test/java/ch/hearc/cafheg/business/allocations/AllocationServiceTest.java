package ch.hearc.cafheg.business.allocations;

import ch.hearc.cafheg.business.common.Montant;
import ch.hearc.cafheg.infrastructure.persistance.AllocataireMapper;
import ch.hearc.cafheg.infrastructure.persistance.AllocationMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class AllocationServiceTest {

  private AllocationService allocationService;

  private AllocataireMapper allocataireMapper;
  private AllocationMapper allocationMapper;

  @BeforeEach
  void setUp() {
    allocataireMapper = Mockito.mock(AllocataireMapper.class);
    allocationMapper = Mockito.mock(AllocationMapper.class);

    allocationService = new AllocationService(allocataireMapper, allocationMapper);
  }

  @Test
  void findAllAllocataires_GivenEmptyAllocataires_ShouldBeEmpty() {
    Mockito.when(allocataireMapper.findAll("Geiser")).thenReturn(Collections.emptyList());
    List<Allocataire> all = allocationService.findAllAllocataires("Geiser");
    assertThat(all).isEmpty();
  }

  @Test
  void findAllAllocataires_Given2Geiser_ShouldBe2() {
    Mockito.when(allocataireMapper.findAll("Geiser"))
        .thenReturn(Arrays.asList(new Allocataire(new NoAVS("1000-2000"), "Geiser", "Arnaud"),
            new Allocataire(new NoAVS("1000-2001"), "Geiser", "Aurélie")));
    List<Allocataire> all = allocationService.findAllAllocataires("Geiser");
    assertAll(() -> assertThat(all.size()).isEqualTo(2),
        () -> assertThat(all.get(0).getNoAVS()).isEqualTo(new NoAVS("1000-2000")),
        () -> assertThat(all.get(0).getNom()).isEqualTo("Geiser"),
        () -> assertThat(all.get(0).getPrenom()).isEqualTo("Arnaud"),
        () -> assertThat(all.get(1).getNoAVS()).isEqualTo(new NoAVS("1000-2001")),
        () -> assertThat(all.get(1).getNom()).isEqualTo("Geiser"),
        () -> assertThat(all.get(1).getPrenom()).isEqualTo("Aurélie"));
  }

  @Test
  void findAllocationsActuelles() {
    Mockito.when(allocationMapper.findAll())
        .thenReturn(Arrays.asList(new Allocation(new Montant(new BigDecimal(1000)), Canton.NE,
            LocalDate.now(), null), new Allocation(new Montant(new BigDecimal(2000)), Canton.FR,
            LocalDate.now(), null)));
    List<Allocation> all = allocationService.findAllocationsActuelles();
    assertAll(() -> assertThat(all.size()).isEqualTo(2),
        () -> assertThat(all.get(0).getMontant()).isEqualTo(new Montant(new BigDecimal(1000))),
        () -> assertThat(all.get(0).getCanton()).isEqualTo(Canton.NE),
        () -> assertThat(all.get(0).getDebut()).isEqualTo(LocalDate.now()),
        () -> assertThat(all.get(0).getFin()).isNull(),
        () -> assertThat(all.get(1).getMontant()).isEqualTo(new Montant(new BigDecimal(2000))),
        () -> assertThat(all.get(1).getCanton()).isEqualTo(Canton.FR),
        () -> assertThat(all.get(1).getDebut()).isEqualTo(LocalDate.now()),
        () -> assertThat(all.get(1).getFin()).isNull());
  }

  @Test
  void getParentDroitAllocation_WhenMissingValues_ShouldUseDefaultValuesAndDecide() {
    ParentAllocationParameters parameters = new ParentAllocationParameters(true, false, null, null, false, false, false, false, false, false, false, false, false);
    String result = allocationService.getParentDroitAllocation(parameters);
    assertThat(result).isEqualTo("Parent1");
  }

  @Test
  void getParentDroitAllocation_WhenAllValuesNull_ShouldHandleGracefully() {
    ParentAllocationParameters parameters = new ParentAllocationParameters(false, false, null, null, false, false, false, false, false, false, false, false, false);
    String result = allocationService.getParentDroitAllocation(parameters);
    assertThat(result).isNotBlank(); // Make sure it doesn't crash and returns a default
  }

  // 1 Parent avec activité lucrative, l'autre sans activité lucrative et inversement
    @Test
    void getParentDroitAllocation_WhenParent1HasActivityAndParent2HasNoActivity_ShouldReturnParent1() {
        ParentAllocationParameters parameters = new ParentAllocationParameters(true, false, new BigDecimal("5000"), new BigDecimal("3000"), false, false, false, false, false, false, false, false, false);
        String result = allocationService.getParentDroitAllocation(parameters);
        assertThat(result).isEqualTo("Parent1");
    }

    // Les deux parents ont une activité lucrative, 1 parent avec autorité parentale, l'autre sans autorité parentale
    @Test
    void getParentDroitAllocation_WhenBothParentsHaveActivityAndParent1HasAuthority_ShouldReturnParent1() {
        ParentAllocationParameters parameters = new ParentAllocationParameters(true, true, new BigDecimal("5000"), new BigDecimal("3000"), false, false, false, true, false, false, false, false, false);
        String result = allocationService.getParentDroitAllocation(parameters);
        assertThat(result).isEqualTo("Parent1");
    }

  // Les deux parents ont une activité lucrative, les deux parents avec autorité parentale, les parents vivent séparément, le parent qui vit avec l'enfant a droit
    @Test
    void getParentDroitAllocation_WhenBothParentsHaveActivityAndBothHaveAuthorityAndParentsLiveSeparately_ShouldReturnParent1() {
        ParentAllocationParameters parameters = new ParentAllocationParameters(true, true, new BigDecimal("5000"), new BigDecimal("3000"), true, false, false, true, true, true, false, false, false);
        String result = allocationService.getParentDroitAllocation(parameters);
        assertThat(result).isEqualTo("Parent1");
    }

    // Les deux parents ont une activité lucrative, les deux parents avec autorité parentale, les parents vivent ensemble, 1 parent travaille dans le canton de domicile de l'enfant, l'autre non, le parent qui travaille dans le canton de domicile de l'enfant a droit
    @Test
    void getParentDroitAllocation_WhenBothParentsHaveActivityAndBothHaveAuthorityAndParentsLiveTogetherAndParent1WorksInChildCanton_ShouldReturnParent1() {
        ParentAllocationParameters parameters = new ParentAllocationParameters(true, true, new BigDecimal("5000"), new BigDecimal("3000"), false, false, true, true, true, true, false, false, false);
        String result = allocationService.getParentDroitAllocation(parameters);
        assertThat(result).isEqualTo("Parent1");
    }

    // Les deux parents ont une activité lucrative, les deux parents avec autorité parentale, les parents vivent ensemble, les deux parents sont indépendants, le parent avec le plus gros salaire a droit
    @Test
    void getParentDroitAllocation_WhenBothParentsHaveActivityAndBothHaveAuthorityAndParentsLiveTogetherAndBothAreIndependant_ShouldReturnParent1() {
        ParentAllocationParameters parameters = new ParentAllocationParameters(true, true, new BigDecimal("5000"), new BigDecimal("3000"), false, false, true, true, true, true, false, true, true);
        String result = allocationService.getParentDroitAllocation(parameters);
        assertThat(result).isEqualTo("Parent1");
    }

    // Les deux parents ont une activité lucrative, les deux parents avec autorité parentale, les parents vivent ensemble, 1 parent est salarié et l'autre indépendant, le parent salarié a droit
    @Test
    void getParentDroitAllocation_WhenBothParentsHaveActivityAndBothHaveAuthorityAndParentsLiveTogetherAndOneIsEmployedAndTheOtherIsIndependant_ShouldReturnParent1() {
        ParentAllocationParameters parameters = new ParentAllocationParameters(true, true, new BigDecimal("5000"), new BigDecimal("3000"), false, false, true, true, true, true, false, true, false);
        String result = allocationService.getParentDroitAllocation(parameters);
        assertThat(result).isEqualTo("Parent1");
    }

    // Les deux parents ont une activité lucrative, les deux parents avec autorité parentale, les parents vivent ensemble, les deux parents sont salariés, le parent avec le plus gros salaire a droit
    @Test
    void getParentDroitAllocation_WhenBothParentsHaveActivityAndBothHaveAuthorityAndParentsLiveTogetherAndBothAreEmployed_ShouldReturnParent1() {
        ParentAllocationParameters parameters = new ParentAllocationParameters(true, true, new BigDecimal("5000"), new BigDecimal("3000"), false, false, true, true, true, true, false, false, false);
        String result = allocationService.getParentDroitAllocation(parameters);
        assertThat(result).isEqualTo("Parent1");
    }
}