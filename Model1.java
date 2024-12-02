package MVCGUI;

import java.io.Serializable;

public class Model1 implements Serializable {
    private static final long serialVersionUID = -7917648974566118675L;
    private int[][] grid; // 2D array to represent the grid
    private int rows, columns; // Grid dimensions
    private boolean wrap = false; // Wrap functionality flag

    public Model1(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.grid = new int[rows][columns]; // Initialize the grid with given dimensions
    }

    public void setCell(int row, int col, int value) {
        if (row >= 0 && row < rows && col >= 0 && col < columns) {
            grid[row][col] = value;
        }
    }

    // Getter methods
    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public int[][] getGrid() {
        return grid;
    }

    public void toggleWrap(boolean wrap) {
        this.wrap = wrap;
    }

    public void updateGrid() {
        int[][] newGrid = new int[rows][columns];

        // Apply the Game of Life rules
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                int aliveNeighbors = countAliveNeighbors(i, j);
                if (grid[i][j] == 1) {
                    // Any live cell with two or three live neighbors survives
                    if (aliveNeighbors == 2 || aliveNeighbors == 3) {
                        newGrid[i][j] = 1;
                    } else {
                        newGrid[i][j] = 0;
                    }
                } else {
                    // Any dead cell with exactly three live neighbors becomes alive
                    if (aliveNeighbors == 3) {
                        newGrid[i][j] = 1;
                    }
                }
            }
        }

        // Copy the new grid back to the current grid
        grid = newGrid;
    }

    // Modified countAliveNeighbors method
    private int countAliveNeighbors(int x, int y) {
        int alive = 0;

        // Check all 8 neighboring cells
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                // Skip the center cell (x, y) itself
                if (i == 0 && j == 0) continue;

                // If wrap is enabled, check neighbors by wrapping
                if (wrap) {
                    int neighborX = (x + i + rows) % rows; // Wrap around rows
                    int neighborY = (y + j + columns) % columns; // Wrap around columns
                    alive += grid[neighborX][neighborY];
                } else {
                    // If wrap is not enabled, check within bounds of the grid
                    if (x + i >= 0 && x + i < rows && y + j >= 0 && y + j < columns) {
                        alive += grid[x + i][y + j];
                    }
                }
            }
        }

        return alive;
    }
}

