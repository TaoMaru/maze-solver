
/**
 * MazeSolver: This class is designed to find exits in text mazes.
 * Mazes are received as .txt file input from the user, the total number of
 * valid exits - routes from the start point to the outer walls of a maze - 
 * are displayed for the user as output with the original maze.
 * <p>
 * As of Feb 07: Uses PathFinder class with built in recursive path finder method
 * V2.3: Changes maze dimensions handling to capture larger integer values
 *</p>
 *<p>
 *As of Feb 08: V2.4 Uses PathFinder's accessors methods to initiate path search.
 *V2.5 Fixed issue of incorrectly initializing PathFinder search from 0 index when an
 * exit position at end of a row and stopped infinite recursion for some mazes!
 *</p>
 *
 * @author Maria Jackson
 * @version Feb 08, 2023 - Version 2.5
 */

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.NumberFormatException;
import java.util.Scanner;
import java.util.ArrayList;

public class MazeSolver
{
    // instance variables:
    private Scanner inputHandler;
    private String filename; //holds filename
    private boolean valid; //indicates input status: valid/invalid
    private BufferedReader fileHandler; //handles file input
    private String contentLine; //one line of file content
    private int[][] mazeContent2D; //2D array of maze contents by [row][col]
    private int exitCount; //number of exits found
    private int[] mDimensions; //size of the maze (num row, num col)
    private ArrayList<Integer> fileContents; //holds every char from file
    private PathFinder finder; //finds path from maze entrance to exit
    private ArrayList<String> exitList; //holds exit positions as "row, col"
    

    /**
     * Class constructor
     * Instantiates inputHandler, filename, exitCount, mDimensions, finder, & exitList
     * with default values.
     */
    public MazeSolver()
    {
        this.inputHandler = new Scanner(System.in);
        this.filename = "";
        this.exitCount = 0;
        this.mDimensions = new int[2];
        this.finder = new PathFinder(0, 1, PathFinder.Direction.UP);
        this.exitList = new ArrayList<String>();
    }

    /**
     * display simple greeting
     */
    public void greet() {
        System.out.println("\nHello! Welcome to the A-MAZE-ING Maze Solver!");
    }
    
    /**
     * get valid filename input: filename must have length > 4 chars, & have '.txt'
     * @return filename (Str)
     */
    public String getFilename() {
        valid = false; // status of user input filename
        
        do { 
            // request filename until valid ".txt" format received
            System.out.println("Please enter the maze filename: ");
            filename = inputHandler.nextLine();
            // check filename ends with ".txt"
            if ( filename.length() > 4 &&
                filename.substring(filename.length() - 4).equals(".txt") ){
                System.out.println("\nYou entered: " + filename);
                valid = true; // exit input validation
            }
            else {
                System.out.println("\nPlease check the filename and try again!");
            }
        }
        while ( !valid ); //valid input received
        
        return filename; //return valid filename
    }

    /**
     * read file from provided filename
     * throws FileNotFoundException, IOException: displays error message in each case,
     * exceptions at this stage prevent any other operations
     *
     * @param newFilename (Str) - name of file to read
     */
    public void readFile(String newFilename) throws IOException, FileNotFoundException
    {
        try {
            fileHandler = new BufferedReader( new FileReader( newFilename ) );
            System.out.println("\nPrepare to be A-MAZED..." + "\n");
            fileHandler.mark(0);
            contentLine = fileHandler.readLine();
            // grab num rows and num cols from first line of content
            mDimensions = pullDimensions(contentLine);
            setMazeContent2D( mDimensions ); 
            //print maze content
            int currentRow = 0; // serves as row index in 2D maze array
            contentLine = fileHandler.readLine(); // first line of maze
            while ( contentLine != null ) {
                System.out.println(contentLine);
                // fill row in 2D maze array
                translateMazeRow( contentLine, currentRow );
                contentLine = fileHandler.readLine(); // new line
                currentRow++; // increment row number
            }
            //System.out.println("End of file!");
        }
        catch (FileNotFoundException fnfException) {
            throw new FileNotFoundException("Sorry, we hit a roadblock." +
                        "\nPlease check the filename and try again.");
        }
        catch (IOException ioException) {
            throw new IOException("Oops! Something went awry..." +
                        "\nPlease check the filename and try again.");
        }
    }
    
    /**
     * pull maze dimensions from file line
     * @pre assumes first line in .txt files contain the dimensions of the maze
     * 
     * @param firstLine (Str)
     * @return dimensions (int[2])
     */
    public int[] pullDimensions(String firstLine) throws NumberFormatException{
        //store dimensions of 2D maze array as [numRows, numCols]
        int[] dimensions = new int[2]; //new array to hold values of numRow, numCol
        String[] tempDims = new String[2]; //holds String values of numRow, numCol
        tempDims = firstLine.split(" "); //grab the expected numbers from firstLine
        try {
        //take first term in first line, parse to int, this is num Rows
        dimensions[0] = Integer.parseInt( tempDims[0] );
        //take 2nd term in line, parse to int, this is num Cols
        dimensions[1] = Integer.parseInt( tempDims[1] );
        }
        catch (NumberFormatException noNumbers) {
            System.out.println("\nWe encountered a problem calculating the maze "
                    + "dimensions. \nRedirecting...");
            System.out.println("Dims are: " + dimensions[0] + " by " + dimensions[1]);
            return dimensions;
        }
        return dimensions; //return [rows, cols] dimensions
    }
    
    /**
     * accessor method for mazeContent2D, instance variable holding maze contents
     * set 2D array with dimensions from maze file
     * 
     * @param mazeDimensions (int[2])
     */
    public void setMazeContent2D(int[] mazeDimensions) {
        //instantiate mazeContent 2D using dimensions [numRows, numCols]
        mazeContent2D = new int[mazeDimensions[0]][mazeDimensions[1]];
    }
    
    /**
     * translate file content into numeric 2D array values for one row in maze
     * white spaces - ' 's - translate to 0s, '#'s translate to 1s
     * sets values in mazeContent2D as [currentRow][correspondingCol]
     * 
     * @param mazeRow (Str)
     * @paramm rowNum (int)
     */
    public void translateMazeRow(String mazeRow, int rowNum) {
        //work across row to set values: e.g. moves from 0, 1 to 0, 2 to 0, 3...
        for (int index = 0; index < mazeRow.length() - 1; index++) {
            if (mazeRow.substring(index, index + 1).equals("#") ) {
                mazeContent2D[rowNum][index] = 1; //'#' --> 1
            }
            else {
                mazeContent2D[rowNum][index] = 0; //' ' --> 0
            }
        }
        //set the value at last point of the row
        if (mazeRow.substring(mazeRow.length() - 1).equals("#") ) {
            mazeContent2D[rowNum][mazeRow.length() - 1] = 1; //'#' --> 1
        }
        else {
            mazeContent2D[rowNum][mazeRow.length() - 1] = 0; //' ' --> 0
        }
    }
    
    /**
     * find valid exits in maze: open spaces indicated by value of 0 in maze array
     * with a path leading from maze start point are counted as valid exits
     * 
     * @return numExits (int) - total number of valid exits in maze
     */
    public int findExits() {
        int numExits = 0; //initiate exit count
        for(int colIndex = 0; colIndex < mazeContent2D[0].length; colIndex++) {
            //work across top row (0), ignoring start point (0, 1)
            if ( mazeContent2D[0][colIndex] == 0 && colIndex != 1 ) {
                //validate open edge, add to count if valid exit
                if( finder.hasPath(finder.getCurrentRow(), finder.getCurrentCol(), 
                        0, colIndex, mazeContent2D, finder.getPreviousMove()) ) {
                            numExits++; //add exit to count
                            //add exit position to exitList collection
                            exitList.add("1," + (colIndex + 1) );
                        }
            }
            //work across bottom row (numrows - 1)
            if ( mazeContent2D[mazeContent2D.length - 1][colIndex] == 0 ){
                //validate open edge, add to count if valid exit
                if ( finder.hasPath(finder.getCurrentRow(), finder.getCurrentCol(), 
                        mazeContent2D.length - 1, colIndex,mazeContent2D, 
                        finder.getPreviousMove()) ){
                            numExits++; //add exit to count
                            //add exit position to exitList collection
                            exitList.add(mazeContent2D.length + "," + (colIndex+1));
                        }
            }
        }
        for(int rowIndex = 1; rowIndex < mazeContent2D.length - 1; rowIndex++) {
            //check 1st and last positions of rows in between 1st and last
            if ( mazeContent2D[rowIndex][0] == 0 ) {
                //validate open edge, add to count if valid exit
                if (finder.hasPath(finder.getCurrentRow(), finder.getCurrentCol(), 
                        rowIndex, 0, mazeContent2D, finder.getPreviousMove()) ) {
                            numExits++; //add exit to count
                            //add exit position to exitList collection
                            exitList.add((rowIndex + 1) + "," + 1);
                        }
            }
            if ( mazeContent2D[rowIndex][mazeContent2D[0].length - 1] == 0) {
                //validate open edge, add to count if valid exit
                if (finder.hasPath(finder.getCurrentRow(), finder.getCurrentCol(), 
                        rowIndex, mazeContent2D[0].length - 1, mazeContent2D, 
                        finder.getPreviousMove()) ) {
                            numExits++; //add exit to count
                            //add exit position to exitList collection
                            exitList.add((rowIndex + 1) + "," + mazeContent2D[0].length);
                        }
            }
        }
        return numExits; //return the number of exits found
    }
    
    /**
     * display simple goodbye message
     */
    public void sayBye() {
        System.out.println("\nBye! Have an A-MAZE-ING day! n_n");
    }
    
    public static void main(String[] args) {
        MazeSolver amaze = new MazeSolver(); //create to MazeSolver object
        amaze.greet(); //say hi
        try {
        amaze.readFile( amaze.getFilename() ); //get valid filename & read file
        } catch (IOException iOException) {
            System.out.println("There was trouble reading the file. Try again.");
        }
        //find open edges only if mazeContent has been instantiated and populated
        if ( amaze.mazeContent2D != null &&
                amaze.mazeContent2D.length > 0 ) {
            int foundExits = amaze.findExits(); //find & print number of exits
            if (foundExits > 0 ) {
                System.out.println("\nFound " + foundExits + " exit(s) at the " +
                        " following positions: "); //indicate exit(s) was/were found
                for( String exitPos : amaze.exitList ) {
                    System.out.println(exitPos); //print exit position (row, col)
                }
            }
            else {
                System.out.println("\nUnsolvable!"); //indicate no exits were found
            }
        }
        amaze.sayBye(); // say bye
    }
}
