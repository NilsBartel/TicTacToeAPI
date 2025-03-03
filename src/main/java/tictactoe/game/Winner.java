package tictactoe.game;

import java.util.ArrayList;
import java.util.List;

import tictactoe.board.Board;
import tictactoe.board.Field;
import tictactoe.board.Position;
import tictactoe.board.Row;

public final class Winner {
    private Winner() {
    }

    public static boolean thereIsWinner(Board board, char currentSymbol) {

        for (Row row : board.getRows()) {
            if (allEqualsSymbol(board.getFieldsInRow(row), currentSymbol)) {
                return true;
            }
        }

        for (int column = 0; column < 3; column++) {
            List<Field> columnList = new ArrayList<>();
            for (int row = 0; row < 3; row++) {
                columnList.add(board.getField(row, column));
            }
            if (allEqualsSymbol(columnList, currentSymbol)) {
                return true;
            }
        }

        return checkDiagonalTopLeftBottomRight(board, currentSymbol) || checkDiagonalTopRightBottomLeft(
            board,
            currentSymbol
        );
    }

    private static boolean checkDiagonalTopLeftBottomRight(Board board, char currentSymbol) {
        List<Field> diagonalLeftRight = new ArrayList<>();
        for (Position pos : Board.DIAGONAL_TOP_LEFT_BOTTOM_RIGHT) {
            diagonalLeftRight.add(board.getField(pos.getRow(), pos.getColumn()));
        }
        return allEqualsSymbol(diagonalLeftRight, currentSymbol);
    }

    private static boolean checkDiagonalTopRightBottomLeft(Board board, char currentSymbol) {
        List<Field> diagonalRightLeft = new ArrayList<>();
        for (Position pos : Board.DIAGONAL_TOP_RIGHT_BOTTOM_LEFT) {
            diagonalRightLeft.add(board.getField(pos.getRow(), pos.getColumn()));
        }
        return allEqualsSymbol(diagonalRightLeft, currentSymbol);
    }

    private static boolean allEqualsSymbol(List<Field> fields, char currentSymbol) {

        for (Field field : fields) {
            if (field.getSymbol() != currentSymbol) {
                return false;
            }
        }
        return true;
    }

    public static List<Position> findWinningRow(Board board, char winningSymbol) {

        List<Position> positions = new ArrayList<>();

        //Row
        List<Field> list = new ArrayList<>();
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                list.add(board.getField(row, column));
                positions.add(new Position(row, column));
            }
            if (allEqualsSymbol(list, winningSymbol)) {
                return positions;
            } else {
                list.clear();
            }
            positions.clear();
        }

        //Column
        for (int column = 0; column < 3; column++) {
            for (int row = 0; row < 3; row++) {
                list.add(board.getField(row, column));
                positions.add(new Position(row, column));
            }
            if (allEqualsSymbol(list, winningSymbol)) {
                return positions;
            } else {
                list.clear();
            }
            positions.clear();
        }

        if (checkDiagonalTopLeftBottomRight(board, winningSymbol)) {
            return Board.DIAGONAL_TOP_LEFT_BOTTOM_RIGHT;
        }

        if (checkDiagonalTopRightBottomLeft(board, winningSymbol)) {
            return Board.DIAGONAL_TOP_RIGHT_BOTTOM_LEFT;
        }

        return positions;
    }

}
