package StudentManagmentSystem.repository.interfaces;

import StudentManagmentSystem.models.Referent;
import java.util.ArrayList;
import java.util.Optional;

public interface ReferentInterface {

    void addReferent(Referent referent);

    boolean updateReferent(Referent referent);

    boolean deleteReferent(String referentId);

    Optional<Referent> getReferentById(String referentId);

    ArrayList<Referent> getAllReferents();

    /**
     * Provjerava kredencijale referenta.
     * @return Referent objekt ako su podaci tačni, inače Optional.empty()
     */
    Optional<Referent> login(String referentId, String password);
}