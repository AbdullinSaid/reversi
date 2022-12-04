public class Cell {
    CellStatus status = CellStatus.EMPTY;
    double captureWeight;
    double turnWeight;

    public Cell() {
        captureWeight = 0;
        turnWeight = 0;
    }

    public Cell(Cell cell) {
        status = cell.status;
        captureWeight = cell.captureWeight;
        turnWeight = cell.turnWeight;
    }
}
