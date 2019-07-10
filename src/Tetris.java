public class Tetris {
    private static int ROWS = 20;
    private static int COLS = 10;
    private int[][] wall = new int[ROWS][COLS];

    private int state; // 0 --- wait | 1 --- run | 2 --- pause | -1 --- gameover
    private int score;
    private int lines;
    private int pieces;
    private int level;

    private SevenBag tetroBag;
    private Tetromino currentTetro;
    private int[] nextTetrosType = new int[4];
    private Tetromino hardDropTetro;
    private int currentHoldType;
    private boolean holdState;

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

    private boolean speedUpAction() {
        boolean flag;
        if (level > 0) {
            level--;
            flag = true;
        } else {
            flag = false;
        }
        return flag;
    }

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

    private boolean isGameover() {
        for (int i = 0; i < COLS; i++) {
            if (wall[0][i] != -1) {
                return true;
            }
        }
        return false;
    }

    private void saveCellsToWall() {
        for (Cell cell : currentTetro.getCells()) {
            int row = cell.getRow();
            int col = cell.getCol();
            this.wall[row][col] = cell.getCellType();
        }
        int temp = removeLine();
        lines += temp;
        score += temp * 10;
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

    private boolean moveLeftAction() {
        if (canLeftMove() && !isBottom(currentTetro)) {
            currentTetro.moveLeft();
            hardDropGenerate();
            return true;
        }
        return false;
    }

    private boolean moveRightAction() {
        if (canRightMove() && !isBottom(currentTetro)) {
            currentTetro.moveRight();
            hardDropGenerate();
            return true;
        }
        return false;
    }

    private boolean moveDownAction() {
        if (!isBottom(currentTetro)) {
            currentTetro.moveDown();
            return true;
        }
        return false;
    }

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
        System.out.println("[CwSpin] " + flag);
        return flag;
    }

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

    private boolean holdAction() {
        System.out.println("[Hold] HoldState :" + holdState + " CurrentHoldType: " + currentHoldType);
        if (!holdState) {
            if (currentHoldType == -1) {
                this.currentHoldType = currentTetro.getTetroType();
                newTetro();
                hardDropGenerate();
                holdState = true;
                return true;
            }
            int temp = this.currentHoldType;
            this.currentHoldType = currentTetro.getTetroType();
            this.currentTetro = new Tetromino(temp);
            hardDropGenerate();
            holdState = true;
            return true;
        }
        return false;
    }

    private void hardDropAction() {
        if (hardDropTetro == null)
            return;
        currentTetro = hardDropTetro;
    }

    /**
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
        if (isBottom(currentTetro)) {
            saveCellsToWall();
        }
        return flag;
    }
}
