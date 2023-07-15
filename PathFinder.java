
/**
 * PathFinder: this class utilizes recursion to find exits in a maze, which is given
 * to an instance of PathFinder as a 2D array
 * <p>
 * V2.1: takes first steps to fix StackOverflowError for mazes with large open areas
 * by creating a copy of the maze array
 * </p>
 * <p>
 * Feb 08: V2.2 Adds accessor methods for instance variables
 * V2.3 Fixes infinicursion by checking 'map' of maze via mazeCopy tile flipping to prevent
 * traversal of maze positions already checked.
 * </p>
 * <p>
 * Feb 12: V3.0 Removes redundancy and eliminates unecessary conditional statements
 * </p>
 * @author Maria Jackson
 * @version Feb 12, 2023 - Version 3.0
 */


public class PathFinder
{
    // instance variables
    private int currentRow; //current row index
    private int currentCol; //current column index
    public enum Direction{
        LEFT, RIGHT, UP, DOWN; //possible directions to move in maze
    }
    private Direction previousMove; //the position PathFinder was in before current
    private int[][] mazeCopy; //2D array copy of original maze
    
    /**
     * Constructor for objects of class PathFinder
     * 
     * @param rowIndex (int) - the current row index, path finder starting position
     * @param colIndex (int) - current column index, path finder starting position
     * @param dir (Direction) - the direction used to initiate maze traversal
     */
    public PathFinder(int rowIndex, int colIndex, Direction dir) {
        this.currentRow = rowIndex; 
        this.currentCol = colIndex;
        this.previousMove = dir;
    }
    
    //accessor methods:
    /**
     * get the current row value for PathFinder
     * @return currentRow - row index of current position
     */
    public int getCurrentRow() {
        return currentRow;
    }
    
    /**
     * get the current column value for PathFinder
     * @return currentCol - column index of current position
     */
    public int getCurrentCol() {
        return currentCol;
    }
    
    /**
     * get the previousMove value for PathFinder
     * @return previousMove - the direction traveled to get to current position
     */
    public Direction getPreviousMove() {
        return previousMove;
    }
    
    /**
     * makes copy of original maze array - sets new value to mazeCopy
     * @param originalMaze (int[][]) - the original 2D maze array to be copied
     */
    public void copyMaze(int[][] originalMaze) {
        //instantiate mazeCopy to match original maze row & col lengths
        mazeCopy = new int[originalMaze.length][originalMaze[0].length];
        //populate mazeCopy with values of original maze
        for (int rowIndex = 0; rowIndex < mazeCopy.length; rowIndex++) {
            for(int colIndex = 0; colIndex < mazeCopy[0].length; colIndex++) {
                mazeCopy[rowIndex][colIndex] = originalMaze[rowIndex][colIndex];
            }
        }
    }
    
    /**
     * Uses recursion to determine maze path from entrance to exit:
     * Each move in maze - indicated by direction 'cameFrom' - checks the other three
     * possible directions (e.g. DOWN, RIGHT, & LEFT) while ignoring the previous position.
     * <p>
     * Each move in maze 'flips' the previous position in mazeCopy to indicate that the
     * position has already been checked.
     * </p>
     * <p>
     * Each call to hasPath requires an update of both the current position (row, col) &
     * the direction moved to get to the current position. The potential exit position
     * (row, col) & original maze array remain the same .
     * </p>
     * 
     * @param currRow (int) - current row index
     * @param currCol (int) - current column index
     * @param exitRow (int) - exit row index
     * @param exitCol (int) - exit column index
     * @param maze2D (int[][]) - 2D array of maze values
     * @param cameFrom - direction of previous place in maze
     * @return boolean - true if current position is open (0) AND an open position has been
     * found in one of the neighboring positions. False if closed (1).
     */
    public boolean hasPath( int currRow, int currCol, 
                                int exitRow, int exitCol, 
                                int[][] maze2D, Direction cameFrom ) {
        int lastRow = maze2D.length - 1; //last row index of original maze
        int lastCol = maze2D[0].length - 1; //last column index of original maze
        
        //create copy of original maze
        if ( mazeCopy == null || (currRow == 0 && currCol == 1) ) {
            copyMaze(maze2D); //create new if traversal beginning from maze entrance (0,1)
        }
        
        //base case: exit is blocked off
        if (    //top row exit is blocked
                ( exitRow == 0 && maze2D[1][exitCol] == 1  ) || 
                //bottom row exit is blocked
                ( exitRow == lastRow && maze2D[exitRow - 1][exitCol] == 1 ) ||
                //first column exit is blocked
                ( exitCol == 0 && maze2D[exitRow][1] == 1 ) ||
                //last column exit is blocked
                ( exitCol == lastCol && maze2D[exitRow][exitCol - 1] == 1 )) {
            return false;
        }
        //base case: entrance is blocked off
        if ( maze2D[1][1] == 1 ) {
            return false;
        }
        //end case: no where left to go, made it to the exit!
        if ( currRow == exitRow && currCol == exitCol ) {
            return true;
        }
        //end case: reached out of range index for row or col without exit found
        if (currRow < 0 || currCol < 0 ) {
            return false;
        }
        if (currRow > lastRow || currCol > lastCol) {
            return false;
        }
        //base case: reached a previously checked open space
        if (mazeCopy[currRow][currCol] == 2 ) {
            return false;
        }
        //flip tile of previous space in mazeCopy
        switch ( cameFrom ) {
            case UP : if ( currRow != 0 ) {mazeCopy[currRow - 1][currCol] = 2;}
                break;
            case LEFT : mazeCopy[currRow][currCol - 1] = 2;
                break;
            case RIGHT : mazeCopy[currRow][currCol + 1] = 2;
                break;
            case DOWN : mazeCopy[currRow + 1][currCol] = 2;
                break;
        }
        // check current space open & check neighbors
        return maze2D[currRow][currCol] == 0 &&
               ( hasPath(currRow + 1, currCol, exitRow, exitCol,
                            maze2D, Direction.UP) ||
                    hasPath(currRow, currCol + 1, exitRow, exitCol,
                             maze2D, Direction.LEFT) ||
                    hasPath(currRow, currCol - 1, exitRow, exitCol,
                             maze2D, Direction.RIGHT) ||
                    hasPath(currRow - 1, currCol, exitRow, exitCol,
                            maze2D, Direction.DOWN) );
    }
}
