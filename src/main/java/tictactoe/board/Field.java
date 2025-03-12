package tictactoe.board;

import java.util.Objects;

public class Field {
    private char symbol;

    public Field(char symbol) {
        this.symbol = symbol;
    }

    public Field() {
        this.symbol = ' ';
    }

    public char getSymbol() {
        return symbol;
    }

    public void setSymbol(char symbol) {
        this.symbol = symbol;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        return !(object instanceof Field field) || symbol == field.symbol;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(symbol);
    }
}
