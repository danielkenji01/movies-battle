Greetings!

This is a simple game called Movies Battle. 
The objective is to guess the movie within two options with the best ratings based on IMDB data.
The player have only 3 credits. On the third miss, the match is ended automatically and the score is calculated. 
The player can end the match whenever he/she wants.

Here is a quick guide on how to play:

1. Create a new player:
   - /api/players
2. Login:
   - /api/players/login
3. Start new match:
   - /api/match/start
4. Next round:
   - /api/match/next-round
5. Answer:
   - /api/match/answer
6. End match
    - /api/match/end

To see more details on how to use the endpoints, check the Open API documentation: http://localhost:8081/swagger-ui/index.html