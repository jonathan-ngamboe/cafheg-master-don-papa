package ch.hearc.cafheg.business.allocations;

import java.math.BigDecimal;

public class ParentAllocationParameters {
    private boolean parent1Salarie;
    private boolean parent2Salarie;
    private BigDecimal parent1Salaire;
    private BigDecimal parent2Salaire;
    private boolean parent1VitAvecEnfant;
    private boolean parent2VitAvecEnfant;
    private boolean parentsViventEnsemble;
    private boolean parent1AutoriteParentale;
    private boolean parent2AutoriteParentale;
    private boolean parent1TravailleCantonDomicile;
    private boolean parent2TravailleCantonDomicile;
    private boolean parent1Independant;
    private boolean parent2Independant;

    public ParentAllocationParameters(boolean parent1Salarie,
                                      boolean parent2Salarie,
                                      BigDecimal parent1Salaire,
                                      BigDecimal parent2Salaire,
                                      boolean parent1VitAvecEnfant,
                                      boolean parent2VitAvecEnfant,
                                      boolean parentsViventEnsemble,
                                      boolean parent1AutoriteParentale,
                                      boolean parent2AutoriteParentale,
                                      boolean parent1TravailleCantonDomicile,
                                      boolean parent2TravailleCantonDomicile,
                                      boolean parent1Independant,
                                      boolean parent2Independant) {
        this.parent1Salarie = parent1Salarie;
        this.parent2Salarie = parent2Salarie;
        this.parent1Salaire = parent1Salaire;
        this.parent2Salaire = parent2Salaire;
        this.parent1VitAvecEnfant = parent1VitAvecEnfant;
        this.parent2VitAvecEnfant = parent2VitAvecEnfant;
        this.parentsViventEnsemble = parentsViventEnsemble;
        this.parent1AutoriteParentale = parent1AutoriteParentale;
        this.parent2AutoriteParentale = parent2AutoriteParentale;
        this.parent1TravailleCantonDomicile = parent1TravailleCantonDomicile;
        this.parent2TravailleCantonDomicile = parent2TravailleCantonDomicile;
        this.parent1Independant = parent1Independant;
        this.parent2Independant = parent2Independant;
    }

    public ParentAllocationParameters() {
    }

    public boolean isParent1ActiviteLucrative() {
        return parent1Salarie || parent1Independant;
    }

    public boolean isParent2ActiviteLucrative() {
        return parent2Salarie || parent2Independant;
    }

    public boolean isParent1Salarie() {
        return parent1Salarie;
    }

    public boolean isParent2Salarie() {
        return parent2Salarie;
    }

    public boolean isParentsViventEnsemble() {
        return parentsViventEnsemble;
    }

    public boolean isParent1VitAvecEnfant() {
        return parent1VitAvecEnfant;
    }

    public boolean isParent2VitAvecEnfant() {
        return parent2VitAvecEnfant;
    }

    public boolean isParent1TravailleCantonDomicile() {
        return parent1TravailleCantonDomicile;
    }

    public boolean isParent2TravailleCantonDomicile() {
        return parent2TravailleCantonDomicile;
    }

    public boolean isParent1Independant() {
        return parent1Independant;
    }

    public boolean isParent2Independant() {
        return parent2Independant;
    }

    public boolean isParent1AutoriteParentale() {
        return parent1AutoriteParentale;
    }

    public boolean isParent2AutoriteParentale() {
        return parent2AutoriteParentale;
    }

    public BigDecimal getParent1Salaire() {
        return parent1Salaire;
    }

    public BigDecimal getParent2Salaire() {
        return parent2Salaire;
    }
}
