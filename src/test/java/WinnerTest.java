import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class WinnerTest {


    private static Board generateBoard(int[] player, int[] computer) {
        Board board = new Board();

        for (int j : player) {
            Position position = new Position(j);
            board.getRows().get(position.getRow()).getFields().get(position.getColumn()).setSymbol(Match.PLAYER_SYMBOL);
        }

        for (int j : computer) {
            Position position = new Position(j);
            board.getRows().get(position.getRow()).getFields().get(position.getColumn()).setSymbol(Match.COMPUTER_SYMBOL);
        }

        return board;
    }

    private static Stream<Arguments> provideArguments(){
        return Stream.of(

                //row
                Arguments.of(generateBoard(new int[]{1, 2, 3}, new int[]{}), 2, Match.PLAYER_SYMBOL, true),
                Arguments.of(generateBoard(new int[]{4, 5, 6}, new int[]{}), 5, Match.PLAYER_SYMBOL, true),
                Arguments.of(generateBoard(new int[]{}, new int[]{4, 5, 6}), 5, Match.COMPUTER_SYMBOL, true),

                //column
                Arguments.of(generateBoard(new int[]{1, 4, 7}, new int[]{}), 1, Match.PLAYER_SYMBOL, true),
                Arguments.of(generateBoard(new int[]{}, new int[]{1, 4, 7}), 1, Match.COMPUTER_SYMBOL, true),

                //diagonals
                Arguments.of(generateBoard(new int[]{1, 5, 9}, new int[]{}), 1, Match.PLAYER_SYMBOL, true),
                Arguments.of(generateBoard(new int[]{3, 5, 7}, new int[]{}), 3, Match.PLAYER_SYMBOL, true),
                Arguments.of(generateBoard(new int[]{}, new int[]{1, 5, 9}), 1, Match.COMPUTER_SYMBOL, true),


                //row
                Arguments.of(generateBoard(new int[]{1, 2}, new int[]{3}), 2, Match.PLAYER_SYMBOL, false),
                Arguments.of(generateBoard(new int[]{1, 2}, new int[]{}), 2, Match.PLAYER_SYMBOL, false),
                Arguments.of(generateBoard(new int[]{}, new int[]{1, 2}), 2, Match.COMPUTER_SYMBOL, false),

                //column
                Arguments.of(generateBoard(new int[]{1, 7}, new int[]{4}), 1, Match.PLAYER_SYMBOL, false),
                Arguments.of(generateBoard(new int[]{4}, new int[]{1, 7}), 1, Match.COMPUTER_SYMBOL, false),

                //empty
                Arguments.of(generateBoard(new int[]{}, new int[]{}), 2, Match.PLAYER_SYMBOL, false),
                Arguments.of(generateBoard(new int[]{}, new int[]{}), 2, Match.COMPUTER_SYMBOL, false),

                //full board with draw
                Arguments.of(generateBoard(new int[]{2,5,6,7}, new int[]{1,3,4,9,8}), 2, Match.PLAYER_SYMBOL, false),
                Arguments.of(generateBoard(new int[]{1,3,4,9,8}, new int[]{2,5,6,7}), 2, Match.COMPUTER_SYMBOL, false)
        );
    }

    @ParameterizedTest
    @MethodSource("provideArguments")
    void test(Board board, int index, char symbol, boolean expected) {
        Position position = new Position(index);
        assertEquals(expected, Winner.thereIsWinner(board, position, symbol ));
    }




}













