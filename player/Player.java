/**
 * Contains the abstract class Player which implements the ReversiPlayer
 * interface and implements some methods common to all Players.
 */
package player;

import reversi.*;

/**
 * This abstract class implements the interface ReversiPlayer. It offers
 * the method checkMove() which can be used to check a desired move's
 * conformity with the game rules. It also features the method initialize()
 * which is used by Arena to set the color and timeout parameters.
 * 
 * @see reversi.ReversiPlayer
 * @author Jonas Huber
 * @author Benedikt Koeppel
 */
public abstract class Player implements ReversiPlayer {

	/**
     * Color of the player.
     */
    protected int color = 0;
    /**
     * Color of the enemy.
     */
    protected int enemyColor = 0;
    
    /**
     * Timeout of the game.
     */
    protected long timeout = 0;
    
    /**
     * Constructor printing some info.
     */
    public Player() {
    	System.out.println("Instance of Player created...");
    } /* End of constructor Player(). */
    
    
    /**
     * Stores the color and the timeout-value in instance variables. This
     * method is called by {@link Arena} at the beginning of a game.
     * 
     * It also sets a global variable containing the color of our enemy.
     * 
     * @see reversi.ReversiPlayer
     */
    public void initialize(int color, long timeout)
    {
        this.color = color;
        if (color == GameBoard.RED)
        {
        	enemyColor = GameBoard.GREEN;
            System.out.println("HumanPlayer ist Spieler RED.");
        }
        else if (color == GameBoard.GREEN)
        {
        	enemyColor = GameBoard.RED;
            System.out.println("HumanPlayer ist Spieler GREEN.");
        }
        this.timeout = timeout;
    } /* End of initialize(). */
    
    
	/**
	 * This abstract method has to be implemented by each player!
	 * 
	 * @see reversi.ReversiPlayer#nextMove(reversi.GameBoard)
	 */
	abstract public Coordinates nextMove(GameBoard gb);
	
	
	/**
     * Checks whether a given move of this player is legal or not. Note that
     * "PASSEN" (move = null) is seen as an illegal move by this method!
     * 
     * @param move The coordinates where the move should be done.
     * @param gb The GameBoard on which we are playing.
     * @return true if the move is legal, false else.
     */
    protected boolean checkMove( Coordinates move, GameBoard gb ) {
    	
    	/* 
    	 * Passen ( move = null) cannot be a legal move from the point of view
    	 * of this method. This special case has to be handled *before* this
    	 * method is called!
    	 */
    	if ( move == null ) {
    		return false;
    	}
    	
    	/* 
    	 * gb.getPosition() throws an OutOfBoundsException if the coordinates
    	 * to check are not within the board's dimensions. We catch this 
    	 * exception and handle it by returning false as such a move can't
    	 * be legal. :-)
    	 */
    	try {
    		
    		/* The field where we want to put our stone must be empty.*/
    		if ( gb.getPosition( move ) != GameBoard.EMPTY ) {
    			return false;
    		}
    	} catch ( OutOfBoundsException e ) {
    		return false;
    	}
    	
    	/*
		 * Now we have to check if this moves leads to beating some of our
		 * adversaries stones... As soon as we find a correct situation in
		 * one direction the move is legal and we return true.
		 */
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				if ( findColorInDirection(color, move, i, j, gb) > 0 ) {
					return true;
				}
			}
		}

		/*
		 * No direction works, so the move is illegal.
		 */
		return false;
    } /* End of checkMove(). */
    
    
    /**
	 * This method searches the GameBoard in a given direction for the next
	 * appearance of a stone of the color specified (one of GameBoard.RED,
	 * GameBoard.GREEN, GameBoard.EMPTY).
	 * 
	 * @param color
	 *            One of GameBoard.RED or GameBoard.GREEN
	 * @param start
	 *            The coordinates of the starting point.
	 * @param xIncr
	 *            The per-move-increment in x-direction, should be -1, 0 or 1.
	 * @param yIncr
	 *            The per-move-increment in y-direction, should be -1, 0 or 1.
	 * @param gb
	 *            The gameboard on which we are performing all this.
	 * @return The number of enemy-stones until the next appearance in the
	 *         specified direction. 0 if the neighbor in this direction is of
	 *         the same color or if the color in this direction is not found
	 *         until the end of the gameboard in the given direction is reached.
	 */
    private int findColorInDirection( int c, Coordinates start, int xIncr, int yIncr, GameBoard gb ) {
    	
    	/* 
    	 * If xIncr and yIncr are zero, this makes no sense since we end up
    	 * in an endless loop at start.
    	 */
    	if ( xIncr == 0 && yIncr == 0 ) {
    		return 0;
    	}
    	
    	/* 
    	 * We have to catch an OutOfBoundsException thrown by gb.getPosition().
    	 */
    	try {
    		int y = start.getRow();// + yIncr;
    		int x = start.getCol();// + xIncr;
    		
    		/* 
    		 * This variable counts the distance to the next appearance of a
    		 * stone with our color. It's initially set to -1 because if a neighbor
    		 * of our stone has the same color, the distance should be zero.
    		 */
    		int distance = -1;
    		
    		Coordinates coord = new Coordinates( y, x );
    		
    		/* 
    		 * This loop adds the specific increment to the two coordinates
    		 * and breaks as soon as we're at a position with the desired
    		 * color *or* if we reach an EMPTY field and then we return the distance to that position.
    		 * 
    		 * In the loop condition we can get an OutOfBoundException if
    		 * we're at a position which isn't on the gameboard. If this is
    		 * the case, we haven't found the desired color and so we catch 
    		 * this exception by returning -1.
    		 * 
    		 * TODO: The while condition is kind of inefficient because it
    		 * performs the same operation as the if-statement in the loop.
    		 */
    		while ( gb.getPosition( coord ) != c ) {
    			x = x + xIncr;
    			y = y + yIncr;
    			distance++;

    			/* 
    			 * If the next field would be empty, the move cannot be legal since
    			 * no empty field is allowed to be in our path.
    			 */
    			coord = new Coordinates( y, x );
    			if ( gb.getPosition( coord )  == GameBoard.EMPTY ) {
    				return 0;
    			}
    		}
    		
    		return distance;
    		
    	} catch ( OutOfBoundsException e) {
    		/* 
    		 * This Exception means we reached the end of the gameboard in
    		 * our direction *without* finding a stone of the desired color,
    		 * so we return 0.
    		 */
    		return 0;
    	}
    } /* End of findColorInDirection(). */

    
    /**
     * Checks whether there exists a possible move for our color on the current
     * gameboard-situation.
     * 
     * @param gb The gameboard we're playing on.
     * @return true if at least one possible move exists, false else.
     */
    protected boolean possibleMoveExists( GameBoard gb ) {
    	/* 
    	 * We try if a move is possible for every field on the gameboard. As soon
    	 * as we find a possible move we abort and return true. If we reach the end
    	 * of the loops, there doesn't exist a valid move and so we return false.
    	 * 
    	 * TODO: I suppose that this is kind of inefficient... ;-)
    	 */
    	for ( int r=1; r<=gb.getSize(); r++ ) {
        	for ( int c=1; c<=gb.getSize(); c++ ) {
        		if ( checkMove( new Coordinates( r, c ), gb ) ) {
        			return true;
        		}
        	}
        }
        return false;
    } /* End of possibleMoveExists(). */
    
    
    /**
     * Counts, how many fields will be changed with this move
     * @param c Coordinates, which you want to play
     * @param gb Gameboard, on which we're playing
     * @return Returns how many fields will be changed when you're playing c
     */
    protected int getNumOfChanges( Coordinates c, GameBoard gb ) {
    	int numOfChanges = 0;
    	for (int i=-1; i<=1; i++) {
    		for (int j=-1; j<=1; j++) {
    			numOfChanges += findColorInDirection(color, c, i, j, gb);
    		}
    	}
    	return numOfChanges;
    }
    
    /**
     * Counts how many fields are still free
     * @param gb GameBoard on which you want to count free fields
     * @return how many fields are still free?
     */
    protected int getFreeFields(GameBoard gb) {
    	int freeFieldsCount=0;
    	for(int i=1; i<=gb.getSize(); i++) {
    		for(int j=1; j<=gb.getSize(); j++) {
    			try {
					if ( gb.getPosition(new Coordinates(i,j)) == GameBoard.EMPTY ) {
						freeFieldsCount++;
					}
				} catch (OutOfBoundsException e) {}
    		}
    	}
    	return freeFieldsCount;
    }
    
    /**
     * Checks, if the game 'gb' is already finished. That means, none of both player can move or the board is already full.
     * @param gb The gameboard to check
     * @return Returns true or false, depending if the game is already finished
     */
    protected boolean isGameFinished(TextGameBoard gb) {
    	// is gameboard full?
    	if ( gb.isFull() ) {
    		return true;
    	}
    	
    	// can't one of both player make a move?
    	if ( !( gb.isMoveAvailable(GameBoard.RED) || gb.isMoveAvailable(GameBoard.GREEN) ) ) {
    		return true;
    	}
    	
    	// else, the game isn't finished yet
    	return false;
    }
    
} /* End of abstract class Player. */