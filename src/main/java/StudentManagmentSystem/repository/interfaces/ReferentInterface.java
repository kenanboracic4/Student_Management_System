package StudentManagmentSystem.repository.interfaces;

import StudentManagmentSystem.models.Referent;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Interfejs koji definiše operacije nad podacima o referentima (administratorima).
 * Pored standardnih CRUD operacija, ovaj interfejs je odgovoran za autentifikaciju
 * korisnika na sistemu.
 */
public interface ReferentInterface {

    /**
     * Registruje novog referenta u sistem.
     * * @param referent Objekat tipa {@link Referent} sa popunjenim podacima.
     */
    void addReferent(Referent referent);

    /**
     * Ažurira podatke o postojećem referentu (npr. promjena imena ili lozinke).
     * * @param referent Objekat sa ažuriranim podacima.
     * @return {@code true} ako je ažuriranje uspješno, inače {@code false}.
     */
    boolean updateReferent(Referent referent);

    /**
     * Briše nalog referenta iz sistema na osnovu njegovog ID-a.
     * * @param referentId Jedinstveni identifikator referenta kojeg treba obrisati.
     * @return {@code true} ako je brisanje izvršeno, inače {@code false}.
     */
    boolean deleteReferent(String referentId);

    /**
     * Pronalazi referenta na osnovu njegovog ID-a (username-a).
     * * @param referentId Jedinstveni ID referenta.
     * @return {@link Optional} koji sadrži referenta ili je prazan ako ID ne postoji.
     */
    Optional<Referent> getReferentById(String referentId);

    /**
     * Vraća listu svih referenata registrovanih u sistemu.
     * * @return {@link ArrayList} svih objekata {@link Referent}.
     */
    ArrayList<Referent> getAllReferents();

    /**
     * Vrši autentifikaciju referenta na osnovu ID-a i lozinke.
     * Ova metoda se koristi prilikom prijave na sistem.
     *
     * @param referentId Identifikator (username) koji korisnik unosi.
     * @param password Lozinka koju korisnik unosi.
     * @return {@link Optional} sa objektom {@link Referent} ako su kredencijali ispravni,
     * inače {@link Optional#empty()} ako su podaci netačni.
     */
    Optional<Referent> login(String referentId, String password);
}