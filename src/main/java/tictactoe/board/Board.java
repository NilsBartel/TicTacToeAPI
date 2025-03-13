package tictactoe.board;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import tictactoe.game.Match;

public class Board {

    public static final List<Position> DIAGONAL_TOP_RIGHT_BOTTOM_LEFT =
        new ArrayList<>(List.of(new Position(3), new Position(5), new Position(7)));
    public static final List<Position> DIAGONAL_TOP_LEFT_BOTTOM_RIGHT =
        new ArrayList<>(List.of(new Position(1), new Position(5), new Position(9)));

    List<Row> rows = new ArrayList<>();

    public Board(List<Character> symbols) {
        int counter = 0;
        for(int row = 0; row<3; row++){
            List<Character> rowSymbols = new ArrayList<>();
            for(int i = 0; i<3; i++){
                rowSymbols.add(symbols.get(i+row+counter));
            }
            counter+=2;
            rows.add(new Row(rowSymbols));
        }
    }

    public Board(Row... rows) {
        this.rows = Arrays.asList(rows);
    }

    public Board() {
        for (int row = 0; row < 3; row++) {
            rows.add(new Row());
        }
    }

    public void print() {
        for (Row row : rows) {
            row.print();
        }
    }

    public boolean isValid(int index) {
        if (index < 1 || index > 9) {
            return false;
        }

        Position position = new Position(index);
        return rows.get(position.getRow()).getSymbol(position.getColumn()) == ' ';
    }

    @JsonIgnore
    public boolean isEmpty() {
        for (Row row : rows) {
            for (Field field : row.getFields()) {
                if (field.getSymbol() != Match.EMPTY_SYMBOL) {
                    return false;
                }
            }
        }
        return true;
    }

    @JsonIgnore
    public boolean isFull() {
        for (Row row : rows) {
            for (Field field : row.getFields()) {
                if (field.getSymbol() == Match.EMPTY_SYMBOL) {
                    return false;
                }
            }
        }
        return true;
    }

    public void setSymbol(int row, int column, char symbol) {
        rows.get(row).setSymbol(column, symbol);
    }

    public char getSymbol(int row, int column) {
        return rows.get(row).getSymbol(column);
    }

    public char getSymbol(Position position) {
        return rows.get(position.getRow()).getSymbol(position.getColumn());
    }

    public List<Row> getRows() {
        return rows;
    }

    public void setRows(List<Row> rows) {
        this.rows = rows;
    }

    public List<Field> getFieldsInRow(Row row) {
        return row.getFields();
    }

    public Field getField(int row, int column) {
        return rows.get(row).getField(column);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        return !(object instanceof Board board) || rows.equals(board.rows);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(rows);
    }
}
