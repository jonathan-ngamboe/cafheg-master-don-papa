package ch.hearc.cafheg.business.allocations;

import ch.hearc.cafheg.infrastructure.persistance.AllocataireMapper;
import ch.hearc.cafheg.infrastructure.persistance.AllocationMapper;

import java.math.BigDecimal;
import java.util.List;

public class AllocationService {

  private static final String PARENT_1 = "Parent1";
  private static final String PARENT_2 = "Parent2";
  private static final String AUCUN_DROIT = "Aucun droit";
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

  // Determine quel parent a le droit aux allocations
  public String getParentDroitAllocation(ParentAllocationParameters params) {
    System.out.println("Déterminer quel parent a le droit aux allocations");

    // Si aucun parent n'a d'activité lucrative, aucun droit
    if (!params.isParent1ActiviteLucrative() && !params.isParent2ActiviteLucrative()) {
      return AUCUN_DROIT;
    }

    // Si un seul parent a une activité lucrative, il a le droit
    if (params.isParent1ActiviteLucrative() && !params.isParent2ActiviteLucrative()) {
      return PARENT_1;
    } else if (!params.isParent1ActiviteLucrative() && params.isParent2ActiviteLucrative()) {
      return PARENT_2;
    } else {
      // Les deux parents ont une activité lucrative
      return evaluateBothParentsLucrative(params);
    }
  }

  // Méthode pour évaluer les deux parents qui ont une activité lucrative
  private String evaluateBothParentsLucrative(ParentAllocationParameters params) {
    // Vérifier si un seul parent a l'autorité parentale
    if (params.isParent1AutoriteParentale() && !params.isParent2AutoriteParentale()) {
      return PARENT_1;
    } else if (!params.isParent1AutoriteParentale() && params.isParent2AutoriteParentale()) {
      return PARENT_2;
    } else {
      // Les deux parents ont l'autorité parentale, vérifier la situation de vie
      return handleParentsLivingArrangements(params);
    }
  }

  // Gère les situations de vie des parents
  private String handleParentsLivingArrangements(ParentAllocationParameters params) {
    if (params.isParentsViventEnsemble()) {
      return handleParentsLivingTogether(params);
    } else {
      return handleParentsLivingSeparately(params);
    }
  }

  // Logique lorsque les parents vivent ensemble
  private String handleParentsLivingTogether(ParentAllocationParameters params) {
    // Determine qui travaille dans le canton de domicile de l'enfant
    if (params.isParent1TravailleCantonDomicile() && !params.isParent2TravailleCantonDomicile()) {
      return PARENT_1;
    } else if (!params.isParent1TravailleCantonDomicile() && params.isParent2TravailleCantonDomicile()) {
      return PARENT_2;
    } else {
      // Les deux parents travaillent dans le canton de domicile de l'enfant ou aucun parent ne travaille dans le canton de domicile de l'enfant, vérifie l'activité indépendante et les salaires
      return checkIndependenceAndSalaries(params);
    }
  }

  // Vérifie l'activité indépendante et les salaires des parents
  private String checkIndependenceAndSalaries(ParentAllocationParameters params) {
    if (params.isParent1Independant() && !params.isParent2Independant()) {
      return PARENT_1;
    } else if (!params.isParent1Independant() && params.isParent2Independant()) {
      return PARENT_2;
    } else {
      // Both are either independent or salaried, compare salaries
      BigDecimal salaireP1 = params.getParent1Salaire();
      BigDecimal salaireP2 = params.getParent2Salaire();
      return salaireP1.compareTo(salaireP2) > 0 ? PARENT_1 : PARENT_2;
    }
  }

  // Logique lorsque les parents vivent séparément
  private String handleParentsLivingSeparately(ParentAllocationParameters params) {
    // Le parent qui vit avec l'enfant a le droit
    if (params.isParent1VitAvecEnfant() && !params.isParent2VitAvecEnfant()) {
      return PARENT_1;
    } else if (!params.isParent1VitAvecEnfant() && params.isParent2VitAvecEnfant()) {
      return PARENT_2;
    } else {
      // Aucun parent ne vit avec l'enfant ou les deux parents vivent avec l'enfant, aucun droit
      return AUCUN_DROIT;
    }
  }
}
