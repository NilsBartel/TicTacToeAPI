package game;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import tictactoe.board.Board;
import tictactoe.board.Position;
import tictactoe.game.ComputerMoveService;
import tictactoe.game.Match;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ComputerMoveServiceTest {
    private static Board generateBoardForTest(int[] player, int[] computer) {
        Board board = new Board();
        for (int j : player) {
            Position position = new Position(j);
            board.setSymbol(position.getRow(), position.getColumn(), Match.PLAYER_SYMBOL);
        }
        for (int j : computer) {
            Position position = new Position(j);
            board.setSymbol(position.getRow(), position.getColumn(), Match.COMPUTER_SYMBOL);
        }
        return board;
    }

    private static Stream<Arguments> generateComputerMovesForTest() {
        return Stream.of(
                // tests winning move row
                Arguments.of(generateBoardForTest(new int[]{}, new int[]{1,2}), 3),

                // tests winning move column
                Arguments.of(generateBoardForTest(new int[]{}, new int[]{1,4}), 7),

                //prevents winning move row
                Arguments.of(generateBoardForTest(new int[]{1,2}, new int[]{}), 3),

                //prevents winning move column
                Arguments.of(generateBoardForTest(new int[]{1,4}, new int[]{}), 7),

                // tests first move on empty board
                Arguments.of(generateBoardForTest(new int[]{}, new int[]{}), 1),

                // first computer move if middle is not takes and is not first move on the board
                Arguments.of(generateBoardForTest(new int[]{1}, new int[]{}), 5),

                // takes winning move while there is opponent to block
                Arguments.of(generateBoardForTest(new int[]{1,4}, new int[]{3,6}), 9),

                // takes a fork
                Arguments.of(generateBoardForTest(new int[]{5,6}, new int[]{4,9}), 7),
                Arguments.of(generateBoardForTest(new int[]{5,8}, new int[]{2,9}), 3),

                // stopping opponents fork by making him defend
                Arguments.of(generateBoardForTest(new int[]{4,9}, new int[]{5,6}), 2),

                // defend double fork
                Arguments.of(generateBoardForTest(new int[]{5,9}, new int[]{1}), 3),
                Arguments.of(generateBoardForTest(new int[]{1, 9}, new int[]{5}), 2)
        );
    }

    @ParameterizedTest
    @MethodSource("generateComputerMovesForTest")
    void computerMove(Board board, int expected) {
        Position position = ComputerMoveService.impossibleComputerMove(board);

        assertEquals(expected, position.getIndex());
    }




    @Test
    void stoppingOpponentsForkByPlacingSymbolThere(){
        Board board = new Board();
        board.getRows().get(1).getFields().get(0).setSymbol(Match.PLAYER_SYMBOL);
        board.getRows().get(2).getFields().get(2).setSymbol(Match.PLAYER_SYMBOL);

        board.getRows().get(0).getFields().get(1).setSymbol('t');

        board.getRows().get(1).getFields().get(1).setSymbol(Match.COMPUTER_SYMBOL);
        board.getRows().get(1).getFields().get(2).setSymbol(Match.COMPUTER_SYMBOL);

        Position position = ComputerMoveService.impossibleComputerMove(board);

        assertEquals(7, position.getIndex());
    }
}
