import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class UI extends JPanel {
    private static int CELL_SIZE = 30;
    private static int ROWS = 20;
    private static int COLS = 10;

    private BufferedImage bgImage;
    private BufferedImage playgroundImage;
    private BufferedImage waitImage;
    private BufferedImage pauseImage;
    private BufferedImage gameoverImage;
    private BufferedImage[] cellImages = new BufferedImage[7];
    private BufferedImage[] hardDropImages = new BufferedImage[7];

    private Tetris tetris = new Tetris();

    /**
     * Type Action       Key
     * 0    start        Enter
     * 1    pause        ESC | F1
     * 2    resume       R
     * 3    restart      I
     * 4    moveDown     Down
     * 5    hardDrop     Space
     * 6    moveLeft     Left
     * 7    moveRight    Right
     * 8    cwSpin       X | Up
     * 9    antiCwSpin   C | Ctrl
     * 10   hold         Shift
     */
    private static Map<Integer, Integer> keyActionMap = new HashMap<Integer, Integer>();

    static {
        keyActionMap.put(KeyEvent.VK_ENTER, 0);
        keyActionMap.put(KeyEvent.VK_ESCAPE, 1);
        keyActionMap.put(KeyEvent.VK_F1, 1);
        keyActionMap.put(KeyEvent.VK_R, 2);
        keyActionMap.put(KeyEvent.VK_I, 3);
        keyActionMap.put(KeyEvent.VK_DOWN, 4);
        keyActionMap.put(KeyEvent.VK_SPACE, 5);
        keyActionMap.put(KeyEvent.VK_LEFT, 6);
        keyActionMap.put(KeyEvent.VK_RIGHT, 7);
        keyActionMap.put(KeyEvent.VK_X, 8);
        keyActionMap.put(KeyEvent.VK_UP, 8);
        keyActionMap.put(KeyEvent.VK_C, 9);
        keyActionMap.put(KeyEvent.VK_CONTROL, 9);
        keyActionMap.put(KeyEvent.VK_SHIFT, 10);
        keyActionMap.put(KeyEvent.VK_E, 11);
    }

    private static Map<Integer, Integer> levelSpeedMap = new HashMap<Integer, Integer>();

    static {
        levelSpeedMap.put(6, 35);
        levelSpeedMap.put(5, 29);
        levelSpeedMap.put(4, 23);
        levelSpeedMap.put(3, 17);
        levelSpeedMap.put(2, 11);
        levelSpeedMap.put(1, 6);
        levelSpeedMap.put(0, 2);
    }

    private KeyListener keyListener = new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            int keyCode = e.getKeyCode();
            int flag = keyToAction(keyCode);
            repaint();
        }
    };

    private Timer timer = new Timer();
    private TimerTask timerTask = new TimerTask() {
        int moveIndex = -1;

        @Override
        public void run() {
            int speed = levelSpeedMap.get(tetris.getLevel());
            if (tetris.getState() == 1) {
                if (moveIndex % speed == 0) {
                    tetris.action(4);
                    moveIndex = 0;
                    repaint();
                }
            }
            moveIndex++;
            repaint();
        }
    };

    public UI() {
        initiate();
    }

    public void run() {
        timer.schedule(timerTask, 10, 20);
    }

    private void initiate() {
        imageLoad();
        this.addKeyListener(keyListener);
        this.setFocusable(true);
        this.requestFocus();
    }

    private int keyToAction(int keyCode) {
        int flag = -1;
        if (tetris == null || keyActionMap.get(keyCode) == null)
            return flag;
        flag = tetris.action(keyActionMap.get(keyCode));
        return flag;
    }

    private void imageLoad() {
        try {
            bgImage = ImageIO.read(UI.class.getResource("background.png"));
            waitImage = ImageIO.read(UI.class.getResource("start.png"));
            for (int i = 0; i < cellImages.length; i++) {
                cellImages[i] = ImageIO.read(UI.class.getResource("/cellImages/" + i + ".png"));
            }
            for (int i = 0; i < hardDropImages.length; i++) {
                hardDropImages[i] = ImageIO.read(UI.class.getResource("/hardDropImages/" + i + ".png"));
            }
            playgroundImage = ImageIO.read(UI.class.getResource("playground.png"));
            pauseImage = ImageIO.read(UI.class.getResource("pause.png"));
            gameoverImage = ImageIO.read(UI.class.getResource("gameover.png"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void paint(Graphics g) {
        if (tetris.getState() == 1) {
            paintImages(g);
            paintWall(g);
            paintTetro(g);
            paintHardDrop(g);
            paintHold(g);
            paintScore(g);
            paintNext(g);
        } else if (tetris.getState() == 2) {
            paintImages(g);
            paintWall(g);
            paintTetro(g);
            paintHardDrop(g);
            paintHold(g);
            paintScore(g);
            paintNext(g);
            paintPause(g);
        } else if (tetris.getState() == -1) {
            paintGameover(g);
        } else if (tetris.getState() == 0) {
            paintWait(g);
        }
    }

    private void paintImages(Graphics g) {
        g.drawImage(bgImage, 0, 0, null);
        g.drawImage(playgroundImage, 0, 0, null);
    }

    private void paintWall(Graphics g) {
        int xAdd = 175, yAdd = 25;
        int[][] wall = tetris.getWall();
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (wall[row][col] != -1) {
                    int rows = row * CELL_SIZE + yAdd;
                    int cols = col * CELL_SIZE + xAdd;
                    g.drawImage(cellImages[wall[row][col]], cols, rows, null);
                }
            }
        }
    }

    private void paintTetro(Graphics g) {
        int xAdd = 175, yAdd = 25;
        Tetromino tetro = tetris.getCurrentTetro();
        if (tetro == null) return;
        Cell[] cells = tetro.getCells();
        if (cells == null) return;
        for (Cell cell : cells) {
            int rows = cell.getRow() * CELL_SIZE + yAdd;
            int cols = cell.getCol() * CELL_SIZE + xAdd;
            g.drawImage(cellImages[cell.getCellType()], cols, rows, null);
        }
    }

    private void paintHardDrop(Graphics g) {
        int xAdd = 175, yAdd = 25;
        Tetromino tetro = tetris.getHardDropTetro();
        if (tetro == null) {
            return;
        }
        Cell[] cells = tetro.getCells();
        for (Cell cell : cells) {
            int rows = cell.getRow() * CELL_SIZE + yAdd;
            int cols = cell.getCol() * CELL_SIZE + xAdd;
            g.drawImage(hardDropImages[cell.getCellType()], cols, rows, null);
        }
    }

    private void paintNext(Graphics g) {
        int xAdd = 420;
        int[] yAdd = {75, 175, 265, 355};
        int[] nextTetrosType = tetris.getNextTetrosType();
        for (int i = 0; i < 4; i++) {
            Cell[] cells = (new Tetromino(nextTetrosType[i])).getCells();
            for (Cell cell : cells) {
                int rows = cell.getRow() * CELL_SIZE + yAdd[i];
                int cols = cell.getCol() * CELL_SIZE + xAdd;
                g.drawImage(cellImages[cell.getCellType()], cols, rows, null);
            }
        }
    }

    private void paintHold(Graphics g) {
        int xAdd = 34, yAdd = 75;
        int holdType = tetris.getCurrentHoldType();
        if (holdType == -1)
            return;
        Tetromino tetro = new Tetromino(holdType);
        for (int i = 0; i < 3; i++)
            tetro.moveLeft();
        Cell[] cells = tetro.getCells();
        for (Cell cell : cells) {
            int rows = cell.getRow() * CELL_SIZE + yAdd;
            int cols = cell.getCol() * CELL_SIZE + xAdd;
            g.drawImage(cellImages[cell.getCellType()], cols, rows, null);
        }
    }

    private void paintScore(Graphics g) {
        int score = tetris.getScore(), level = tetris.getLevel(), lines = tetris.getLines(), pieces = tetris.getPieces();
        Color color = new Color(255, 255,255);
        g.setColor(color);
        Font font = new Font(Font.SANS_SERIF, Font.BOLD, 30);
        g.setFont(font);
        g.drawString(String.valueOf(score), 50, 170);
        g.drawString("SCORE", 30, 200);
        g.drawString("LEVEL " + level, 20, 270);
        g.drawString(String.valueOf(pieces), 50, 350);
        g.drawString("PIECES", 25, 400);
        g.drawString(String.valueOf(lines), 50, 450);
        g.drawString("LINES", 30, 500);
    }

    private void paintWait(Graphics g) {
        g.drawImage(waitImage, 0, 0, null);
    }

    private void paintPause(Graphics g) {
        g.drawImage(pauseImage, 0, 0, null);
    }

    private void paintGameover(Graphics g) {
        g.drawImage(gameoverImage, 0, 0, null);
        Color color = new Color(255, 255, 0);
        Font font = new Font(Font.SANS_SERIF, Font.PLAIN,30);
        g.setColor(color);
        g.setFont(font);
        g.drawString(String.valueOf(tetris.getScore()), 340, 342);
    }

    public static void main(String[] args) {
        UI ui = new UI();
        JFrame frame = new JFrame();
        frame.add(ui);
        frame.setSize(650, 670);
        frame.setTitle("Tetris");
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        ui.run();
    }
}
