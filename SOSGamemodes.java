package sprintPackage;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Random;



public class SOSGamemodes {
    public int TOTALROWS;
    public int TOTALCOLUMNS;

    public enum Cell { // board layout
        EMPTY, S, O }

    protected Cell[][] grid;
    protected char turn;
    protected int x, y;
    protected int blueScore, redScore;
    protected ArrayList<ArrayList<Integer>> sosInfo;

    public enum GameType { // to see if simple or general game is chosen
        Simple, General }

    protected GameType currentGameType;

    public enum GameState { // options in game (win or draw)
        PLAYING, DRAW, BLUE_WON, RED_WON, IN_PROGRESS  }

    protected GameState currentGameState;

    public SOSGamemodes(int n) {
        currentGameType = GameType.Simple;
        grid = new Cell[n][n];
        TOTALROWS = TOTALCOLUMNS = n;
        startGame(); }

    private void startGame() { // run game initialization
        for (int row = 0; row < TOTALROWS; ++row) {
            for (int col = 0; col < TOTALCOLUMNS; ++col) {
                grid[row][col] = Cell.EMPTY;  } }
        
        currentGameState = GameState.PLAYING;
        turn = 'B';
        blueScore = redScore = 0;
        sosInfo = new ArrayList<ArrayList<Integer>>(); }

    public Cell getCell(int row, int column) {
        if (row >= 0 && row < TOTALROWS && column >= 0 && column < TOTALCOLUMNS) {
            return grid[row][column];
        } else {
            return null; } }

    public void writeLine(String str) {
        try {
            FileOutputStream o = new FileOutputStream(new File("record.txt"), true);
            o.write(str.getBytes("GBK"));
            o.close();
        } catch (Exception e) {
            e.printStackTrace(); } }

    public void makeMove(int row, int column, int type) { // make move according to player's selection
        if (row >= 0 && row < TOTALROWS && column >= 0 && column < TOTALCOLUMNS && grid[row][column] == Cell.EMPTY) {
            x = row;
            y = column;
            grid[row][column] = (type == 0) ? Cell.S : Cell.O;
            sosInfo.add(checkSOS());
            updateGameState();
            String input = (type == 0) ? "S" : "O";
            String tu = (turn == 'B') ? "Red" : "Blue";
            writeLine(tu + ": (" + row + ", " + column + ") -> " + input + "\n");
            turn = (turn == 'B') ? 'R' : 'B'; } }

    public int getNumberOfEmptyCells() {
        int numberOfEmptyCells = 0;
        for (int row = 0; row < TOTALROWS; ++row) {
            for (int col = 0; col < TOTALCOLUMNS; ++col) {
                if (grid[row][col] == Cell.EMPTY) {
                    numberOfEmptyCells++; } } }
        return numberOfEmptyCells; }

    public ArrayList<Integer> checkSOS() { // check grid for blue or red SOS's
        ArrayList<Integer> res = new ArrayList<Integer>();
        if (turn == 'B')
            res.add(0);
        else
            res.add(1);
        boolean isChanged = false;
        if (grid[x][y] == Cell.O) {
            for (int i = -1; i < 2; i++) {
                for (int j = -1; j < 2; j++) {
                    if (x - i < 0 || x - i >= TOTALROWS || x + i < 0 || x + i >= TOTALROWS || y - j < 0
                            || y - j >= TOTALCOLUMNS || y + j < 0 || y + j >= TOTALCOLUMNS)
                        continue;
                    if (grid[x - i][y - j] == Cell.S && grid[x + i][y + j] == Cell.S) {
                        res.add(x - i);
                        res.add(y - j);
                        res.add(x + i);
                        res.add(y + j);
                        if (turn == 'B')
                            blueScore++;
                        else
                            redScore++;
                        isChanged = true; } } } } 
        else {
            for (int i = -1; i < 2; i++) {
                for (int j = -1; j < 2; j++) {
                    if (x + 2 * i < 0 || x + 2 * i >= TOTALROWS || y + 2 * j < 0 || y + 2 * j >= TOTALCOLUMNS)
                        continue;
                    if (grid[x + 2 * i][y + 2 * j] == Cell.S && grid[x + i][y + j] == Cell.O) {
                        res.add(x);
                        res.add(y);
                        res.add(x + 2 * i);
                        res.add(y + 2 * j);
                        if (turn == 'B')
                            blueScore++;
                        else
                            redScore++;
                        isChanged = true; } } } }
        if (isChanged)
            turn = (turn == 'B') ? 'R' : 'B';
        return res; }

    private void updateGameState() {
        int x = gameWinner();
        if (x > 0) { // check for win
            if (x == 1)
                currentGameState = GameState.BLUE_WON;
            else if (x == 2)
                currentGameState = GameState.RED_WON;
            else if (x == 3)
                currentGameState = GameState.DRAW; } }

    private boolean isFull() {
        for (int row = 0; row < TOTALROWS; ++row) {
            for (int col = 0; col < TOTALCOLUMNS; ++col) {
                if (grid[row][col] == Cell.EMPTY) {
                    return false; } } }
        return true; }

    private int gameWinner() { // see who won the game
        if (currentGameType == GameType.Simple) {
            if (blueScore > 0)
                return 1;
            if (redScore > 0)
                return 2;
            if(isFull() && ((blueScore & redScore) == 0))
            	return 3;
            return 0;
        } else {
            if (!isFull())
                return 0;
            if (blueScore > redScore)
                return 1;
            else if (blueScore < redScore)
                return 2;
            else
                return 3; } }
    
    public void resetGame() { // reset function to start new game 
        startGame(); }

    public int getTotalRows() { // getter for rows
        return TOTALROWS; }
    
    public char getTurn() { // get player's turn
        return turn;  }


    public int getTotalColumns() { // getter for columns
        return TOTALCOLUMNS; }

    public GameType getCurrentGameType() { // get current game type
        return currentGameType; }

    public void setCurrentGameType(GameType currentGameType) { // set current game type
        this.currentGameType = currentGameType; }

    public ArrayList<ArrayList<Integer>> getSosInfo() {
        return sosInfo; }

    public GameState getGameState() { // return results to decide winner
        return currentGameState;}
}
