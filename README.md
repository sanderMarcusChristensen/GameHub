# GameHub

GameHub is an information hub for League of Legends data. The initial version will focus on professional players, their accounts, match history and player statistics.

The project uses the official Riot Games API to retrieve League of Legends account and match data.

## Riot Games development account

To use the Riot Games API, each developer must create a free account on the Riot Developer Portal:

https://developer.riotgames.com/

1. Sign in with a Riot Games account.
2. Open the Riot Developer Portal.
3. Generate a development API key.
4. Copy the generated key into the local `.env` file.

A Riot development API key is temporary and expires after 24 hours. When the key expires, a new key must be generated on the Riot Developer Portal and inserted into the `.env` file.

The API key must never be committed to GitHub or shared publicly.

## CitoAPI account

To use the CitoAPI, each developer must create an account and obtain an API key:

https://citoapi.com/

1. Create or sign in to a CitoAPI account.
2. Open the CitoAPI dashboard.
3. Generate an API key.
4. Copy the API key into the local `.env` file.

The API key must never be committed to GitHub or shared publicly.

## Environment variables

Create a file named `.env` in the root directory of the project:

```env
CITO_API_KEY=cito_live_INSERT_YOUR_KEY_HERE
```
## Environment variables

Create a file named `.env` in the root directory of the project:

```env
RIOT_API_KEY=RGAPI_INSERT_YOUR_KEY_HERE
```

Do not add quotation marks around the key.

A template named `.env.example` can be included in the repository:

