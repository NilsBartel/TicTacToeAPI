import org.junit.jupiter.api.Test;

class PositionTest {


    @Test
    void positionIndex() {
        Position position = new Position(2, 0);

        assertEquals(7, position.getIndex());
    }


    @Test
    void positionRowColumn() {
        Position position = new Position(5);

        assertEquals(1, position.getRow());
        assertEquals(1, position.getColumn());
    }


}