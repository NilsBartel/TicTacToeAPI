package api.databaseData;

import org.junit.jupiter.api.Test;
import tictactoe.board.Board;
import tictactoe.game.DifficultyState;
import tictactoe.game.Match;
import tictactoe.game.MatchStatus;

import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CreateNewData {

    //change to main and run
    public static void main(String[] args) {
        create();
    }




    public static void create() {
        int userId = 5; //change
        int matchId = 5; //change

        List<Character> boardList = List.of('o',  'x',  ' ',
                                            ' ',  'x',  ' ',
                                            ' ',  'x',  'o'
        ); //change
        MatchStatus matchStatus = MatchStatus.PLAYER_WON; //change
        DifficultyState difficultyState = DifficultyState.EASY; //change







        long startMillis = System.currentTimeMillis();
        long endMillis = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(15);
        Timestamp startTime = new Timestamp(startMillis);
        Timestamp endTime = new Timestamp(endMillis);

        Board board = new Board(boardList);
        Match match = new Match(board, matchStatus, difficultyState, true, matchId, startTime, endTime);

        System.out.println();
        System.out.println();
        System.out.println("MatchData:");
        System.out.println(
                "private static final Board board"+matchId+" = new Board(List.of(" +
                        "'"+boardList.get(0)+"', " +
                        "'"+boardList.get(1)+"', " +
                        "'"+boardList.get(2)+"', " +
                        "'"+boardList.get(3)+"', " +
                        "'"+boardList.get(4)+"', " +
                        "'"+boardList.get(5)+"', " +
                        "'"+boardList.get(6)+"', " +
                        "'"+boardList.get(7)+"', " +
                        "'"+boardList.get(8)+"'));"
        );

        System.out.println(
                "private static final Match match"+matchId+
                        " = new Match(board"+matchId+", " +
                        "MatchStatus."+matchStatus+", DifficultyState."+difficultyState+", true, "+matchId+", " +
                        "new Timestamp("+startMillis+"L), new Timestamp("+endMillis+"L));"
        );


        System.out.println();
        System.out.println();
        System.out.println("Match:");
        System.out.println(matchId+","+match.getDifficulty()+","+match.getStatus()+","+match.isIsPlayerTurn()+","+userId);

        System.out.println();
        System.out.println("Time:");
        System.out.println(matchId+","+startTime+","+endTime+","+matchId);

        System.out.println();
        System.out.println("Board:");
        System.out.println(matchId+","+matchId);

        System.out.println();
        System.out.println("Row:");
        int rowId = (matchId*3)-2;
        for (int i = 0; i < 3; i++) {
            System.out.println(rowId+i+","+i+","+matchId);
        }

        System.out.println();
        System.out.println("Fields:");
        int fieldId = 9*(matchId-1)+1;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                System.out.println(fieldId+","+j+","+board.getSymbol(i,j)+","+rowId);
                fieldId++;
            }
            rowId++;
        }






    }

















}
