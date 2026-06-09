package com.matrix.matrixgame.engine;

public record GameConfig(

                int boardSize,

                int wallCount,

                int agentCount,

                int phoneCount

) {
        public GameConfig {

                if (boardSize < 5) {
                        throw new IllegalArgumentException("Board size must be at least 5");
                }
                if (boardSize <= 0) {
                        throw new IllegalArgumentException("Board size must be greater than 0");
                }
                if (wallCount < 0) {
                        throw new IllegalArgumentException("Wall count cannot be negative");
                }
                if (agentCount < 0) {
                        throw new IllegalArgumentException("Agent count cannot be negative");
                }
                if (phoneCount < 0) {
                        throw new IllegalArgumentException("Phone count cannot be negative");
                }

                int totalEntities = wallCount + agentCount + phoneCount + 1;
                int totalCells = boardSize * boardSize;

                double wallRatio = (double) wallCount / totalCells;

                double agentRatio = (double) agentCount / totalCells;

                if (wallRatio > 0.40) {
                        throw new IllegalArgumentException("Too many walls for the board size");
                }

                if (agentRatio > 0.20) {
                        throw new IllegalArgumentException("Too many agents for the board size");
                }

                if (totalEntities > totalCells) {
                        throw new IllegalArgumentException("Total entities exceed board capacity");
                }
        }
}