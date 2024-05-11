package ch.hearc.cafheg.business.allocations;

import ch.hearc.cafheg.business.versements.VersementAllocation;

import java.util.ArrayList;

public class Allocataire {

  private final NoAVS noAVS;
  private final String nom;
  private final String prenom;

  private final ArrayList<VersementAllocation> versements;

  public Allocataire(NoAVS noAVS, String nom, String prenom) {
    this.noAVS = noAVS;
    this.nom = nom;
    this.prenom = prenom;
    this.versements = new ArrayList<>();
  }

  public String getNom() {
    return nom;
  }

  public String getPrenom() {
    return prenom;
  }

  public NoAVS getNoAVS() {
    return noAVS;
  }
}
