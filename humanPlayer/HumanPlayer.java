package humanPlayer;

import java.io.*;
import reversi.*;
import player.*;

/**
 * HumanPlayer extends the abstract class Player (and implements the Interface
 * ReversiPlayer). Instead of calculating a move it asks the user to enter some
 * coordinates.
 * 
 * @see reversi.ReversiPlayer
 * @see player.Player
 * @author chfrank
 * @author Jonas Huber
 * @author Benedikt Koeppel
 */
public class HumanPlayer extends Player {
	
    /**
     * Constant that can be entered by the user in order to do "PASSEN".
     */
    private final static String PASSEN = "p";
    
    
    /**
     * Constructor printing some info...
     */
    public HumanPlayer() {
    	System.out.println("Instance of HumanPlayer created...");
    }
    
    
    /**
     * Makes a move for a HumanPlayer by asking the user to enter a move. This
     * method is called by {@link reversi.Arena} if it's this player's turn to
     * make a move.
     * 
     * @see reversi.ReversiPlayer
     * @return The move of this player.
     */
    public Coordinates nextMove(GameBoard gb) {

        Coordinates coord = null;

        System.out.print("HumanPlayer ");
        if (color == GameBoard.RED)
        {
            System.out.print("(RED)");
        }
        else if (color == GameBoard.GREEN)
        {
            System.out.print("(GREEN)");
        }
        
        /* 
         * Check whether there exist possible moves. If no, we do "PASSEN", meaning
         * we return null. 
         */
        if ( !possibleMoveExists( gb ) ) {
        	return null;
        }
        
        /* 
         * Prompt the user for coordinates. 
         */
        System.out.print(", gib deinen Zug ein (passen mit '" + PASSEN + "'): ");
        coord = readMoveFromKeyboard();

        /* 
         * TODO: What is IllegalMoveException and could it be used in this
         * context? 
         */
        
        /*
         * Now we have to check if the coordinates are legal.
         */
        while ( !checkMove( coord, gb ) ) {
        	System.out.print("ILLEGAL MOVE: Please enter another move: ");
        	coord = readMoveFromKeyboard();
        }
        
        return coord;
    } /* End of nextMove(). */
    

    /**
     * Reads a move from command line.
     * Valid input is a pair of coordinates (e.g. '6d') or 'p' to pass this move (if no move is possible).
     * This method repeats the prompt, as long as the user hasn't entered any valid coordinates.
     * @return Returns the read coordinates, or <code>null</code> if the user has chosen to pass this move.
     */
    public static Coordinates readMoveFromKeyboard() {
        
    	Coordinates result = null;
        while (result == null)
        {
            System.out.print(">");
            String str = null;
            int row = 0, column = 0;

            /*
             * Buffer to read from standard input
             */
            BufferedReader d = new BufferedReader(new InputStreamReader(System.in));

            /*
             * read string from input
             */
            try
            {
                str = d.readLine();
            }
            catch (IOException e)
            {
            	/*
            	 * TODO: probably it is better to do 'continue' here, so the user can re-enter new coordinates
            	 */
                e.printStackTrace();
            }

            /*
             * start to parse string
             */
            str.trim();
            str = str.toLowerCase();

            /*
             * if user has chossen "PASSEN", return null
             */
            if (str.equals(PASSEN))
            {
                System.out.println("Zug 'PASSEN' wurde ausgewaehlt.");
                break;
            }

            if (str.length() != 2)
            {
                System.out.println("Ungueltige Eingabe: mehr als 2 Zeichen.");
                continue;
            }

            /*
             * is the first character a number between 0 and 8?
             */
            row = (int) (str.charAt(0)) - (int) '0';

            /*
             * rows 9 and 0 are not valid
             */
            if (row < 1 || row > 8)
            {
                System.out.println("Ungueltige Eingabe: die Zeilennummer muss "
                        + "zwischen 1 und 8 liegen.");
                continue;
            }

            /*
             * is the second character a character between a and h?
             */
            column = (int) (str.charAt(1)) - (int) 'a' + 1;

            /*
             * other characters are not valid.
             */
            if (column < 1 || column > 8)
            {
                System.out.println("Ungueltige Eingabe: die Spaltenummer muss "
                        + "zwischen A und H liegen.");
                continue;
            }
            
            /*
             * if the coordinates are valid, create a new Coordinates-object
             */
            result = new Coordinates(row, column);
        }
        return result;
    } /* End of readMoveFromKeyboard(). */
    
} /* End of class HumanPlayer. */