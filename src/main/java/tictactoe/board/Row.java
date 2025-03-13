package tictactoe.board;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Row {

    List<Field> fields = new ArrayList<>();

    public Row(List<Character> symbols) {
        for(int column = 0; column<3; column++){
            fields.add(new Field(symbols.get(column)));
        }
    }

    public Row() {
        for (int column = 0; column < 3; column++) {
            fields.add(new Field());
        }
    }

    public void print() {
        List<String> symbols = new ArrayList<>();
        for (Field field : fields) {
            symbols.add(String.valueOf(field.getSymbol()));
        }

        String line = String.join(" | ", symbols);
        System.out.println(line);
    }

    public List<Field> getFields() {
        return fields;
    }

    public Field getField(int column) {
        return fields.get(column);
    }

    public void setSymbol(int column, char symbol) {
        fields.get(column).setSymbol(symbol);
    }

    public char getSymbol(int column) {
        return fields.get(column).getSymbol();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        return !(object instanceof Row row) || fields.equals(row.fields);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(fields);
    }
}
