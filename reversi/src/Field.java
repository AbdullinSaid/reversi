import java.util.ArrayList;

/**
 * Game field
 */
public class Field {
    final static int FIELD_SIZE = 8;

    Cell[][] cells;

    public Field() {
        cells = new Cell[FIELD_SIZE][FIELD_SIZE];
        System.out.println(cells[0].length);
        for (int i = 0; i < FIELD_SIZE; ++i) {
            for (int j = 0; j < FIELD_SIZE; ++j) {
                cells[i][j] = new Cell();
            }
        }
        for (int i = 0; i < FIELD_SIZE; ++i) {
            for (int j = 0; j < FIELD_SIZE; ++j) {
                if ((i == 0 || i == FIELD_SIZE - 1) && (j == 0 || j == FIELD_SIZE - 1)) {
                    cells[i][j].captureWeight = 2;
                    cells[i][j].turnWeight = 0.8;
                } else if (i == 0 || i == FIELD_SIZE - 1 || j == 0 || j == FIELD_SIZE - 1) {
                    cells[i][j].captureWeight = 2;
                    cells[i][j].turnWeight = 0.4;
                } else {
                    cells[i][j].captureWeight = 1;
                    cells[i][j].turnWeight = 0;
                }
            }
        }
        cells[3][3].status = CellStatus.WHITE;
        cells[4][4].status = CellStatus.WHITE;
        cells[3][4].status = CellStatus.BLACK;
        cells[4][3].status = CellStatus.BLACK;
    }

    public Field(Field field) {
        cells = new Cell[FIELD_SIZE][FIELD_SIZE];
        for (int i = 0; i < FIELD_SIZE; ++i) {
            for (int j = 0; j < FIELD_SIZE; ++j) {
                cells[i][j] = new Cell(field.cells[i][j]);
            }
        }
    }

    /**
     * get possible moves this turn for one player
     * @param playerCellStatus player color by cellStatus
     * @return ArrayList of possible move Points
     */
    public ArrayList<Point> getPossibleMoves(CellStatus playerCellStatus) {
        ArrayList<Point> result = new ArrayList<>();
        for (int i = 0; i < FIELD_SIZE; ++i) {
            for (int j = 0; j < FIELD_SIZE; ++j) {
                if (cells[i][j].status == CellStatus.EMPTY) {
                    double value = 0;
                    for (int x = -1; x <= 1; ++x) {
                        for (int y = -1; y <= 1; ++y) {
                            int currentValue = 0;
                            if (x == 0 && y == 0) {
                                continue;
                            }
                            int x1 = i + x;
                            int y1 = j + y;
                            while (x1 >= 0 && x1 < FIELD_SIZE && y1 >= 0 && y1 < FIELD_SIZE) {
                                if (cells[x1][y1].status == CellStatus.EMPTY) {
                                    break;
                                }
                                if (cells[x1][y1].status == playerCellStatus) {
                                    value += currentValue;
                                    break;
                                } else {
                                    currentValue += cells[x1][y1].captureWeight;
                                }
                                x1 += x;
                                y1 += y;
                            }
                        }
                    }
                    if (value != 0) {
                        Point pt = new Point(i, j, value + cells[i][j].turnWeight);
                        result.add(pt);
                    }
                }
            }
        }
        return result;
    }

    private Point getMaxTurnValue(CellStatus playerCellStatus) {
        var possibleMoves = getPossibleMoves(playerCellStatus);
        Point result = new Point();
        for (Point possibleMove : possibleMoves) {
            if (result.value < possibleMove.value) {
                result.value = possibleMove.value;
                result.x = possibleMove.x;
                result.y = possibleMove.y;
            }
        }
        return result;
    }

    public void putToken(CellStatus playerCellStatus, Point pt) {
        cells[pt.x][pt.y].status = playerCellStatus;
        ArrayList<Point> pointsToRecolor = new ArrayList<>();
        for (int x = -1; x <= 1; ++x) {
            for (int y = -1; y <= 1; ++y) {
                ArrayList<Point> currentPoints = new ArrayList<>();
                if (x == 0 && y == 0) {
                    continue;
                }
                int x1 = pt.x + x;
                int y1 = pt.y + y;
                while (x1 >= 0 && x1 < FIELD_SIZE && y1 >= 0 && y1 < FIELD_SIZE) {
                    if (cells[x1][y1].status == CellStatus.EMPTY) {
                        break;
                    }
                    if (cells[x1][y1].status == playerCellStatus) {
                        pointsToRecolor.addAll(currentPoints);
                        break;
                    } else {
                        currentPoints.add(new Point(x1, y1, 0));
                    }
                    x1 += x;
                    y1 += y;
                }
            }
        }
        for (Point pointToRecolor : pointsToRecolor) {
            cells[pointToRecolor.x][pointToRecolor.y].status = playerCellStatus;
        }
    }

    /**
     * Make one move for AI side.
     * @param computerCellStatus color of computer
     * @param difficulty difficulty of AI(0 - easy, 1 - hard)
     */
    public void makeMove(CellStatus computerCellStatus, int difficulty) {
        CellStatus playerCellStatus;
        if (computerCellStatus == CellStatus.BLACK) {
            playerCellStatus = CellStatus.WHITE;
        } else {
            playerCellStatus = CellStatus.BLACK;
        }
        if (difficulty == 0) {
            Point move = getMaxTurnValue(computerCellStatus);
            if (move.value == 0) {
                System.out.println("No possible turn for opponent");
            } else {
                putToken(computerCellStatus, move);
                System.out.println("Opponent made a move on x = " + (move.x + 1) + " y = " + (move.y + 1));
            }
        } else {
            var possibleMoves = getPossibleMoves(computerCellStatus);
            Point result = new Point();
            result.value = -1e9;
            if (possibleMoves.size() == 0) {
                System.out.println("No possible turn for opponent");
                return;
            }
            for (Point possibleMove : possibleMoves) {
                Cell[][] cellsCopy = new Cell[FIELD_SIZE][FIELD_SIZE];
                for (int i = 0; i < FIELD_SIZE; ++i) {
                    for (int j = 0; j < FIELD_SIZE; ++j) {
                        cellsCopy[i][j] = new Cell(cells[i][j]);
                    }
                }
                putToken(computerCellStatus, new Point(possibleMove.x, possibleMove.y, 0));
                if (result.value < possibleMove.value - getMaxTurnValue(playerCellStatus).value) {
                    result.value = possibleMove.value - getMaxTurnValue(playerCellStatus).value;
                    result.x = possibleMove.x;
                    result.y = possibleMove.y;
                }
                for (int i = 0; i < FIELD_SIZE; ++i) {
                    for (int j = 0; j < FIELD_SIZE; ++j) {
                        cells[i][j] = new Cell(cellsCopy[i][j]);
                    }
                }
            }
            putToken(computerCellStatus, result);
            System.out.println("Opponent made a move on x = " + (result.x + 1) + " y = " + (result.y + 1));
        }
    }

    public void printFieldWithPossibleTurns(CellStatus playerCellStatus) {
        var possibleMoves = getPossibleMoves(playerCellStatus);
        for (int i = 0; i < FIELD_SIZE + 1; ++i) {
            for (int j = 0; j < FIELD_SIZE + 1; ++j) {
                if (i == 0) {
                    if (j != 0) {
                        System.out.print((char)(j + '①' - 1));
                    } else {
                        System.out.print("_");
                    }
                } else {
                    if (j == 0) {
                        System.out.print(i);
                        continue;
                    }
                    if (cells[i - 1][j - 1].status == CellStatus.EMPTY) {
                        boolean isPossibleMove = false;
                        for (Point possibleMove : possibleMoves) {
                            if (i - 1 == possibleMove.x && j - 1 == possibleMove.y) {
                                System.out.print("▢");
                                isPossibleMove = true;
                                break;
                            }
                        }
                        if (!isPossibleMove) {
                            System.out.print("▦");
                        }
                    } else if (cells[i - 1][j - 1].status == CellStatus.WHITE) {
                        System.out.print("◉");
                    } else {
                        System.out.print("❂");
                    }
                }
            }
            System.out.println();
        }
    }
}
