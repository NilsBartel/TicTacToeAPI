import org.junit.jupiter.api.Test;


class BoardTest {

    @Test
    void isValidCheckNumber() {
        Board board = new Board();
        int number = 5;

        assertTrue(board.isValid(number));
    }

    @Test
    void isValidCheckNumberTooBig() {
        Board board = new Board();
        int number = 20;
        assertFalse(board.isValid(number));
    }

    @Test
    void isValidCheckNegativeNumber() {
        Board board = new Board();
        int number = -2;
        assertFalse(board.isValid(number));
    }

    @Test
    void alreadyUsedField(){
        Board board = new Board();
        board.getRows().get(2).getFields().get(1).setSymbol(Match.COMPUTER_SYMBOL);

        assertFalse(board.isValid(8));
    }

    @Test
    void emptyField(){
        Board board = new Board();

        assertTrue(board.isValid(2));
    }




    @Test
    void isEmpty() {
        Board board = new Board();

        assertTrue(board.isEmpty());
    }

    @Test
    void isNotEmpty() {
        Board board = new Board();
        board.getRows().get(1).getFields().get(1).setSymbol(Match.COMPUTER_SYMBOL);

        assertFalse(board.isEmpty());
    }


}