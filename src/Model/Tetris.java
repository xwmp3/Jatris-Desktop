package Model;

import Algorithm.SevenBag;

public class Tetris {
    private static int ROWS = 20; // 底部方块数组的大小
    private static int COLS = 10;
    private int[][] wall = new int[ROWS][COLS]; // 底部方块数组

    private int state; // 游戏状态： 0 --- wait | 1 --- run | 2 --- pause | -1 --- gameover
    private int score; // 得分
    private int lines; // 已消除行数
    private int pieces; // 已消除块数
    private int level; // 下落速度，数字越小越快

    private SevenBag tetroBag; // 生成新方块的算法实例
    private Tetromino currentTetro; // 当前方块
    private int[] nextTetrosType = new int[4]; // 预览方块类型序列
    private Tetromino hardDropTetro; // 当前方块的硬降预览方块
    private int currentHoldType; // 当前Hold槽中的方块类型（-1代表为空）
    private boolean holdState; // Hold状态（true已Hold，false未Hold）

    public Tetris() {
        initiate();
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getScore() {
        return score;
    }

    public int getLines() {
        return lines;
    }

    public int getPieces() {
        return pieces;
    }

    public int getLevel() {
        return level;
    }

    public Tetromino getCurrentTetro() {
        return currentTetro;
    }

    public int[] getNextTetrosType() {
        return nextTetrosType;
    }

    public Tetromino getHardDropTetro() {
        return hardDropTetro;
    }

    public int getCurrentHoldType() {
        return currentHoldType;
    }

    public int[][] getWall() {
        return wall;
    }

    // 初始化游戏数据
    private void initiate() {
        for (int i = 0; i < ROWS; i++)
            for (int j = 0; j < COLS; j++)
                wall[i][j] = -1;
        state = 0;
        score = 0;
        lines = 0;
        pieces = 0;
        level = 7;
        tetroBag = new SevenBag(4);
        newTetro();
        hardDropGenerate();
        currentHoldType = -1;
        holdState = false;
    }

    public void start() {
        this.state = 1;
    }

    public void pause() {
        this.state = 2;
    }

    public void resume() {
        this.state = 1;
    }

    public void restart() {
        initiate();
    }

    private void gameover() {
        this.state = -1;
    }

    // 获取新的当前方块和预览方块序列
    private void newTetro() {
        int[] tempTypeInt = tetroBag.newNextPieces();
        for (int i = 0; i < tempTypeInt.length; i++) {
            if (i == 0) {
                this.currentTetro = new Tetromino(tempTypeInt[i]);
            } else {
                nextTetrosType[i - 1] = tempTypeInt[i];
            }
        }
    }

    // 根据当前方块的状态，生成当前方块的硬降预览方块
    private void hardDropGenerate() {
        Cell[] tempCells = new Cell[4];
        Cell[] cells = currentTetro.getCells();
        if (cells == null) return;
        for (int i = 0; i < tempCells.length; i++) {
            tempCells[i] = new Cell(cells[i].getRow(), cells[i].getCol(), cells[i].getCellType());
        }
        Tetromino tempTetro = new Tetromino(currentTetro.getTetroType(), tempCells);
        do {
            tempTetro.moveDown();
        } while (!isBottom(tempTetro));
        hardDropTetro = tempTetro;
    }

    // 扫描底部方块数组，自动消行，返回消除的行数
    private int removeLine() {
        int removed = 0;
        boolean flag = true;
        int rowStart = 0;
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (wall[row][col] == -1) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                rowStart = row;
                removed++;
                for (int row1 = rowStart; row1 > 0; row1--) {
                    for (int col1 = 0; col1 < COLS; col1++) {
                        wall[row1][col1] = wall[row1 - 1][col1];
                    }
                }
            } else {
                flag = true;
            }
        }
        return removed;
    }

    // 加快下落速度
    private boolean speedUpAction() {
        boolean flag;
        if (level > 1) {
            level--;
            flag = true;
        } else {
            flag = false;
        }
        return flag;
    }

    // 减慢下落速度
    private boolean speedDownAction() {
        boolean flag;
        if (level < 7) {
            level++;
            flag = true;
        } else {
            flag = false;
        }
        return flag;
    }

    // 检测游戏是否结束
    private boolean isGameover() {
        for (int i = 0; i < COLS; i++) {
            if (wall[0][i] != -1) {
                return true;
            }
        }
        return false;
    }

    // 将当前方块存入底部方块数组
    private void saveCellsToWall() {
        for (Cell cell : currentTetro.getCells()) {
            int row = cell.getRow();
            int col = cell.getCol();
            this.wall[row][col] = cell.getCellType();
        }
        int temp = removeLine();
        lines += temp;
        scoreCalc(temp);
        pieces++;
        newTetro();
        hardDropGenerate();
        holdState = false;
        if (pieces >= 30 && pieces % 30 == 0 && level > 1)
            level--;
        if (isGameover()) {
            gameover();
        }
    }

    // 检测当前方块是否触底
    private boolean isBottom(Tetromino tetro) {
        Cell[] cells = tetro.getCells();
        if (cells == null) return false;
        for (Cell cell : cells) {
            int row = cell.getRow();
            int col = cell.getCol();
            if (row + 1 == ROWS || wall[row + 1][col] != -1) {
                return true;
            }
        }
        return false;
    }

    // 检测当前方块是否能够左移
    private boolean canLeftMove() {
        Cell[] cells = currentTetro.getCells();
        if (cells == null) return false;
        for (Cell cell : cells) {
            int row = cell.getRow();
            int col = cell.getCol();
            if (col == 0 || wall[row][col - 1] != -1)
                return false;
        }
        return true;
    }

    // 检测当前方块是否能够右移
    private boolean canRightMove() {
        Cell[] cells = currentTetro.getCells();
        if (cells == null) {
            return false;
        }
        for (Cell cell : cells) {
            int row = cell.getRow();
            int col = cell.getCol();
            if (col + 1 == COLS || wall[row][col + 1] != -1)
                return false;
        }
        return true;
    }

    // 检测当前方块是否能够旋转
    private boolean canSpin(Cell[] tempCells) {
        if (tempCells == null)
            return false;
        if (currentTetro.getTetroType() == 1)
            return false;
        for (Cell tempCell : tempCells) {
            int row = tempCell.getRow();
            int col = tempCell.getCol();
            if (row < 0 || col >= ROWS || col < 0 || col >= COLS || wall[row][col] != -1)
                return false;
        }
        return true;
    }

    // 当前方块左移
    private boolean moveLeftAction() {
        if (canLeftMove() && !isBottom(currentTetro)) {
            currentTetro.moveLeft();
            hardDropGenerate();
            return true;
        }
        return false;
    }

    // 当前方块右移
    private boolean moveRightAction() {
        if (canRightMove() && !isBottom(currentTetro)) {
            currentTetro.moveRight();
            hardDropGenerate();
            return true;
        }
        return false;
    }

    // 当前方块下移
    private boolean moveDownAction() {
        if (!isBottom(currentTetro)) {
            currentTetro.moveDown();
            return true;
        }
        return false;
    }

    // 当前方块顺时针旋转
    private boolean cwSpinAction() {
        boolean flag;
        Cell[] tempCells = currentTetro.cwSpin();
        if (canSpin(tempCells)) {
            currentTetro.setCells(tempCells);
            hardDropGenerate();
            flag = true;
        } else {
            flag = false;
        }
        return flag;
    }

    // 当前方块逆时针旋转
    private boolean antiCwSpinAction() {
        Cell[] tempCells = currentTetro.antiCwSpin();
        if (canSpin(tempCells)) {
            currentTetro.setCells(tempCells);
            hardDropGenerate();
            return true;
        } else {
            return false;
        }
    }

    // Hold操作
    private boolean holdAction() {
        boolean flag = false;
        if (!holdState) {
            if (currentHoldType == -1) {
                this.currentHoldType = currentTetro.getTetroType();
                newTetro();
                hardDropGenerate();
                holdState = true;
                flag = true;
            } else {
                int temp = this.currentHoldType;
                this.currentHoldType = currentTetro.getTetroType();
                this.currentTetro = new Tetromino(temp);
                hardDropGenerate();
                holdState = true;
                flag = true;
            }
        }
        return flag;
    }

    // 当前方块硬降
    private void hardDropAction() {
        if (hardDropTetro == null)
            return;
        currentTetro = hardDropTetro;
    }

    // 计算游戏分数
    private void scoreCalc(int removedLines) {
        switch (removedLines) {
            case 1:
                score += 10;
                break;
            case 2:
                score += 20;
                break;
            case 3:
                score += 40;
                break;
            case 4:
                score += 80;
                break;
            default:
                break;
        }
    }

    /**
     * 根据输入的actionType，执行对应的action
     * @param actionType Type Action
     *                   0    start
     *                   1    pause
     *                   2    resume
     *                   3    restart
     *                   4    moveDown
     *                   5    hardDrop
     *                   6    moveLeft
     *                   7    moveRight
     *                   8    cwSpin
     *                   9    antiCwSpin
     *                   10   hold
     *                   11   speedUp
     *                   12   speedDown
     * @return flag
     * <p>
     * Flag State
     * 0    wait
     * 1    running
     * 2    pause
     * <p>
     * 3    move success
     * 4    move failure
     * 5    hard drop
     * 6    hold success
     * 7    hold failure
     */
    public int action(int actionType) {
        int flag = 0;
        switch (actionType) {
            case 0:
                start();
                flag = 1;
                break;
            case 1:
                pause();
                flag = 2;
                break;
            case 2:
                resume();
                flag = 1;
                break;
            case 3:
                restart();
                flag = 0;
                break;
            case 4:
                flag = moveDownAction() ? 3 : 4;
                break;
            case 5:
                hardDropAction();
                flag = 5;
                break;
            case 6:
                flag = moveLeftAction() ? 3 : 4;
                break;
            case 7:
                flag = moveRightAction() ? 3 : 4;
                break;
            case 8:
                flag = cwSpinAction() ? 3 : 4;
                break;
            case 9:
                flag = antiCwSpinAction() ? 3 : 4;
                break;
            case 10:
                flag = holdAction() ? 6 : 7;
                break;
            case 11:
                flag = speedUpAction() ? 3 : 4;
                break;
            case 12:
                flag = speedDownAction() ? 3 : 4;
                break;
        }
        // 当方块触底，把方块存到底部方块数组
        if (isBottom(currentTetro)) {
            saveCellsToWall();
        }
        return flag;
    }
}
