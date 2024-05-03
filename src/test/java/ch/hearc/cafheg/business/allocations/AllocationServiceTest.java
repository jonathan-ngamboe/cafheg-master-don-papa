package ch.hearc.cafheg.business.allocations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import ch.hearc.cafheg.business.common.Montant;
import ch.hearc.cafheg.infrastructure.persistance.AllocataireMapper;
import ch.hearc.cafheg.infrastructure.persistance.AllocationMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

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
  void getParentDroitAllocation_WhenParent1IsOnlyActive_ShouldReturnParent1() {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("parent1ActiviteLucrative", true);
    parameters.put("parent2ActiviteLucrative", false);
    parameters.put("parent1Salaire", new BigDecimal("5000"));
    parameters.put("parent2Salaire", new BigDecimal("3000"));
    String result = allocationService.getParentDroitAllocation(parameters);
    assertThat(result).isEqualTo("Parent1");
  }

  @Test
  void getParentDroitAllocation_WhenParent2IsOnlyActive_ShouldReturnParent2() {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("parent1ActiviteLucrative", false);
    parameters.put("parent2ActiviteLucrative", true);
    parameters.put("parent1Salaire", new BigDecimal("3000"));
    parameters.put("parent2Salaire", new BigDecimal("5000"));
    String result = allocationService.getParentDroitAllocation(parameters);
    assertThat(result).isEqualTo("Parent2");
  }

  @Test
  void getParentDroitAllocation_WhenBothParentsActiveAndSalary1GreaterThanSalary2_ShouldReturnParent1() {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("parent1ActiviteLucrative", true);
    parameters.put("parent2ActiviteLucrative", true);
    parameters.put("parent1Salaire", new BigDecimal("7000"));
    parameters.put("parent2Salaire", new BigDecimal("5000"));
    String result = allocationService.getParentDroitAllocation(parameters);
    assertThat(result).isEqualTo("Parent1");
  }

  @Test
  void getParentDroitAllocation_WhenBothParentsActiveAndSalary2GreaterThanSalary1_ShouldReturnParent2() {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("parent1ActiviteLucrative", true);
    parameters.put("parent2ActiviteLucrative", true);
    parameters.put("parent1Salaire", new BigDecimal("5000"));
    parameters.put("parent2Salaire", new BigDecimal("7000"));
    String result = allocationService.getParentDroitAllocation(parameters);
    assertThat(result).isEqualTo("Parent2");
  }

  @Test
  void getParentDroitAllocation_WhenMissingValues_ShouldUseDefaultValuesAndDecide() {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("parent1ActiviteLucrative", true);
    String result = allocationService.getParentDroitAllocation(parameters);
    assertThat(result).isEqualTo("Parent1");
  }

  @Test
  void getParentDroitAllocation_WhenAllValuesNull_ShouldHandleGracefully() {
    Map<String, Object> parameters = new HashMap<>();
    String result = allocationService.getParentDroitAllocation(parameters);
    assertThat(result).isEqualTo("Parent2");
  }

}