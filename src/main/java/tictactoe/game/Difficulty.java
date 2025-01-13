package tictactoe.game;

import tictactoe.board.*;
import tictactoe.board.Board;


public final class Difficulty {
    public static final int MEDIUM_DIFFICULTY_PERCENTAGE = 40;
    private Difficulty() {
    }

    public static Position returnMove(Board board, DifficultyState difficulty) {

        switch (difficulty){
            case EASY -> {
                return ComputerMoveService.easyComputerMove(board);
            }
            case MEDIUM -> {
                return ComputerMoveService.mediumComputerMove(board, MEDIUM_DIFFICULTY_PERCENTAGE);
            }
            case IMPOSSIBLE -> {
                return ComputerMoveService.impossibleComputerMove(board);
            }
            default -> {throw new IllegalStateException("Unexpected value: " + MEDIUM_DIFFICULTY_PERCENTAGE);}
        }
    }
}
