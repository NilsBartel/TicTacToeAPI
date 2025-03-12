

# Tic Tac Toe API
This is a TicTacToe game that is build with Java and can be played via an api.

## Description:
The game can be played against the computer on three different difficulties, easy, medium and impossible. Each player has
their own account where their match history and their score is saved in a database.



## Getting set up:
1. have **docker** running
2. **run the start-local-setup.sh script** at local/start-local-setup.sh
3. **run main** at src/main/java/Main
4. you are ready to go 


## playing a match:
1. create account (/account/register)
2. log in and receive the Authentication token (/account/login)
3. start a match (/match/start)
4. play back and forth (/match/play)





## API endpoints:
http://localhost:8080  
account:
- POST /account/login  
  - logs a user in  
  - **needs:** username and password as json body  
  - e.g.: {"userName":"test", "password":"Test1234!"}  
  - **returns:** Authentication token
- POST /account/register  
  - creates a new account  
  - **needs:** username, password, security answer1, security answer2 as json body
  - e.g.: {"userName":"test", "password":"Test1234!", "answer1":"bruno", "answer2":"hamburg"}  
  - **returns:** nothing
- POST /account/resetPassword 
  - resets a users password with the given security questions  
  - **needs:** username, new password, security answer1, security answer2 as json body  
  - e.g.: {"userName":"test", "password":"tEst123456!", "answer1":"bruno", "answer2":"hamburg"}  
  - **returns:** nothing

match:
- GET /match/{match_id}
  - returns a specific match from the database
  - **needs:** token as header
  - e.g.: "token" "M3Abzml44RhZyV4zTUNl3Xwayadh0kxv"
  - **returns:** a match as json
- POST /match/play
  - takes a match and returns the match after the computer played
  - **needs:** token as header
  - **needs:** match as json body (e.g. see below)
  - **returns:** match as json
- GET /match/create
  - creates a new match
  - **needs:** token as header
  - **needs:** difficulty as json body
  - e.g.: {"difficulty": "EASY"} (EASY/MEDIUM/IMPOSSIBLE)
  - **returns:** match as json
- GET /match/start
  - checks if a player still has a running match and returns it. Returns a new match if not.
  - **needs:** token as header
  - **needs:** difficulty as json body
  - e.g.: {"difficulty": "EASY"} (EASY/MEDIUM/IMPOSSIBLE)
  - **returns:** match as json
- GET /match/matchHistory/{number of matches}
  - returns the given number of matches from the players match history
  - **needs:** token as header
  - **returns:** a list of matches as json

score:
- GET /score/
    - returns the score of player, computer and draw
    - **needs:** token as header
    - **returns:** the score
    - e.g.: {"playerScore": 0, "computerScore": 0, "drawCount": 0}

analyze:
- GET /analyze/
  - gives an analyzation of the lines that won the most in this players match history 
  - **needs:** token as header
  - **returns:** a list of Positions as the representation of the line and an Integer of how often that line was played

    
Example of a match Object:
```json
{
  "board": {
    "rows": [
      {
        "fields": [
          {
            "symbol": "x"
          },
          {
            "symbol": "o"
          },
          {
            "symbol": " "
          }
        ]
      },
      {
        "fields": [
          {
            "symbol": " "
          },
          {
            "symbol": " "
          },
          {
            "symbol": " "
          }
        ]
      },
      {
        "fields": [
          {
            "symbol": " "
          },
          {
            "symbol": " "
          },
          {
            "symbol": " "
          }
        ]
      }
    ]
  },
  "status": "RUNNING",
  "difficulty": "EASY",
  "isPlayerTurn": true,
  "matchID": 1,
  "startTime": 1741780699448,
  "endTime": null
}
```
