# Event Manager

## Projektni tim

Ime i prezime | E-mail adresa (FOI) | JMBAG | Github korisničko ime | Seminarska grupa
------------  | ------------------- | ----- | --------------------- | ----------------
Toni Leo Modrić Dragičević | tmodricdr20@student.foi.hr | 0016150195 | tmodricdr20 | G 1.2
Mateo Pikl | mpikl20@student.foi.hr | 0016151626 | mpikl | G 1.2
Dorian Rušak | drusak21@student.foi.hr | 0016155513 | drusak21 | G 1.2
Toni Papić | tpapic21@student.foi.hr | 0016154932 | tpapic21 | G 1.2

## Opis domene
Event Manager za Android je mobilna aplikacija koja omogućava organizaciju i upravljanje različitim vrstama događaja na mobilnim uređajima. Ova aplikacija olakšava korisnicima upravljanje događajima kao što su koncerti, konferencije, zabave, seminari ili bilo kakvi javni ili privatni događaji, putem njihovih Android pametnih telefona.

## Specifikacija projekta

Oznaka | Naziv | Kratki opis | Odgovorni član tima
------ | ----- | ----------- | -------------------
F01 | Registracija i login | Za pristup Event Manageru potrebna je registracija te zatim autentikacija korisnika pomoću registracijskih funkcionalnosti. Aplikacija bi trebala omogućiti korisnicima stvaranje računa pomoću podataka definiranih pri registraciji i zatim prijavu pomoću korisničkog imena i lozinke. | Mateo Pikl
F02 | Upravljanje eventima | Korisnici bi trebali moći stvarati, mijenjati i ažurirati nove i postojeće događaje pružanjem pojedinosti kao što su naziv događaja, datum, vrijeme, mjesto i opis. | Mateo Pikl
F03 | Upravljanje ulaznicama | Aplikacija bi trebala podržavati stvaranje i upravljanje ulaznicama, omogućujući organizatorima da postave cijene, količinu i vrste ulaznica (npr. VIP, obične) za svoje događaje. | Mateo Pikl
F04 | Provjera valjanosti karte qr kodom | Korisnik može provjeriti valjanost svoje karte za event skeniranjem qr koda. | Toni Leo Modrić Dragičević
F05 | Kategorizacija eventa | Korisnik može filtrirati događaje prema tipu (npr. sportski, koncerti, predstave)  | Toni Leo Modrić Dragičević
F06 | Podsjetnik događaja | Automatska obavijest prijavljenog događaja dan prije izvođenja istog događaja | Toni Leo Modrić Dragičević
F07 | Generiranje izvještaja|Aplikacija će omogućiti organizatorima generiranje izvještaja za događaj koji će biti dostupan dan nakon održavanja događaja. Izvještaj će sadržavati: naziv događaja, datum, mjesto održavanja, organizator događaja, broj osoba koji su bili prijavljeni, ukupni prihod. Izvještaj će se spremati u PDF formatu.  |Toni Papić
F08 | Prijavljivanje na događaje | Korisnici će se moći putem aplikacije prijaviti na postojeće događaje. Prilikom prikaza svih događaja, kod svakog događaja će biti gumb putem kojeg će se korisinci moći prijavit/odjaviti događaj za koji su zainteresirani . Na profilu svakog korisnika će biti dostupna lista svih događaja na koje su se prijavili. | Toni Papić
F09 | Upravljanje profilima | Aplikacija će omogućiti korisnicima uređivanje vlastitih profila, te brisanje profila uz dodatnu potvrdu korisnika. Moguće će biti promijeniti ime, prezime, email adresu, te broj telefona. Korisnici će također moći odabrati koji od njihovih podataka će biti vidljiv ostalim korisnicima, izuzetak su organizatori događaja kojima će biti dostupni svi osnovni navedneni podaci: ime, prezime, email adresa, broj telefona. | Toni Papić
F10 | Komentari i ocjene | Aplikacija će omogućiti korisnicima da ostavljaju komentare i ocjene nakon završetka događaja. | Dorian Rušak
F11 | Integracija s kartama(Google Maps) | Aplikacija će omogućiti korisnicima jednostavno pronalaženje lokacija događaja. | Dorian Rušak
F12 | Nagrade i bodovi | Najaktivniji sudionici će dobivati razne nagrade i bodove u aplikaciji. | Dorian Rušak
F13 | Upravljanje sponzorstvima | Pronalaženje i upravljanje sponzorima za događaje. | Dorian Rušak


## Tehnologije i oprema
* Kotlin
* Android Studio
* Github
* Firebase

## Baza podataka i web server
Trebamo MySQL bazu podataka i pristup serveru za PHP skripte. 

## .gitignore
Uzmite u obzir da je u mapi Software .gitignore konfiguriran za nekoliko tehnologija, ali samo ako će projekti biti smješteni direktno u mapu Software ali ne i u neku pod mapu. Nakon odabira konačne tehnologije i projekta obavezno dopunite/premjestite gitignore kako bi vaš projekt zadovoljavao kriterije koji su opisani u ReadMe.md dokumentu dostupnom u mapi Software.
