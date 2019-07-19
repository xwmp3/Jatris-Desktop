package Model;

public class Cell implements Cloneable{
    private int row;
    private int col;
    private int cellType; // 类型

    public Cell(int _row, int _col, int _cellType) {
        this.row = _row;
        this.col = _col;
        this.cellType = _cellType;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return (Cell) super.clone();
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public int getCellType() {
        return cellType;
    }

    public void setCellType(int cellType) {
        this.cellType = cellType;
    }

    // 向下移动
    public void moveDown() {
        this.row++;
    }

    // 向左移动
    public void moveLeft() {
        this.col--;
    }

    // 向右移动
    public void moveRight() {
        this.col++;
    }

    // 顺时针旋转
    public void cwSpin(Cell center) {
        int tempRow = this.row;
        int tempCol = this.col;
        this.row = center.row - center.col + tempCol;
        this.col = center.col + center.row - tempRow;
    }

    // 逆时针旋转
    public void antiCwSpin(Cell center) {
        int tempRow = this.row;
        int tempCol = this.col;
        this.row = center.row + center.col - tempCol;
        this.col = center.col - center.row + tempRow;
    }
}
