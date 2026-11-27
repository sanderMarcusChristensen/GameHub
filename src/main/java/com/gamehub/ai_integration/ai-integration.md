# AI integration

The AI coach compares a player with their active teammates using statistics from MySQL. Ollama generates the answer, and MySQL stores the latest answer per player and team.

## Java files

The following files are in `src/main/java/com/gamehub/ai_integration`:

- `PlayerChatController.java` receives questions from the chat.
- `PlayerChatService.java` finds the player named in the question and their active team.
- `TeamStatisticsController.java` exposes the team statistics endpoint.
- `TeamStatisticsService.java` calculates the team's average kills, deaths and assists per game.
- `PlayerAnalysisService.java` combines the prompt, game information, question and statistics, calls Ollama and saves its answer.
- `PlayerAnalysisController.java` exposes endpoints to generate or retrieve an analysis by player and team ID.
- `PlayerAnalysisRepository.java` saves and retrieves the latest analysis for a player and team using Spring's `JdbcTemplate`.

## Other files

- `src/main/resources/ai/player-analysis.txt` contains Traits, Tone and Target for the system message.
- `src/main/resources/ai/player-analysis-task.txt` contains the Task sent with each question.
- `src/main/resources/ai/general_leage_info.txt` contains the short game introduction sent with each question.
- `src/main/resources/static/index.html` contains the chat window.
- `src/main/resources/static/js/ai-chat.js` sends questions and displays answers.
- `src/main/resources/static/css/style.css` styles the chat.
- `src/main/resources/application.properties` connects the `.env` values to Spring AI.
- `data/database/init/07_player_ai_analysis.sql` creates the table for saved analyses.
- `pom.xml` includes Spring AI's Ollama dependency.

The player lookup at the top of the website uses CitoAPI. The AI coach uses players and team statistics from MySQL.
