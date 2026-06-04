package co.eci.snake.ui.legacy;

import co.eci.snake.concurrency.SnakeRunner;
import co.eci.snake.core.Board;
import co.eci.snake.core.Direction;
import co.eci.snake.core.Position;
import co.eci.snake.core.Snake;
import co.eci.snake.core.engine.GameClock;
import co.eci.snake.core.engine.PauseController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

public final class SnakeApp extends JFrame {

  private final Board board;
  private final GamePanel gamePanel;
  private final JButton startButton;
  private final JButton actionButton;
  private final JLabel statsLabel;
  private final GameClock clock;
  private final List<Snake> snakes = new java.util.ArrayList<>();

  private final PauseController pauseController = new PauseController();

  private boolean started = false;
  private boolean paused = true;

  public SnakeApp() {
    super("The Snake Race");
    this.board = new Board(35, 28);

    int N = Integer.getInteger("snakes", 2);
    for (int i = 0; i < N; i++) {
      int x = 2 + (i * 3) % board.width();
      int y = 2 + (i * 2) % board.height();
      var dir = Direction.values()[i % Direction.values().length];
      snakes.add(Snake.of(x, y, dir, i + 1));
    }
    this.board.registerSnakes(snakes);

    this.pauseController.pause();

    this.gamePanel = new GamePanel(board, () -> snakes);
    this.startButton = new JButton("Start");
    this.actionButton = new JButton("Action");
    this.actionButton.setEnabled(false);
    this.statsLabel = new JLabel("Press Start to begin the game");
    this.statsLabel.setHorizontalAlignment(SwingConstants.CENTER);

    setLayout(new BorderLayout());
    add(gamePanel, BorderLayout.CENTER);

    JPanel controls = new JPanel(new BorderLayout());
    JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 6));
    buttonRow.add(startButton);
    buttonRow.add(actionButton);
    controls.add(buttonRow, BorderLayout.NORTH);
    controls.add(statsLabel, BorderLayout.SOUTH);
    add(controls, BorderLayout.SOUTH);

    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    pack();
    setResizable(false);
    setLocationRelativeTo(null);

    this.clock = new GameClock(60, () -> SwingUtilities.invokeLater(gamePanel::repaint));

    var exec = Executors.newVirtualThreadPerTaskExecutor();
    
    snakes.forEach(s -> exec.submit(new SnakeRunner(s, board, pauseController)));

    Runtime.getRuntime().addShutdownHook(new Thread(() -> exec.shutdownNow()));

    startButton.addActionListener((ActionEvent e) -> startGame());

    actionButton.addActionListener((ActionEvent e) -> togglePause());

    gamePanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "pause");
    gamePanel.getActionMap().put("pause", new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        togglePause();
      }
    });

    var player = snakes.get(0);
    InputMap im = gamePanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
    ActionMap am = gamePanel.getActionMap();
    im.put(KeyStroke.getKeyStroke("LEFT"), "left");
    im.put(KeyStroke.getKeyStroke("RIGHT"), "right");
    im.put(KeyStroke.getKeyStroke("UP"), "up");
    im.put(KeyStroke.getKeyStroke("DOWN"), "down");
    am.put("left", new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        player.turn(Direction.LEFT);
      }
    });
    am.put("right", new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        player.turn(Direction.RIGHT);
      }
    });
    am.put("up", new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        player.turn(Direction.UP);
      }
    });
    am.put("down", new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        player.turn(Direction.DOWN);
      }
    });

    if (snakes.size() > 1) {
      var p2 = snakes.get(1);
      im.put(KeyStroke.getKeyStroke('A'), "p2-left");
      im.put(KeyStroke.getKeyStroke('D'), "p2-right");
      im.put(KeyStroke.getKeyStroke('W'), "p2-up");
      im.put(KeyStroke.getKeyStroke('S'), "p2-down");
      am.put("p2-left", new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
          p2.turn(Direction.LEFT);
        }
      });
      am.put("p2-right", new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
          p2.turn(Direction.RIGHT);
        }
      });
      am.put("p2-up", new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
          p2.turn(Direction.UP);
        }
      });
      am.put("p2-down", new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
          p2.turn(Direction.DOWN);
        }
      });
    }

    setVisible(true);
  }

  private void startGame() {
    if (started) {
      return;
    }
    started = true;
    paused = false;
    startButton.setVisible(false);
    actionButton.setEnabled(true);
    actionButton.setText("Pause");
    statsLabel.setText("Running");
    pauseController.resume();
    clock.start();
    revalidate();
    repaint();
  }

  private void togglePause() {
    if (!started) {
      return;
    }
    if (!paused) {
      paused = true;
      actionButton.setText("Resume");
      clock.pause();
      pauseController.pause();
      statsLabel.setText("Waiting for a stable pause state...");
    } else {
      paused = false;
      statsLabel.setText("Running");
      actionButton.setText("Pause");
      pauseController.resume();
      clock.resume();
    }
  }

  public static final class GamePanel extends JPanel {
    private final transient Board board;
    private final transient Supplier snakesSupplier;
    private static final int CELL = 20;

    @FunctionalInterface
    public interface Supplier {
      List<Snake> get();
    }

    public GamePanel(Board board, Supplier snakesSupplier) {
      this.board = board;
      this.snakesSupplier = snakesSupplier;
      setPreferredSize(new Dimension(board.width() * CELL + 1, board.height() * CELL + 40));
      setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      var g2 = (Graphics2D) g.create();
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

      g2.setColor(new Color(220, 220, 220));
      for (int x = 0; x <= board.width(); x++)
        g2.drawLine(x * CELL, 0, x * CELL, board.height() * CELL);
      for (int y = 0; y <= board.height(); y++)
        g2.drawLine(0, y * CELL, board.width() * CELL, y * CELL);

      // Obstáculos
      g2.setColor(new Color(255, 102, 0));
      for (var p : board.obstacles()) {
        int x = p.x() * CELL;
        int y = p.y() * CELL;
        g2.fillRect(x + 2, y + 2, CELL - 4, CELL - 4);
        g2.setColor(Color.RED);
        g2.drawLine(x + 4, y + 4, x + CELL - 6, y + 4);
        g2.drawLine(x + 4, y + 8, x + CELL - 6, y + 8);
        g2.drawLine(x + 4, y + 12, x + CELL - 6, y + 12);
        g2.setColor(new Color(255, 102, 0));
      }

      // Ratones
      g2.setColor(Color.BLACK);
      for (var p : board.mice()) {
        int x = p.x() * CELL;
        int y = p.y() * CELL;
        g2.fillOval(x + 4, y + 4, CELL - 8, CELL - 8);
        g2.setColor(Color.WHITE);
        g2.fillOval(x + 8, y + 8, CELL - 16, CELL - 16);
        g2.setColor(Color.BLACK);
      }

      // Teleports (flechas rojas)
      Map<Position, Position> tp = board.teleports();
      g2.setColor(Color.RED);
      for (var entry : tp.entrySet()) {
        Position from = entry.getKey();
        int x = from.x() * CELL;
        int y = from.y() * CELL;
        int[] xs = { x + 4, x + CELL - 4, x + CELL - 10, x + CELL - 10, x + 4 };
        int[] ys = { y + CELL / 2, y + CELL / 2, y + 4, y + CELL - 4, y + CELL / 2 };
        g2.fillPolygon(xs, ys, xs.length);
      }

      // Turbo (rayos)
      g2.setColor(Color.BLACK);
      for (var p : board.turbo()) {
        int x = p.x() * CELL;
        int y = p.y() * CELL;
        int[] xs = { x + 8, x + 12, x + 10, x + 14, x + 6, x + 10 };
        int[] ys = { y + 2, y + 2, y + 8, y + 8, y + 16, y + 10 };
        g2.fillPolygon(xs, ys, xs.length);
      }

      // Serpientes
      var snakes = snakesSupplier.get();
      int idx = 0;
      for (Snake s : snakes) {
        var body = s.snapshot().toArray(new Position[0]);
        for (int i = 0; i < body.length; i++) {
          var p = body[i];
          Color base = (idx == 0) ? new Color(0, 170, 0) : new Color(0, 160, 180);
          int shade = Math.max(0, 40 - i * 4);
          g2.setColor(new Color(
              Math.min(255, base.getRed() + shade),
              Math.min(255, base.getGreen() + shade),
              Math.min(255, base.getBlue() + shade)));
          g2.fillRect(p.x() * CELL + 2, p.y() * CELL + 2, CELL - 4, CELL - 4);
        }

        var head = s.head();
        if (head != null) {
          int x = head.x() * CELL;
          int y = head.y() * CELL;
          g2.setColor(Color.BLACK);
          g2.setFont(g2.getFont().deriveFont(Font.BOLD, 12f));
          String label = String.valueOf(s.deathOrder());
          FontMetrics fm = g2.getFontMetrics();
          int textX = x + (CELL - fm.stringWidth(label)) / 2;
          int textY = y + (CELL - fm.getHeight()) / 2 + fm.getAscent();
          g2.drawString(label, textX, textY);
        }
        idx++;
      }
      g2.dispose();
    }
  }

  public static void launch() {
    try {
      SwingUtilities.invokeAndWait(SnakeApp::new);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException("Unable to start SnakeApp", e);
    } catch (java.lang.reflect.InvocationTargetException e) {
      throw new IllegalStateException("Unable to start SnakeApp", e.getCause());
    }
  }
}
