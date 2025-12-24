package StudentManagmentSystem.models;

import java.util.Objects;

public class Referent {
    private String referentId;
    private String password; // Nova kolona
    private String firstName;
    private String lastName;
    private String createdAt;

    // Konstruktor za puni prikaz (čitanje iz baze)
    public Referent(String referentId, String password, String firstName, String lastName, String createdAt) {
        this.referentId = referentId;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.createdAt = createdAt;
    }

    // Konstruktor za kreiranje novog referenta (prije unosa)
    public Referent(String referentId, String password, String firstName, String lastName) {
        this.referentId = referentId;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // Getters and Setters
    public String getReferentId() { return referentId; }
    public void setReferentId(String referentId) { this.referentId = referentId; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Referent: [" + referentId + "] " + firstName + " " + lastName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Referent referent = (Referent) o;
        return Objects.equals(referentId, referent.referentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(referentId);
    }
}