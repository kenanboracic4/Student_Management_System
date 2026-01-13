package StudentManagmentSystem.services;

import StudentManagmentSystem.models.Referent;
import StudentManagmentSystem.repository.interfaces.ReferentInterface;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Servis zadužen za upravljanje autentifikacijom i autorizacijom referenata.
 * Ova klasa održava stanje trenutno ulogovanog korisnika (sesiju) i pruža
 * mehanizme za prijavu, odjavu i registraciju novih referenata.
 */
public class ReferentService {

    private final ReferentInterface referentRepository;

    /**
     * Statičko polje koje čuva referencu na trenutno ulogovanog referenta.
     * Omogućava globalni pristup podacima o operateru unutar aplikacije.
     */
    private static Referent currentUser = null;

    /**
     * Konstruktor koji povezuje servis sa repozitorijumom za referente.
     * * @param referentRepository Implementacija repozitorijuma za rad sa podacima referenata.
     */
    public ReferentService(ReferentInterface referentRepository) {
        this.referentRepository = referentRepository;
    }

    /**
     * Vrši autentifikaciju referenta na sistem.
     * Ukoliko su podaci ispravni, postavlja korisnika u {@code currentUser} sesiju.
     *
     * @param referentId Korisnički identifikator referenta.
     * @param password Lozinka referenta.
     * @return {@code true} ako je prijava uspješna, inače {@code false}.
     * @throws IllegalArgumentException Ukoliko su parametri prazni ili null.
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
     * Uništava trenutnu sesiju postavljanjem {@code currentUser} na {@code null}.
     * Koristi se prilikom odjave korisnika sa sistema.
     */
    public void logout() {
        currentUser = null;
    }

    /**
     * Globalni getter za trenutno aktivnog korisnika sistema.
     * Neophodan za audit logove (bilježenje ko je dodao studenta ili predmet).
     *
     * @return Objekat {@link Referent} koji predstavlja trenutno ulogovanu osobu.
     */
    public static Referent getCurrentUser() {
        return currentUser;
    }

    /**
     * Registruje novog referenta u sistem uz prethodnu validaciju podataka.
     * Provjerava da li je ID referenta već zauzet.
     *
     * @param referent Objekat referenta koji se registruje.
     * @throws IllegalArgumentException Ako su podaci neispravni ili ID već postoji.
     */
    public void registerReferent(Referent referent) {
        validateReferent(referent);

        if (referentRepository.getReferentById(referent.getReferentId()).isPresent()) {
            throw new IllegalArgumentException("Referent sa ID-em " + referent.getReferentId() + " već postoji.");
        }

        referentRepository.addReferent(referent);
    }

    /**
     * Vraća listu svih registrovanih referenata.
     *
     * @return {@link ArrayList} svih referenata u sistemu.
     */
    public ArrayList<Referent> getAllReferents() {
        return referentRepository.getAllReferents();
    }

    /**
     * Interna validacija podataka referenta.
     * Provjerava minimalnu dužinu ID-a (3 znaka) i lozinke (4 znaka),
     * kao i obaveznost imena i prezimena.
     *
     * @param referent Objekat koji se validira.
     * @throws IllegalArgumentException Ukoliko podaci ne ispunjavaju sigurnosne zahtjeve.
     */
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