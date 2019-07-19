package View;

import Model.Cell;
import Model.Tetris;
import Model.Tetromino;

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
    private static int CELL_SIZE = 30; // cell图片的像素大小
    private static int ROWS = 20; // 底部方块数组的大小
    private static int COLS = 10;

    // 界面的图片资源缓存
    private BufferedImage bgImage;
    private BufferedImage playgroundImage;
    private BufferedImage waitImage;
    private BufferedImage pauseImage;
    private BufferedImage gameoverImage;
    private BufferedImage[] cellImages = new BufferedImage[7];
    private BufferedImage[] hardDropImages = new BufferedImage[7];

    // 游戏逻辑类
    private Tetris tetris = new Tetris();

    /**
     * 将键盘输入转换为actionType
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
     * 11   Speed Up     ]
     * 12   Speed Down   [
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
        keyActionMap.put(KeyEvent.VK_CLOSE_BRACKET, 11);
        keyActionMap.put(KeyEvent.VK_OPEN_BRACKET, 12);
    }

    // 将level转换为速度
    private static Map<Integer, Integer> levelSpeedMap = new HashMap<Integer, Integer>();

    static {
        levelSpeedMap.put(7, 34);
        levelSpeedMap.put(6, 29);
        levelSpeedMap.put(5, 23);
        levelSpeedMap.put(4, 19);
        levelSpeedMap.put(3, 15);
        levelSpeedMap.put(2, 9);
        levelSpeedMap.put(1, 5);
    }

    // 按键事件监听器
    private KeyListener keyListener = new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            int keyCode = e.getKeyCode();
            int flag = keyToAction(keyCode);
            repaint();
        }
    };

    // 设置定时任务
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

    // 开始游戏运行
    public void run() {
        timer.schedule(timerTask, 100, 20);
    }

    // 初始化界面资源
    private void initiate() {
        imageLoad();
        this.addKeyListener(keyListener);
        this.setFocusable(true);
        this.requestFocus();
    }

    // 将键盘输入转换为actionType
    private int keyToAction(int keyCode) {
        int flag = -1;
        if (tetris == null || keyActionMap.get(keyCode) == null)
            return flag;
        flag = tetris.action(keyActionMap.get(keyCode));
        return flag;
    }

    // 加载界面中的图片资源
    private void imageLoad() {
        try {
            bgImage = ImageIO.read(UI.class.getResource("/background.png"));
            waitImage = ImageIO.read(UI.class.getResource("/start.png"));
            for (int i = 0; i < cellImages.length; i++) {
                cellImages[i] = ImageIO.read(UI.class.getResource("/cellImages/" + i + ".png"));
            }
            for (int i = 0; i < hardDropImages.length; i++) {
                hardDropImages[i] = ImageIO.read(UI.class.getResource("/hardDropImages/" + i + ".png"));
            }
            playgroundImage = ImageIO.read(UI.class.getResource("/playground.png"));
            pauseImage = ImageIO.read(UI.class.getResource("/pause.png"));
            gameoverImage = ImageIO.read(UI.class.getResource("/gameover.png"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 绘制界面
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
        if (tetro == null) return;
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
        Color color = new Color(255, 255, 255);
        g.setColor(color);
        Font font = new Font(Font.SANS_SERIF, Font.BOLD, 30);
        g.setFont(font);
        g.drawString(String.valueOf(score), 60, 170);
        g.drawString("SCORE", 35, 220);
        g.drawString("LEVEL " + level, 20, 300);
        g.drawString(String.valueOf(pieces), 60, 370);
        g.drawString("PIECEs", 25, 420);
        g.drawString(String.valueOf(lines), 60, 470);
        g.drawString("LINEs", 30, 520);
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
        Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 30);
        g.setColor(color);
        g.setFont(font);
        g.drawString(String.valueOf(tetris.getScore()), 340, 342);
    }
}
