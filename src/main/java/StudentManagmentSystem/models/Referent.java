package StudentManagmentSystem.models;

import java.util.Objects;

/**
 * Predstavlja model referenta (administratora sistema).
 * Sadrži podatke za autentifikaciju (referentId, password) i osnovne lične podatke.
 * * @author Kenan Boračić
 * @version 1.0
 */
public class Referent {

    /** Jedinstveni identifikator (korisničko ime) referenta u sistemu */
    private String referentId;

    /** Lozinka referenta za pristup sistemu */
    private String password;

    /** Ime referenta */
    private String firstName;

    /** Prezime referenta */
    private String lastName;

    /** Datum i vrijeme kada je nalog referenta kreiran */
    private String createdAt;

    /**
     * Konstruktor za puni prikaz objekta Referent.
     * Obično se koristi prilikom učitavanja postojećeg referenta iz baze podataka.
     *
     * @param referentId Jedinstveni identifikator (username) referenta.
     * @param password Lozinka za pristup sistemu.
     * @param firstName Ime referenta.
     * @param lastName Prezime referenta.
     * @param createdAt Datum i vrijeme kreiranja naloga.
     */
    public Referent(String referentId, String password, String firstName, String lastName, String createdAt) {
        this.referentId = referentId;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.createdAt = createdAt;
    }

    /**
     * Konstruktor za kreiranje novog referenta.
     * Koristi se prilikom registracije ili prvog unosa referenta u sistem
     * prije nego što baza generiše datum kreiranja.
     *
     * @param referentId Jedinstveni identifikator referenta.
     * @param password Lozinka referenta.
     * @param firstName Ime.
     * @param lastName Prezime.
     */
    public Referent(String referentId, String password, String firstName, String lastName) {
        this.referentId = referentId;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // --- GETTERS AND SETTERS ---

    /** * Dohvata jedinstveni ID referenta.
     * @return String koji predstavlja referentId.
     */
    public String getReferentId() { return referentId; }

    /** * Postavlja jedinstveni ID referenta.
     * @param referentId Novi ID referenta.
     */
    public void setReferentId(String referentId) { this.referentId = referentId; }

    /** * Dohvata lozinku referenta.
     * @return String koji predstavlja lozinku.
     */
    public String getPassword() { return password; }

    /** * Postavlja novu lozinku za referenta.
     * @param password Nova lozinka.
     */
    public void setPassword(String password) { this.password = password; }

    /** * Dohvata ime referenta.
     * @return Ime kao String.
     */
    public String getFirstName() { return firstName; }

    /** * Postavlja ime referenta.
     * @param firstName Novo ime.
     */
    public void setFirstName(String firstName) { this.firstName = firstName; }

    /** * Dohvata prezime referenta.
     * @return Prezime kao String.
     */
    public String getLastName() { return lastName; }

    /** * Postavlja prezime referenta.
     * @param lastName Novo prezime.
     */
    public void setLastName(String lastName) { this.lastName = lastName; }

    /** * Dohvata datum kreiranja naloga.
     * @return Datum u tekstualnom formatu.
     */
    public String getCreatedAt() { return createdAt; }

    /** * Postavlja datum kreiranja naloga.
     * @param createdAt Datum kreiranja zapisa.
     */
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    /**
     * Vraća formatiran string sa informacijama o referentu.
     * @return String u formatu "Referent: [ID] Ime Prezime".
     */
    @Override
    public String toString() {
        return "Referent: [" + referentId + "] " + firstName + " " + lastName;
    }

    /**
     * Poredi dva referenta na osnovu njihovog jedinstvenog ID-a.
     * * @param o Objekat koji se poredi.
     * @return true ako su referentId-ovi identični, inače false.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Referent referent = (Referent) o;
        return Objects.equals(referentId, referent.referentId);
    }

    /**
     * Generiše hash kod koristeći isključivo referentId polje.
     * @return int hash kod za instancu referenta.
     */
    @Override
    public int hashCode() {
        return Objects.hash(referentId);
    }
}