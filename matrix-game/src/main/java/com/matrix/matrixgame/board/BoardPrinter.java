package com.matrix.matrixgame.board;

public class BoardPrinter {
    public static void print(Board board) {

        int size = board.getSize();

        String[][] matrix = new String[size][size];

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                matrix[row][col] = ".";
            }
        }

        board.getWalls()
                .forEach(wall -> {
                    Position p = wall.getPosition();
                    matrix[p.row()][p.col()] = "#";
                });

        board.getPhones()
                .forEach(phone -> {
                    Position p = phone.getPosition();
                    matrix[p.row()][p.col()] = "T";
                });

        board.getAgents()
                .forEach(agent -> {
                    Position p = agent.getPosition();
                    matrix[p.row()][p.col()] = "A";
                });
        
        if (board.getNeo() != null) {
            Position neo = board.getNeo().getPosition();

            matrix[neo.row()][neo.col()] = "N";
        }

        System.out.println();

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                System.out.print(matrix[row][col] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
}
