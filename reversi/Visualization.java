package reversi;

abstract class Visualization 
{
    /**
     * Init this instance of Visualization
     */
    abstract void init(Arena a);    
    
    /**
     * Animate a move by turning opponent's stones.
     */
    abstract void animateMove(GameBoard oldBoard, Coordinates move, int player,
            int[][] toFlip);

    /**
     * @param board
     * @param player
     */
    abstract public void showPossibleMoves(TextGameBoard board, int player);
        
    /**
     * @param text
     */
    abstract public void setInfoLine(String text);
    /**
     * @param text
     */
    abstract public void setInfoLine2(String text);
    /**
     * @param text
     */
    abstract public void setStatusLine(String text);

    /**
     * @param visible
     */
    abstract public void setVisible(boolean visible);
    /**
     * 
     */
    abstract public void dispose();

    /**
     * @param board
     */
    abstract public void update(TextGameBoard board);
}
