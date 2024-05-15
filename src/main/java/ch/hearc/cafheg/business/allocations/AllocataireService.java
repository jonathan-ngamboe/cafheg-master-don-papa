package ch.hearc.cafheg.business.allocations;

import ch.hearc.cafheg.infrastructure.persistance.AllocataireMapper;
public class AllocataireService {
    private final AllocataireMapper allocataireMapper;

    public AllocataireService(AllocataireMapper allocataireMapper) {
        this.allocataireMapper = allocataireMapper;
    }

    public boolean deleteAllocataire(long id) {
        Allocataire allocataire = allocataireMapper.findById(id);
        if (allocataire == null) {
            throw new RuntimeException("Allocataire introuvable avec l'ID : " + id);
        }

        if (allocataireMapper.hasVersements(id)) {
            throw new RuntimeException("L'allocataire a des versements et ne peut pas être supprimé.");
        }

        return allocataireMapper.deleteById(id);
    }

    public Allocataire updateNomPrenom(long id, String nouveauNom, String nouveauPrenom) {
        Allocataire allocataire = allocataireMapper.findById(id);
        Allocataire nouveauAllocataire = null;
        if (allocataire == null) {
            throw new RuntimeException("Allocataire introuvable avec l'ID : " + id);
        }

        if (!allocataire.getNom().equals(nouveauNom) || !allocataire.getPrenom().equals(nouveauPrenom)) {
            allocataireMapper.updateNomPrenom(id, nouveauNom, nouveauPrenom);
        }

        return nouveauAllocataire;
    }
}
