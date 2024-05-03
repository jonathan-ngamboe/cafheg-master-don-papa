package ch.hearc.cafheg.business.allocations;

import java.math.BigDecimal;

public class ParentAllocationParameters {
    private boolean parent1ActiviteLucrative;
    private boolean parent2ActiviteLucrative;
    private BigDecimal parent1Salaire;
    private BigDecimal parent2Salaire;

    public ParentAllocationParameters(boolean parent1ActiviteLucrative, boolean parent2ActiviteLucrative,
                                      BigDecimal parent1Salaire, BigDecimal parent2Salaire) {
        this.parent1ActiviteLucrative = parent1ActiviteLucrative;
        this.parent2ActiviteLucrative = parent2ActiviteLucrative;
        this.parent1Salaire = parent1Salaire;
        this.parent2Salaire = parent2Salaire;
    }

    public boolean isParent1ActiviteLucrative() {
        return parent1ActiviteLucrative;
    }

    public boolean isParent2ActiviteLucrative() {
        return parent2ActiviteLucrative;
    }

    public BigDecimal getParent1Salaire() {
        return parent1Salaire;
    }

    public BigDecimal getParent2Salaire() {
        return parent2Salaire;
    }
}
