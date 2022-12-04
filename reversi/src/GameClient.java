import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Class with game client, can print menu, start games, save best score
 */
public class GameClient {
    private int bestScore;

    private void printMenu() {
        System.out.println("Welcome to the reversi game");
        System.out.println("Best score: " + bestScore);
        System.out.println("1.Start mode player vs easy bot");
        System.out.println("2.Start mode player vs hard bot");
        System.out.println("3.Start mode player vs player");
        System.out.println("4.Close client");
    }

    public void startClient() {
        boolean isClientClosed = false;
        while (!isClientClosed) {
            printMenu();
            Scanner in = new Scanner(System.in);
            int mode = -1;
            try {
                mode = in.nextInt();
            } catch (InputMismatchException ignored) {
            }
            switch (mode) {
                case 1 -> startGameWithComputer(0);
                case 2 -> startGameWithComputer(1);
                case 3 -> startGameWithPlayer();
                case 4 -> isClientClosed = true;
                default -> System.out.println("Incorrect input");
            }
        }
    }

    private void startGameWithComputer(int difficulty) {
        ArrayList<Field> turns = new ArrayList<>();
        turns.add(new Field());
        Scanner in = new Scanner(System.in);
        while (turns.get(turns.size() - 1).getPossibleMoves(CellStatus.BLACK).size() > 0
                || turns.get(turns.size() - 1).getPossibleMoves(CellStatus.WHITE).size() > 0) {
            turns.get(turns.size() - 1).printFieldWithPossibleTurns(CellStatus.BLACK);
            var possiblePlayerTurns = turns.get(turns.size() - 1).getPossibleMoves(CellStatus.BLACK);
            if (possiblePlayerTurns.size() > 0) {
                System.out.println("Choose one of possible turns(print turn number) or undo turn");
                System.out.println("0. Undo turn");
                for (int i = 0; i < possiblePlayerTurns.size(); ++i) {
                    System.out.println((i + 1) + ". x = " + (possiblePlayerTurns.get(i).x + 1) +
                            " y = " + (possiblePlayerTurns.get(i).y + 1));
                }
                int turnNum = -1;
                try {
                    turnNum = in.nextInt();
                } catch (InputMismatchException ignored) {
                }
                if (turnNum < 0 || turnNum > possiblePlayerTurns.size()) {
                    System.out.println("Incorrect input.");
                    continue;
                }
                if (turnNum == 0) {
                    if (turns.size() > 1) {
                        turns.remove(turns.size() - 1);
                    }
                    continue;
                }
                turns.add(new Field(turns.get(turns.size() - 1)));
                turns.get(turns.size() - 1).putToken(CellStatus.BLACK, possiblePlayerTurns.get(turnNum - 1));
            } else {
                System.out.println("No possible turns, you miss a move.");
            }
            turns.get(turns.size() - 1).makeMove(CellStatus.WHITE, 0);
        }
        int blackCounter = 0;
        int whiteCounter = 0;
        for (int i = 0; i < turns.get(turns.size() - 1).cells.length; ++i) {
            for (int j = 0; j < turns.get(turns.size() - 1).cells.length; ++j) {
                if (turns.get(turns.size() - 1).cells[i][j].status == CellStatus.BLACK) {
                    blackCounter++;
                } else if (turns.get(turns.size() - 1).cells[i][j].status == CellStatus.WHITE) {
                    whiteCounter++;
                }
            }
        }
        turns.get(turns.size() - 1).printFieldWithPossibleTurns(CellStatus.BLACK);
        if (blackCounter > whiteCounter) {
            System.out.println("Player wins with " + blackCounter + " points");
        } else if (blackCounter == whiteCounter) {
            System.out.println("Draw. " + blackCounter + " points");
        } else {
            System.out.println("Player loses with " + blackCounter + " points");
        }
        if (bestScore < blackCounter) {
            bestScore = blackCounter;
        }
    }

    private void startGameWithPlayer() {
        ArrayList<Field> turns = new ArrayList<>();
        ArrayList<CellStatus> turnStatus = new ArrayList<>();
        turns.add(new Field());
        CellStatus currentPlayerStatus = CellStatus.BLACK;
        String currentColor = "Black";
        Scanner in = new Scanner(System.in);
        while (turns.get(turns.size() - 1).getPossibleMoves(CellStatus.BLACK).size() > 0
                || turns.get(turns.size() - 1).getPossibleMoves(CellStatus.WHITE).size() > 0) {
            turns.get(turns.size() - 1).printFieldWithPossibleTurns(currentPlayerStatus);
            var possiblePlayerTurns = turns.get(turns.size() - 1).getPossibleMoves(currentPlayerStatus);
            if (possiblePlayerTurns.size() > 0) {
                System.out.println(currentColor + ", choose one of possible turns(print turn number) or undo turn");
                System.out.println("0. Undo turn");
                for (int i = 0; i < possiblePlayerTurns.size(); ++i) {
                    System.out.println((i + 1) + ". x = " + (possiblePlayerTurns.get(i).x + 1) +
                            " y = " + (possiblePlayerTurns.get(i).y + 1));
                }
                int turnNum = -1;
                try {
                    turnNum = in.nextInt();
                } catch (InputMismatchException ignored) {
                }
                if (turnNum < 0 || turnNum > possiblePlayerTurns.size()) {
                    System.out.println("Incorrect input.");
                    continue;
                }
                if (turnNum == 0) {
                    if (turns.size() > 1) {
                        turns.remove(turns.size() - 1);
                        currentPlayerStatus = turnStatus.get(turnStatus.size() - 1);
                        if (currentPlayerStatus == CellStatus.WHITE) {
                            currentColor = "White";
                        } else {
                            currentColor = "Black";
                        }
                        turnStatus.remove(turnStatus.size() - 1);
                    }
                    continue;
                }
                turns.add(new Field(turns.get(turns.size() - 1)));
                turnStatus.add(currentPlayerStatus);
                turns.get(turns.size() - 1).putToken(currentPlayerStatus, possiblePlayerTurns.get(turnNum - 1));
                if (currentPlayerStatus == CellStatus.BLACK) {
                    currentColor = "White";
                    currentPlayerStatus = CellStatus.WHITE;
                } else {
                    currentColor = "Black";
                    currentPlayerStatus = CellStatus.BLACK;
                }
            } else {
                System.out.println(currentColor + ", no possible turns, you miss a move.");
                if (currentPlayerStatus == CellStatus.BLACK) {
                    currentColor = "White";
                    currentPlayerStatus = CellStatus.WHITE;
                } else {
                    currentColor = "Black";
                    currentPlayerStatus = CellStatus.BLACK;
                }
            }
        }
        int blackCounter = 0;
        int whiteCounter = 0;
        for (int i = 0; i < turns.get(turns.size() - 1).cells.length; ++i) {
            for (int j = 0; j < turns.get(turns.size() - 1).cells.length; ++j) {
                if (turns.get(turns.size() - 1).cells[i][j].status == CellStatus.BLACK) {
                    blackCounter++;
                } else if (turns.get(turns.size() - 1).cells[i][j].status == CellStatus.WHITE) {
                    whiteCounter++;
                }
            }
        }
        turns.get(turns.size() - 1).printFieldWithPossibleTurns(CellStatus.BLACK);
        if (blackCounter > whiteCounter) {
            System.out.println("Black wins with " + blackCounter + " points");
        } else if (blackCounter == whiteCounter) {
            System.out.println("Draw. " + blackCounter + " points");
        } else {
            System.out.println("White wins with " + whiteCounter + " points");
        }
    }

    public GameClient() {
        bestScore = 0;
    }
}
