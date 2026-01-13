# Studentska Služba - Mini Informacioni Sistem

**Predmet:** Razvoj softvera (Java)  
**Student:** Kenan Boračić 
**Indeks:** 473/IT-24 
**Akademska godina:** 2025/2026.

---

## 📄 O Projektu
"Studentska Služba" je softversko rješenje dizajnirano da automatizuje i digitalizuje ključne administrativne procese na visokoškolskoj ustanovi. Aplikacija omogućava potpunu evidenciju studenata, nastavnog plana (predmeta), te upravljanje akademskim procesima poput upisa semestra i ocjenjivanja.

Sistem je implementiran u programskom jeziku **Java**, koristeći **Swing** biblioteku za grafički interfejs. Poseban akcenat tokom razvoja stavljen je na robusnu arhitekturu, odvajanje poslovne logike od prezentacionog sloja i konzistentnost podataka.

---

## 🏗️ Arhitektura i Struktura Projekta
Projekat je organizovan prema **višeslojnoj arhitekturi (N-Tier)**, gdje svaki paket ima jasno definisanu odgovornost. Ovakva struktura osigurava modularnost i olakšava buduće nadogradnje.

### Struktura paketa:

1.  **`models`** (Sloj Podataka)
    * Sadrži POJO (Plain Old Java Object) klase koje predstavljaju entitete sistema: `Student`, `Course` (Predmet), `Enrollment` (Upis) i `Referent`. Ovi objekti prenose podatke kroz aplikaciju.

2.  **`repository`** (Sloj koji priča s bazom)
    * **`interfaces`**: Definišu ugovore za rad sa podacima (`StudentInterface`, `CourseInterface`...).
    * **`implementations`**: Konkretne implementacije koje komuniciraju sa bazom podataka (`StudentRepository`, `EnrollmentRepository`...). Ovo omogućava laku zamjenu izvora podataka (npr. prelazak sa SQLite na drugu bazu) bez mijenjanja ostatka koda.

3.  **`services`** (Sloj Poslovne Logike)
    * Sadrži klase kao što su `StudentService`, `CourseService`, `EnrollmentService`.
    * Ovdje se vrše sve validacije (npr. provjera da li je ocjena između 5 i 10, da li student već ima upisan predmet) prije nego što se podaci proslijede repozitorijumu. Ovaj sloj štiti integritet podataka.

4.  **`ui`** (Prezentacioni Sloj)
    * **`gui`**: Sadrži grafički interfejs podijeljen na logičke cjeline:
        * `auth`: Ekrani za prijavu (`LoginFrame`).
        * `dashboard`: Glavni kontejneri (`MainDashboard`, `StatPanel`).
        * `components`: Zasebni paneli za tabele i prikaze (`StudentTablePanel`, `StudentReportPanel`).
        * `dialogs`: Modalni prozori za unos i interakciju (`AddStudentDialog`, `GradingDialog`).
        * `util`: Pomoćne klase za dizajn (`SwingUtil`).
    * **`ConsoleUI`**: Implementacija konzolnog interfejsa za alternativni način rada.

5.  **`DbConnection`**
    * Klasa zadužena za uspostavljanje konekcije sa lokalnom **SQLite** bazom podataka i inicijalno kreiranje tabela.

---

## Ključne Funkcionalnosti

### Kontrola Pristupa (RBAC)
Sistem podržava dvije uloge korisnika sa različitim privilegijama:
* **Referent (Administrator):** Ima puni pristup sistemu, uključujući dodavanje studenata, predmeta, upisivanje ocjena i uvid u statistiku.
* **Student:** Ima ograničen pristup ("View-Only"), gdje može vidjeti isključivo svoj akademski karton i status položenih ispita.

### Upravljanje Akademskim Procesima
* **Evidencija Studenata i Predmeta:** CRUD operacije sa validacijom unosa (npr. jedinstvenost indeksa).
* **Sistem Upisa:** Onemogućen je višestruki upis istog studenta na isti predmet u jednoj akademskoj godini.
* **Ocjenjivanje:** Proces unosa ocjene zahtijeva autorizaciju referenta. Sistem bilježi istoriju izmjena (Audit Trail) – svaka naknadna izmjena ocjene zahtijeva unos razloga promjene.

### Korisničko Iskustvo (UX)
* **Napredna Pretraga:** Implementirana je filtracija tabela u realnom vremenu.
* **Statistika:** Početni ekran prikazuje ključne metrike (broj studenata, predmeta, prosjek ocjena).
* **Moderni Dizajn:** Korišten je prilagođeni *flat* dizajn komponenti (definisano u `SwingUtil`) radi bolje preglednosti i modernijeg izgleda aplikacije.

---

## Uputstvo za Pokretanje

### Sistemski Zahtjevi
* **Java SDK:** Verzija 17 ili novija.
* **Baza podataka:** SQLite (JDBC drajver je uključen u zavisnosti projekta).

### Koraci za pokretanje
1.  Učitajte projekat u IntelliJ IDEA ili Eclipse.
2.  Osigurajte da su sve zavisnosti (biblioteke) preuzete.
3.  Pronađite klasu **`Main.java`** u root paketu.
4.  Pokrenite metodu `main()`.

> **Napomena:** Prilikom prvog pokretanja, aplikacija će automatski kreirati fajl baze podataka (`studentska_sluzba.db`) u korijenskom folderu projekta i inicijalizovati potrebne tabele. Nije potrebna ručna konfiguracija SQL skripti.

---

## Zaključak
Projekat uspješno demonstrira primjenu objektno-orijentisanih principa i dobrih praksi softverskog inženjerstva. Kroz jasnu separaciju slojeva (Repository), Dependency Injection u `Main` klasi i modularni UI, postignut je sistem koji je stabilan, lak za testiranje i spreman za dalja proširenja.