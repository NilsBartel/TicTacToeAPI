package tictactoe.board;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Objects;

public class Position {
    private final int index;
    @JsonIgnore
    private final int row;
    @JsonIgnore
    private final int column;


    public Position(int index){
        this.index = index;
        this.row = (index -1) / 3;
        this.column = (index -1) % 3 ;
    }

    public Position(int row, int column){
        this.index = row *3 + column +1;
        this.row = row;
        this.column = column;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        return !(object instanceof Position position) || index == position.index;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(index);
    }
    public int getIndex() {
        return index;
    }
    public int getRow() {
        return row;
    }
    public int getColumn() {
        return column;
    }
}
