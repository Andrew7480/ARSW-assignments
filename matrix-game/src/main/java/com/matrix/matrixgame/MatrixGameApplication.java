package com.matrix.matrixgame;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.matrix.matrixgame.engine.GameConfig;
import com.matrix.matrixgame.engine.GameEngine;

@SpringBootApplication
public class MatrixGameApplication {

	public static void main(String[] args) {
		GameConfig config = new GameConfig(
				10, // tamaño tablero
				15, // muros
				3, // agentes
				2 // teléfonos
		);

		GameEngine engine = new GameEngine(config);

		engine.initializeGame();

		engine.startGame();
	}
}
