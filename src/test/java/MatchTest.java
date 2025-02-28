import org.junit.jupiter.api.Test;

class MatchTest {

    @Test
    void getOpponentsSymbolTest() {
        char computerOpposite = Match.getOpponentsSymbol(Match.COMPUTER_SYMBOL);
        char playerOpposite = Match.getOpponentsSymbol(Match.PLAYER_SYMBOL);

        assertEquals(Match.PLAYER_SYMBOL, computerOpposite);
        assertEquals(Match.COMPUTER_SYMBOL, playerOpposite);
    }
}
