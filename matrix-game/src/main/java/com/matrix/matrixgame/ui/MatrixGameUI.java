package com.matrix.matrixgame.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import com.matrix.matrixgame.board.Board;
import com.matrix.matrixgame.engine.BoardObserver;
import com.matrix.matrixgame.engine.GameConfig;
import com.matrix.matrixgame.engine.GameEngine;
import com.matrix.matrixgame.engine.GameState;
import com.matrix.matrixgame.engine.RoundGate;

public class MatrixGameUI extends JFrame implements BoardObserver {

    private static final Color COLOR_BG       = new Color(5, 5, 5);
    private static final Color COLOR_GRID     = new Color(0, 45, 0);
    private static final Color COLOR_NEO_BG   = new Color(0, 70, 0);
    private static final Color COLOR_NEO_FG   = new Color(0, 255, 70);
    private static final Color COLOR_AGENT_BG = new Color(100, 0, 0);
    private static final Color COLOR_AGENT_FG = new Color(255, 50, 50);
    private static final Color COLOR_PHONE_BG = new Color(0, 50, 80);
    private static final Color COLOR_PHONE_FG = new Color(0, 210, 255);
    private static final Color COLOR_WALL_BG  = new Color(50, 50, 50);
    private static final Color COLOR_WALL_FG  = new Color(130, 130, 130);
    private static final Color COLOR_CTRL_BG  = new Color(10, 10, 10);
    private static final Color COLOR_BTN_BG   = new Color(0, 90, 0);
    private static final Color COLOR_STATUS   = new Color(0, 200, 60);
    private static final String FONT_MONO    = "Monospaced";

    private final GamePanel gamePanel;
    private final JButton nextButton;
    private final JLabel statusLabel;
    private final transient RoundGate gate;
    private int round = 0;

    public MatrixGameUI() {
        super("Matrix Game");

        GameConfig config = new GameConfig(12, 15, 10, 2);
        GameEngine engine = new GameEngine(config);
        engine.initializeGame();

        this.gate = new RoundGate();
        engine.addObserver(this);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBackground(COLOR_BG);
        setLayout(new BorderLayout());

        gamePanel = new GamePanel(engine.getBoard());
        add(gamePanel, BorderLayout.CENTER);

        JPanel controls = new JPanel(new BorderLayout(0, 4));
        controls.setBackground(COLOR_CTRL_BG);
        controls.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        nextButton = new JButton("Next Round  [ENTER]");
        nextButton.setBackground(COLOR_BTN_BG);
        nextButton.setForeground(COLOR_NEO_FG);
        nextButton.setFont(new Font(FONT_MONO, Font.BOLD, 13));
        nextButton.setFocusPainted(false);
        nextButton.setBorderPainted(false);
        nextButton.setOpaque(true);
        nextButton.addActionListener(e -> advance());

        statusLabel = new JLabel("Estado inicial — presiona Next Round para la Ronda 1", SwingConstants.CENTER);
        statusLabel.setForeground(COLOR_STATUS);
        statusLabel.setFont(new Font(FONT_MONO, Font.PLAIN, 12));
        statusLabel.setBackground(COLOR_CTRL_BG);
        statusLabel.setOpaque(true);

        controls.add(nextButton, BorderLayout.CENTER);
        controls.add(statusLabel, BorderLayout.SOUTH);
        add(controls, BorderLayout.SOUTH);

        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "nextRound");
        getRootPane().getActionMap().put("nextRound", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                advance();
            }
        });

        pack();
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);

        new Thread(() -> engine.startGame(gate), "game-engine").start();
    }

    private void advance() {
        if (!nextButton.isEnabled()) return;
        gate.releaseNext();
    }

    @Override
    public void onRoundEnd(Board board, GameState state) {
        round++;
        SwingUtilities.invokeLater(() -> {
            gamePanel.setBoard(board);
            gamePanel.repaint();
            switch (state) {
                case NEO_WON -> {
                    statusLabel.setText("¡Neo escapó! Juego terminado.");
                    statusLabel.setForeground(COLOR_PHONE_FG);
                    nextButton.setEnabled(false);
                }
                case AGENTS_WON -> {
                    statusLabel.setText("Los agentes capturaron a Neo. Juego terminado.");
                    statusLabel.setForeground(COLOR_AGENT_FG);
                    nextButton.setEnabled(false);
                }
                default -> statusLabel.setText("Ronda " + round + " — presiona Next Round o ENTER");
            }
        });
    }

    static class GamePanel extends JPanel {

        private static final int CELL = 50;

        private transient Board board;

        GamePanel(Board board) {
            this.board = board;
            int size = board.getSize();
            setPreferredSize(new Dimension(size * CELL + 1, size * CELL + 1));
            setBackground(COLOR_BG);
        }

        void setBoard(Board board) {
            this.board = board;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Board b = board;
            int size = b.getSize();

            // Grid
            g2.setColor(COLOR_GRID);
            for (int i = 0; i <= size; i++) {
                g2.drawLine(i * CELL, 0, i * CELL, size * CELL);
                g2.drawLine(0, i * CELL, size * CELL, i * CELL);
            }

            for (var wall : b.getWalls()) {
                var p = wall.getPosition();
                fillCell(g2, p.col(), p.row(), COLOR_WALL_BG);
                drawLabel(g2, "#", p.col(), p.row(), COLOR_WALL_FG, 16);
            }

            for (var phone : b.getPhones()) {
                var p = phone.getPosition();
                fillCell(g2, p.col(), p.row(), COLOR_PHONE_BG);
                drawLabel(g2, "T", p.col(), p.row(), COLOR_PHONE_FG, 22);
            }

            for (var agent : b.getAgents()) {
                var p = agent.getPosition();
                fillCell(g2, p.col(), p.row(), COLOR_AGENT_BG);
                drawLabel(g2, "A", p.col(), p.row(), COLOR_AGENT_FG, 22);
            }

            if (b.getNeo() != null) {
                var p = b.getNeo().getPosition();
                fillCell(g2, p.col(), p.row(), COLOR_NEO_BG);
                drawLabel(g2, "N", p.col(), p.row(), COLOR_NEO_FG, 24);
            }

            g2.dispose();
        }

        private void fillCell(Graphics2D g2, int col, int row, Color color) {
            g2.setColor(color);
            g2.fillRect(col * CELL + 1, row * CELL + 1, CELL - 1, CELL - 1);
        }

        private void drawLabel(Graphics2D g2, String text, int col, int row, Color color, int fontSize) {
            g2.setColor(color);
            g2.setFont(new Font(FONT_MONO, Font.BOLD, fontSize));
            FontMetrics fm = g2.getFontMetrics();
            int x = col * CELL + (CELL - fm.stringWidth(text)) / 2;
            int y = row * CELL + (CELL - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(text, x, y);
        }
    }
}
