package reversi;


/**
 * This class holds the scoring of a single game.
 */
class GameState
{
    /**
     * 
     */
    public final static int RESULT_DRAW_GAME  = 1;
    /**
     * 
     */
    public final static int RESULT_RED_WINS   = 2;
    /**
     * 
     */
    public final static int RESULT_GREEN_WINS = 3;
    
    /**
     * 
     */
    public final static int FINISH_REGULAR = 0;
    /**
     * 
     */
    public final static int FINISH_ABORTED = 1;
    /**
     * 
     */
    public final static int FINISH_CHEATED = 2;

    /**
     * 
     */
    public final static int CHEATED_ILLEGAL_MOVE  = 1;
    /**
     * 
     */
    public final static int CHEATED_TIME_EXCEEDED = 2;
    
    private int redStones   = 0;
    private int greenStones = 0;
    private int result = 0;
    private int finish = FINISH_ABORTED;
    private int cheat = 0;
    

    /**
     * 
     */
    public GameState()
    {
        // just a dummy constructor
    }
    
    /**
     * @param red
     * @param green
     */
    public void regularFinish( int red, int green )
    {
        redStones   = red;
        greenStones = green;
        
        if (greenStones == redStones)
        {
            result = RESULT_DRAW_GAME;
        }
        else if (redStones < greenStones)
        {
            result = RESULT_GREEN_WINS;
        }
        else
        {
            result = RESULT_RED_WINS;
        }
        
        finish = FINISH_REGULAR; // regular finish
    }   
    
    /**
     * @param currentPlayer
     * @param reason
     * @param red
     * @param green
     */
    public void cheatedFinish( int currentPlayer, int reason, int red, int green )
    {
        if ( reason != CHEATED_ILLEGAL_MOVE && reason != CHEATED_TIME_EXCEEDED )
        {
            throw new IllegalArgumentException( "Invalid cheat reason!" );
        }
        
        redStones   = red;
        greenStones = green;
        
        if (currentPlayer == GameBoard.RED)
        {
            result = RESULT_GREEN_WINS;
        }
        else if (currentPlayer == GameBoard.GREEN)
        {
            result = RESULT_RED_WINS;
        }
        else
        {
            throw new IllegalArgumentException( "Player has to be RED OR GREEN!");
        }

        finish = FINISH_CHEATED; // finished with cheat
        cheat  = reason; 
    }
    
    /**
     * @return
     */
    public int getRedStones()
    {
        return redStones;
    }
    
    /**
     * @return
     */
    public int getGreenStones()
    {
        return greenStones;
    }   
    
    /**
     * @return
     */
    public int getResult()
    {
        return result;
    }
    
    public String toString()
    {
        String msg = "GameState: ";
        if ( finish == FINISH_ABORTED ) 
        {
            msg += "Game aborted";
        }
        else if ( finish == FINISH_REGULAR )
        {
            if ( result == RESULT_RED_WINS )
            {
                msg += "RED wins";
            }           
            else if ( result == RESULT_GREEN_WINS )
            {
                msg += "GREEN wins";
            }
            else
            {
                msg += "DRAW game.";
            }
        }
        else if ( finish == FINISH_CHEATED )
        {
            if ( result == RESULT_RED_WINS )
            {
                msg += "GREEN cheated -> RED wins, ";
                if ( cheat == CHEATED_ILLEGAL_MOVE )
                {
                    msg += "GREEN makes illegal move";
                }
                else if ( cheat == CHEATED_TIME_EXCEEDED )
                {
                    msg += "GREEN exceeds time limit";
                }               
            }           
            else if ( result == RESULT_GREEN_WINS )
            {
                msg += "RED cheated -> GREEN wins, ";
                if ( cheat == CHEATED_ILLEGAL_MOVE )
                {
                    msg += "RED makes illegal move";
                }
                else if ( cheat == CHEATED_TIME_EXCEEDED )
                {
                    msg += "RED exceeds time limit";
                }               
            }           
        }
        
        msg += " (RED vs. GREEN = " + redStones + ":" + greenStones + ")";
        
        
        return msg;  
    }
    
    /**
     * @return
     */
    public int getFinish()
    {
        return finish;
    }
}
