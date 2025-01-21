package tictactoe.api;

import tictactoe.game.Match;

import java.util.List;

public class MatchResponse {

    private String message;
    private List<Match> matches;

    public MatchResponse(String message) {
        this.message = message;
    }
    public MatchResponse() {}





    public List<Match> getMatches() {
        return matches;
    }

    public void setMatches(List<Match> matches) {
        this.matches = matches;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
