public class Cell implements Cloneable{
    private int row;
    private int col;
    private int cellType;

    public Cell(int _row, int _col, int _cellType) {
        this.row = _row;
        this.col = _col;
        this.cellType = _cellType;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return (Cell) super.clone();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
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

    public void moveDown() {
        this.row++;
    }

    public void moveLeft() {
        this.col--;
    }

    public void moveRight() {
        this.col++;
    }

    public void cwSpin(Cell center) {
        int tempRow = this.row;
        int tempCol = this.col;
        this.row = center.row - center.col + tempCol;
        this.col = center.col + center.row - tempRow;
    }

    public void antiCwSpin(Cell center) {
        int tempRow = this.row;
        int tempCol = this.col;
        this.row = center.row + center.col - tempCol;
        this.col = center.col - center.row + tempRow;
    }
}
