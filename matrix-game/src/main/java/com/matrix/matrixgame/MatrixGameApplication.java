package com.matrix.matrixgame;

import javax.swing.SwingUtilities;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.matrix.matrixgame.ui.MatrixGameUI;

@SpringBootApplication
public class MatrixGameApplication {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(MatrixGameUI::new);
	}
}
