

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
5. check matchStatus for "COMPUTER_WIN", "PLAYER_WIN" or "DRAW"


## Documentation:
API endpoint:  
GET http://localhost:8080/documentation





## Errors:
| code: | message                                           |
|-------|---------------------------------------------------|
| 0     | No Token provided.                                |
| 1     | Provided Token invalid.                           |
| 2     | Request Body Incomplete                           |
| 4     | Username does not Exist                           |
| 5     | Username or Password is incorrect                 |
| 6     | Username already exists                           |
| 7     | Username too short                                |
| 3     | Username too short                                |
| 8     | Password is empty                                 |
| 9     | Password too short                                |
| 10    | Password needs lowercase letter                   |
| 11    | Password needs uppercase letter                   |
| 12    | Password needs digit                              |
| 13    | Password needs special character                  |
| 14    | Security Questions Empty                          |
| 15    | Security Question is wrong                        |
| 16    | Match History is Empty.                           |
| 17    | Parameter seconds can't be zero or negative.      |
| 18    | Parameter amount can't be zero or negative        |
| 19    | Parameter seconds can only be a Long              |
| 20    | Parameter amount can only be an Int               |
| 21    | Parameter seconds can only be 209.999.999.999 max |
| 22    | Parameter amount can only be 1.000 max            |
| 23    | This Status does not exist.                       |
| 24    | This Difficulty does not exist.                   |
| 25    | Match Not Found                                   |


if there are issues with docker while testing like:  
Error: Could not find a valid Docker environment. Please see logs and check configuration  
run:  
sudo ln -s /Users/nilsbartel/.colima/default/docker.sock /var/run/docker.sock

 
