package tictactoe.board;

import tictactoe.game.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Board {

    public static final List<Position> DIAGONAL_TOP_RIGHT_BOTTOM_LEFT = new ArrayList<>(List.of(new Position(3), new Position(5), new Position(7)));
    public static final List<Position> DIAGONAL_TOP_LEFT_BOTTOM_RIGHT = new ArrayList<>(List.of(new Position(1), new Position(5), new Position(9)));

    List<Row> rows = new ArrayList<>();

    public Board(Row... rows) {
        this.rows = Arrays.asList(rows);
    }

    public Board(){
        for(int row = 0; row<3; row++){
            rows.add(new Row());
        }
    }

    public void print(){
        for(Row row : rows){
            row.print();
        }
    }

    @JsonIgnore
    public String getBoardAsString(){
        String boardString = "";
        for(Row row : rows){
            //row.print();
            //row.getRowsAsString();
            boardString = boardString.concat(row.getRowsAsString() + "\n");
        }

        return boardString;
    }

    public boolean isValid(int index){
        if(index < 1 || index > 9) {
            return false;
        }

        Position position = new Position(index);
        return rows.get(position.getRow()).getSymbol(position.getColumn()) == ' ';
    }

    @JsonIgnore
    public boolean isEmpty(){
        for(Row row : rows){
            for(Field field : row.getFields()){
                if(field.getSymbol() != Match.EMPTY_SYMBOL) {
                    return false;
                }
            }
        }
        return true;
    }

    @JsonIgnore
    public boolean isFull(){
        for(Row row : rows){
            for(Field field : row.getFields()){
                if(field.getSymbol() == Match.EMPTY_SYMBOL) {
                    return false;
                }
            }
        }
        return true;
    }

    public void setSymbol(int row, int column, char symbol){
        rows.get(row).setSymbol(column, symbol);
    }

    public char getSymbol(int row, int column){
        return rows.get(row).getSymbol(column);
    }

    public char getSymbol(Position position){
        return rows.get(position.getRow()).getSymbol(position.getColumn());
    }

    public List<Row> getRows() {
        return rows;
    }

//    public List<Field> getFieldsInRow(int row){
//        return rows.get(row).getFields();
//    }
    public List<Field> getFieldsInRow(Row row){
        return row.getFields();
    }

    public Field getField(int row , int column){
        return rows.get(row).getField(column);
    }

    public void setRows(List<Row> rows) {
        this.rows = rows;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object){
            return true;
        }
        return !(object instanceof Board board) || rows.equals(board.rows);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(rows);
    }
}
