package tictactoe.game;

import tictactoe.board.*;
import tictactoe.database.*;

import com.zaxxer.hikari.HikariDataSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class AnalyseService {
    private static AnalyseService instance;
    private AnalyseService() {
    }

    public static AnalyseService getInstance() {
        if (instance == null) {
            instance = new AnalyseService();
        }
        return instance;
    }

    public Map<List<Position>, Integer> findBestWinningLine(int userID, HikariDataSource dataSource) {

        Map<List<Position>, Integer> map = new HashMap<>();
        List<List<Position>> wins = new ArrayList<>();
        //List<Match> matches = DB_Match.getAllMatchesFromUserWhereWin(userID, dataSource);
        List<Match> matches = DBMatch.getAllMatchesFromUser(userID, dataSource);

        for (Match match : matches) {
            if (match.isStatusEqual(MatchStatus.PLAYER_WON) || match.isStatusEqual(MatchStatus.COMPUTER_WON)) {
                wins.add(Winner.findWinningRow(match.getBoard(), getWinnerSymbol(match.getStatus())));
            }

        }

        for (List<Position> win : wins) {

            if (map.containsKey(win)) {
                map.put(win, map.get(win) + 1);
            } else {
                map.put(win, 1);
            }
        }

        return map;
    }



    private Character getWinnerSymbol(MatchStatus status) {
        switch (status) {
            case COMPUTER_WON -> {
                return Match.COMPUTER_SYMBOL;
            }
            case PLAYER_WON -> {
                return Match.PLAYER_SYMBOL;
            }
            default -> {
                return null;
            }
        }
    }

}
