import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PlayerInputTest {



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
    @Test
    void testAskForMove() {
        Board board = generateBoard(new int[]{1, 2}, new int[]{4,5});
        Position pos = new Position(9);

        PlayerInput playerInput = mock(PlayerInput.class);
        when(playerInput.askForMove(board)).thenReturn(new Position(9));

        assertEquals(pos, playerInput.askForMove(board));
    }


    @Test
    void testAskPlayAgainTrue() {
        PlayerInput playerInput = mock(PlayerInput.class);
        int userID = 1;
        when(playerInput.askPlayAgain(userID)).thenReturn(true);

        assertTrue(playerInput.askPlayAgain(userID));
    }

    @Test
    void testAskPlayAgainFalse() {
        PlayerInput playerInput = mock(PlayerInput.class);
        int userID = 1;
        when(playerInput.askPlayAgain(userID)).thenReturn(false);

        assertFalse(playerInput.askPlayAgain(userID));
    }



    private static Stream<Arguments> generateInteger() {
        return Stream.of(
                // tests int
                Arguments.of("6", true),
                Arguments.of("1", true),
                Arguments.of("1998", true),

                // tests null
                Arguments.of(null, false),

                //tests empty
                Arguments.of("", false),
                Arguments.of(" ", false),

                //tests words/letters
                Arguments.of("hallo", false),
                Arguments.of("r", false),
                Arguments.of("4 hallo", false),

                //tests symbols
                Arguments.of("*", false),
                Arguments.of("!", false),
                Arguments.of("*#*?)", false),

                //tests double
                Arguments.of("2.78", false),

                //tests negative int
                Arguments.of("-30", true),

                //long
                Arguments.of(" 4147483647", false)
        );
    }
    @ParameterizedTest
    @MethodSource("generateInteger")
    void testIsInteger (String input, boolean expected) {

        assertEquals(expected, PlayerInput.getInstance().isInteger(input));
    }
}