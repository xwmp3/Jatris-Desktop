package Model;

public class Tetromino{
    private static int CENTERNO = 2;
    private Cell[] cells = new Cell[4];
    private int tetroType;

    public Tetromino(int _tetroType){
        this.tetroType = _tetroType;
        this.cellsGenerate();
    }

    public Tetromino(int _tetroType, Cell[] _cell) {
        this.tetroType = _tetroType;
        this.cells = _cell;
    }

    public Cell[] getCells() {
        return cells;
    }

    public void setCells(Cell[] cells) {
        this.cells = cells;
    }

    public int getTetroType() {
        return tetroType;
    }

    private void cellsGenerate() {
        int type = this.tetroType;
        switch (type) {
            case 0: // I
                cells[0] = new Cell(0, 3, type);
                cells[1] = new Cell(0, 4, type);
                cells[2] = new Cell(0, 5, type);
                cells[3] = new Cell(0, 6, type);
                break;
            case 1: // O
                cells[0] = new Cell(0, 4, type);
                cells[1] = new Cell(0, 5, type);
                cells[2] = new Cell(1, 5, type);
                cells[3] = new Cell(1, 4, type);
                break;
            case 2: // T
                cells[0] = new Cell(0, 4, type);
                cells[1] = new Cell(1, 3, type);
                cells[2] = new Cell(1, 4, type);
                cells[3] = new Cell(1, 5, type);
                break;
            case 3: // S
                cells[0] = new Cell(0, 5, type);
                cells[1] = new Cell(0, 4, type);
                cells[2] = new Cell(1, 4, type);
                cells[3] = new Cell(1, 3, type);
                break;
            case 4: // Z
                cells[0] = new Cell(0, 3, type);
                cells[1] = new Cell(0, 4, type);
                cells[2] = new Cell(1, 4, type);
                cells[3] = new Cell(1, 5, type);
                break;
            case 5: // J
                cells[0] = new Cell(0, 3, type);
                cells[1] = new Cell(1, 3, type);
                cells[2] = new Cell(1, 4, type);
                cells[3] = new Cell(1, 5, type);
                break;
            case 6: // J
                cells[0] = new Cell(0, 5, type);
                cells[1] = new Cell(1, 5, type);
                cells[2] = new Cell(1, 4, type);
                cells[3] = new Cell(1, 3, type);
                break;
        }
    }

    public void moveDown() {
        if (cells == null) return;
        for (Cell cell : this.cells) {
            cell.moveDown();
        }
    }

    public void moveLeft() {
        for (Cell cell : this.cells) {
            cell.moveLeft();
        }
    }

    public void moveRight() {
        for (Cell cell : this.cells) {
            cell.moveRight();
        }
    }

    public Cell[] cwSpin(){
        if (this.tetroType == 1)
            return null;
        Cell[] tempCells = new Cell[4];
        for (int i = 0; i < tempCells.length; i++) {
            try {
                tempCells[i] = (Cell)cells[i].clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
        Cell center = tempCells[CENTERNO];
        for (Cell tempCell : tempCells) {
            tempCell.cwSpin(center);
        }
        return tempCells;
    }

    public Cell[] antiCwSpin() {
        if (this.tetroType == 1)
            return null;
        Cell[] tempCells = new Cell[4];
        for (int i = 0; i < tempCells.length; i++) {
            try {
                tempCells[i] = (Cell)cells[i].clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
        Cell center = tempCells[CENTERNO];
        for (Cell tempCell : tempCells) {
            tempCell.antiCwSpin(center);
        }
        return tempCells;
    }
}
