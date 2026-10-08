
# Stored procedures

En stored procedure er SQL-kode, som er gemt i databasen og køres med `CALL`.

### `sp_save_team_membership`
Opretter en spillers medlemsperiode på et hold eller opdaterer slutdatoen.

- Kontrollerer datoerne.
- Afviser overlappende perioder for samme spiller og hold.
- Tillader, at spilleren forlader og senere vender tilbage til holdet.

### `sp_record_game_result`
Gemmer eller retter resultatet af et game.

- Kontrollerer, at vinderen deltager i matchen.
- Kontrollerer tidspunkter, game-numre og matchformat.
- Opdaterer automatisk matchens status og vinder.
- Ruller ændringen tilbage, hvis resultatet er ugyldigt.

Eksempel: Når et hold har vundet to games i en BO3, afsluttes matchen med det hold som vinder.

### `sp_save_player_game_stats`
Gemmer eller opdaterer en spillers champion, hold, kills, deaths og assists i et game.

- Kontrollerer, at holdet deltager i matchen.
- Afviser negative statistiktal.
- Opdaterer en eksisterende række frem for at oprette en dublet.

Den kræver ikke, at spilleren har en registreret medlemsperiode på holdet.

### `sp_get_tournament_standings`
Viser en turnerings tilmeldte hold med antal afsluttede matches, sejre, nederlag og match-winrate.

Hold uden resultater vises også. Proceduren beregner ikke særlige turneringspoint eller tie-break-regler.

# Stored functions

En stored function returnerer én værdi og kan bruges i en `SELECT`.

### `fn_team_match_win_rate`
Returnerer et holds procentdel af vundne, afsluttede matches.

Eksempel: To sejre ud af tre matches giver `66.67`.

### `fn_team_game_win_rate`
Returnerer et holds procentdel af vundne, afsluttede games i igangværende eller afsluttede matches. Annullerede matches tæller ikke med.

Begge functions returnerer `NULL`, hvis holdet ikke har relevante resultater. Et ukendt hold-id giver en fejl.

## Kør tests

Fra projektets rodmappe:

    bash data/database/test_queries/run_tests.sh

Hvis terminalen allerede står i `test_queries`:

    bash run_tests.sh

Scriptet kører alle `.sql`-filer direkte i mappen i alfabetisk rækkefølge. Filerne bruger samme databaseforbindelse, så testvariabler kan deles.

- `PASS`: Kontrollen gav det forventede resultat.
- `FAIL`: Kontrollen gav et forkert resultat.
- En uventet SQL-fejl stopper kørslen.

Beskeden “Ingen SQL-fejl” betyder ikke automatisk, at alle kontroller viser `PASS`.

Testene opretter data, som bliver liggende efter kørslen.

## Hvad har vi testet?

| Fil | Kontrol |
|---|---|
| `01_test_tables.sql` | Viser, at de 11 tabeller, fire procedures og to functions findes. |
| `02_test_match.sql` | Søger efter eksisterende matches med ugyldige hold, vindere eller kombinationer af status og vinder. |
| `03_test_functions_and_procedures.sql` | Opretter testdata til de efterfølgende tests. Dette er primært opsætning. |
| `04_test_can_a_player_get_back_to_same_team.sql` | Opretter og afslutter en medlemsperiode og registrerer spillerens tilbagevenden. |
| `05_test_match_results_and_winrates.sql` | Kontrollerer manglende resultater, BO3-status, matchvinder og begge winrates. |
| `06_test_player_statistics.sql` | Kontrollerer indsættelse og opdatering af statistik uden dubletter. |
| `07_test_invalid_result_rollback.sql` | Kontrollerer, at en ugyldig resultatændring afvises, og at rollback bevarer data. |

Den seneste kørsel viste `PASS` i alle ni automatiske kontrolrækker og ingen uventede SQL-fejl.

## Hvad mangler vi at teste?

- BO1 og BO5 med forventet matchstatus og vinder.
- Turneringsoversigten, herunder hold uden resultater og adskillelse af turneringer.
- Afvisning af en vinder eller et statistikhold, som ikke deltager.
- Afvisning af negative statistiktal og ugyldige datoer.
- Afvisning af overlappende medlemsperioder.
- Afvisning af manglende game-numre og resultater til annullerede matches.
- Gyldige rettelser af tidligere game-resultater.
- Ukendte ID’er, dubletter og direkte brud på foreign keys og CHECK-regler.
- Samtidige ændringer fra flere databaseforbindelser.

Views, triggers, events og ekstra indexes skal testes, når de bliver implementeret. For indexes skal vi også undersøge forespørgslernes udførelse før og efter ændringen.