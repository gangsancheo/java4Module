import java.util.*;

public class BattleshipGame {
    private static final int BOARD_SIZE = 8;
    private static final int TIMEOUT_SECONDS = 15;
    private static final List<Ship> SHIPS = Arrays.asList(
            new Ship(1, Arrays.asList("B1")),
            new Ship(2, Arrays.asList("E2", "E3")),
            new Ship(2, Arrays.asList("H5", "H6"))
    );

    private final char[][] board;
    private final Set<String> ships;
    private int shotsCount;

    public BattleshipGame() {
        board = new char[BOARD_SIZE][BOARD_SIZE];
        ships = new HashSet<>();
        shotsCount = 0;
    }

    public void startGame() {
        initializeBoard();
        placeShips();
        printBoard();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Куда стреляем: ");
            Thread inputThread = new Thread(() -> {
                String input = scanner.nextLine().toUpperCase();
                processUserInput(input);
            });
            inputThread.start();

            try {
                inputThread.join(TIMEOUT_SECONDS * 1000);
                if (inputThread.isAlive()) {
                    System.out.println("Слишком долго! Вы проиграли.");
                    break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (ships.isEmpty()) {
                System.out.println("Все корабли уничтожены!");
                break;
            }

            printBoard();
        }
    }

    private void initializeBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            Arrays.fill(board[i], '.');
        }
    }

    private void placeShips() {
        for (Ship ship : SHIPS) {
            for (String cell : ship.getCells()) {
                int row = cell.charAt(1) - '1';
                int col = cell.charAt(0) - 'A';
                board[row][col] = 'U';
                ships.add(cell);
            }
        }
    }

    private void printBoard() {
        System.out.print("  ");
        for (char c = 'A'; c < 'A' + BOARD_SIZE; c++) {
            System.out.print(c);
        }
        System.out.println();

        for (int i = 0; i < BOARD_SIZE; i++) {
            System.out.print((i + 1) + " ");
            for (int j = 0; j < BOARD_SIZE; j++) {
                System.out.print(board[i][j]);
            }
            System.out.println();
        }
    }

    private void processUserInput(String input) {
        if (input.equals("EXIT")) {
            System.out.println("Игра завершена.");
            System.exit(0);
        }

        if (input.equals("RESULTS")) {
            printResults();
            return;
        }

        if (input.length() != 2 || !Character.isLetter(input.charAt(0)) || !Character.isDigit(input.charAt(1))) {
            System.out.println("Неверный формат ввода. Введите ячейку в формате 'A2', 'D5' и т.д.");
            return;
        }

        int row = input.charAt(1) - '1';
        int col = input.charAt(0) - 'A';

        if (row < 0 || row >= BOARD_SIZE || col < 0 || col >= BOARD_SIZE) {
            System.out.println("Неверные координаты. Введите ячейку в формате 'A2', 'D5' и т.д.");
            return;
        }

        shotsCount++;
        String cell = String.valueOf((char) ('A' + col)) + (row + 1);
        if (board[row][col] == 'X' || board[row][col] == 'o') {
            System.out.println("Уже стреляли в эту ячейку!");
        } else if (board[row][col] == 'U') {
            board[row][col] = 'X';
            markAdjacentCells(row, col);
            ships.remove(cell);
            System.out.println("Попадание!");
        } else {
            board[row][col] = 'o';
            System.out.println("Мимо!");
        }
    }

    private void markAdjacentCells(int row, int col) {
        for (int i = Math.max(0, row - 1); i <= Math.min(row + 1, BOARD_SIZE - 1); i++) {
            for (int j = Math.max(0, col - 1); j <= Math.min(col + 1, BOARD_SIZE - 1); j++) {
                if (board[i][j] == '.') {
                    board[i][j] = 'o';
                }
            }
        }
    }

    private void printResults() {
        System.out.println("Результаты игры:");
        System.out.println("Количество выстрелов: " + shotsCount);
    }

    public static void main(String[] args) {
        BattleshipGame game = new BattleshipGame();
        game.startGame();
    }
}
