package StudentManagmentSystem.services;

import StudentManagmentSystem.models.Referent;
import StudentManagmentSystem.repository.interfaces.ReferentInterface;

import java.util.ArrayList;
import java.util.Optional;

public class ReferentService {

    private final ReferentInterface referentRepository;

    // Polje koje čuva trenutno ulogovanog referenta u memoriji tokom rada aplikacije
    private static Referent currentUser = null;

    public ReferentService(ReferentInterface referentRepository) {
        this.referentRepository = referentRepository;
    }

    /**
     * Pokušava prijavu referenta na sistem.
     */
    public boolean login(String referentId, String password) {
        if (referentId == null || password == null || referentId.isEmpty() || password.isEmpty()) {
            throw new IllegalArgumentException("Korisničko ime i lozinka ne smiju biti prazni.");
        }

        Optional<Referent> referent = referentRepository.login(referentId, password);

        if (referent.isPresent()) {
            currentUser = referent.get();
            return true;
        }

        return false;
    }

    /**
     * Odjava sa sistema.
     */
    public void logout() {
        currentUser = null;
    }

    /**
     * Vraća trenutno ulogovanog referenta.
     * Ovo ćeš koristiti u drugim servisima (npr. StudentService)
     * da bi znao čiji ID poslati u bazu (addedByReferentId).
     */
    public static Referent getCurrentUser() {
        return currentUser;
    }

    public void registerReferent(Referent referent) {
        validateReferent(referent);

        if (referentRepository.getReferentById(referent.getReferentId()).isPresent()) {
            throw new IllegalArgumentException("Referent sa ID-em " + referent.getReferentId() + " već postoji.");
        }

        referentRepository.addReferent(referent);
    }

    public ArrayList<Referent> getAllReferents() {
        return referentRepository.getAllReferents();
    }

    private void validateReferent(Referent referent) {
        if (referent.getReferentId() == null || referent.getReferentId().length() < 3) {
            throw new IllegalArgumentException("ID referenta mora imati najmanje 3 znaka.");
        }
        if (referent.getPassword() == null || referent.getPassword().length() < 4) {
            throw new IllegalArgumentException("Lozinka mora imati najmanje 4 znaka.");
        }
        if (referent.getFirstName() == null || referent.getLastName() == null) {
            throw new IllegalArgumentException("Ime i prezime su obavezni.");
        }
    }
}