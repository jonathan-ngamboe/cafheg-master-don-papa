package ch.hearc.cafheg.business.allocations;

import ch.hearc.cafheg.infrastructure.persistance.AllocataireMapper;
import ch.hearc.cafheg.infrastructure.persistance.AllocationMapper;

import java.math.BigDecimal;
import java.util.List;

public class AllocationService {

  private static final String PARENT_1 = "Parent1";
  private static final String PARENT_2 = "Parent2";

  private final AllocataireMapper allocataireMapper;
  private final AllocationMapper allocationMapper;

  public AllocationService(
      AllocataireMapper allocataireMapper,
      AllocationMapper allocationMapper) {
    this.allocataireMapper = allocataireMapper;
    this.allocationMapper = allocationMapper;
  }

  public List<Allocataire> findAllAllocataires(String likeNom) {
    System.out.println("Rechercher tous les allocataires");
    return allocataireMapper.findAll(likeNom);
  }

  public List<Allocation> findAllocationsActuelles() {
    return allocationMapper.findAll();
  }

  public String getParentDroitAllocation(ParentAllocationParameters params) {
    System.out.println("Déterminer quel parent a le droit aux allocations");

    BigDecimal salaireP1 = params.getParent1Salaire() != null ? params.getParent1Salaire() : BigDecimal.ZERO;
    BigDecimal salaireP2 = params.getParent2Salaire() != null ? params.getParent2Salaire() : BigDecimal.ZERO;

    if (params.isParent1ActiviteLucrative() && !params.isParent2ActiviteLucrative()) {
      return PARENT_1;
    } else if (params.isParent2ActiviteLucrative() && !params.isParent1ActiviteLucrative()) {
      return PARENT_2;
    } else {
      return salaireP1.compareTo(salaireP2) > 0 ? PARENT_1 : PARENT_2;
    }
  }

}
