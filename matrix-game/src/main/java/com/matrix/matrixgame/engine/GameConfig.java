package com.matrix.matrixgame.engine;

public record GameConfig(

        int boardSize,

        int wallCount,

        int agentCount,

        int phoneCount

) {
}