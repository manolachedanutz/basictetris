import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Tetris extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final int BLOCK_SIZE = 30;
    private static final int BOARD_WIDTH = 10;
    private static final int BOARD_HEIGHT = 20;
    private static final int FPS = 60;
    private static final int MOVE_INTERVAL = FPS / 2;

    private int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
    private Piece currentPiece;
    private Random random = new Random();
    private int moveCounter = 0;
    private boolean isGameOver = false;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Tetris");
        Tetris tetris = new Tetris();
        frame.getContentPane().add(tetris);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        tetris.run();
    }

    public Tetris() {
        setPreferredSize(new java.awt.Dimension(BLOCK_SIZE * BOARD_WIDTH, BLOCK_SIZE * BOARD_HEIGHT));
        setFocusable(true);
        addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_LEFT) {
                    if (currentPiece.canMoveLeft(board)) {
                        currentPiece.moveLeft();
                    }
                } else if (evt.getKeyCode() == KeyEvent.VK_RIGHT) {
                    if (currentPiece.canMoveRight(board)) {
                        currentPiece.moveRight();
                    }
                } else if (evt.getKeyCode() == KeyEvent.VK_DOWN) {
                    if (currentPiece.canMoveDown(board)) {
                        currentPiece.moveDown();
                        moveCounter = 0;
                    }
                } else if (evt.getKeyCode() == KeyEvent.VK_UP) {
                    currentPiece.rotate();
                }
            }
        });
        newPiece();
    }

    private void run() {
        while (!isGameOver) {
            try {
                Thread.sleep(1000 / FPS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            moveCounter++;
            if (moveCounter == MOVE_INTERVAL) {
                if (currentPiece.canMoveDown(board)) {
                    currentPiece.moveDown();
                } else {
                    addPieceToBoard();
                    removeCompletedRows();
                    newPiece();
                }
                moveCounter = 0;
            }
            repaint();
        }
    }

    private void newPiece() {
        currentPiece = new Piece(random.nextInt(7) + 1);
        if (!currentPiece.canMoveDown(board)) {
            isGameOver = true;
        }
    }

    private void addPieceToBoard() {
        int[][] shape = currentPiece.getShape();
        int color = currentPiece.getColor();
        int x = currentPiece.getX();
        int y = currentPiece.getY();
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                if (shape[row][col] != 0) {
                    board[y + row][x + col] = color;
                }
            }
        }
    }

    private void removeCompletedRows() {
        for (int row = BOARD_HEIGHT - 1; row >= 0; row--) {
            boolean isRowComplete = true;
            for (int col = 0; col < BOARD_WIDTH; col++) {
                if (board[row][col] == 0) {
                    isRowComplete = false;
                    break;
                }
            }
            if (isRowComplete) {
                removeRow(row);
                row++;
            }
        }
    }

    private void removeRow(int row) {
        for (int i = row; i > 0; i--) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                board[i][j] = board[i - 1][j];
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBoard(g);
        drawPiece(g);
        if (isGameOver) {
            g.setColor(Color.RED);
            g.drawString("GAME OVER", BLOCK_SIZE * BOARD_WIDTH / 2 - 30, BLOCK_SIZE * BOARD_HEIGHT / 2);
        }
    }

    private void drawBoard(Graphics g) {
        for (int row = 0; row < BOARD_HEIGHT; row++) {
            for (int col = 0; col < BOARD_WIDTH; col++) {
                if (board[row][col] != 0) {
                    drawBlock(g, col, row, board[row][col]);
                }
            }
        }
    }

    private void drawPiece(Graphics g) {
        int[][] shape = currentPiece.getShape();
        int color = currentPiece.getColor();
        int x = currentPiece.getX();
        int y = currentPiece.getY();
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                if (shape[row][col] != 0) {
                    drawBlock(g, x + col, y + row, color);
                }
            }
        }
    }

    private void drawBlock(Graphics g, int x, int y, int color) {
        g.setColor(getColorForBlock(color));
        g.fillRect(x * BLOCK_SIZE, y * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
        g.setColor(Color.BLACK);
        g.drawRect(x * BLOCK_SIZE, y * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
    }

    private Color getColorForBlock(int color) {
        switch (color) {
        case 1:
            return Color.CYAN;
        case 2:
            return Color.BLUE;
        case 3:
            return Color.ORANGE;
        case 4:
            return Color.YELLOW;
        case 5:
            return Color.GREEN;
        case 6:
            return Color.PINK;
        case 7:
            return Color.RED;
        default:
            return Color.BLACK;
        }
    }

}
