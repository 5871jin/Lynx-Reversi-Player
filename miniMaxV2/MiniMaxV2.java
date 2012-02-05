/**
 * This package is all about miniMax player version 2
 */
package miniMaxV2;

import reversi.*;
import lookOneAheadPlayer.LookOneAheadPlayer;

/**
 * This player implements a method to evaluate a game situation. It tries to
 * find the best move by simulating each possible move and then evaluating the
 * resulting situation.
 * @author Jonas Huber
 * @author Benedikt Koeppel
 *
 */
public class MiniMaxV2 extends LookOneAheadPlayer {

	/*
	 * Some parameters.
	 * 
	 * Don't forget the changeToMaxStrategyThreshold in super!
	 */
	private final int MAX_DEPTH = 6;		/** The maximum depth we go down the tree. */
	private final int ABORT_TIME = 100;		/** The minium number of ms that have to be left before we abort. */
	
	/*
	 * Global variables.
	 */
	private long startTime = 0;
	
	/**
	 * This player makes its move by performing a minimax-search down to a
	 * specified depth. It also tries to stay within the given time limit.
	 * 
	 * @param gb The Gameboard on which we are playing.
	 * @see lookOneAheadPlayer.LookOneAheadPlayer#nextMove(reversi.GameBoard)
	 */
	public Coordinates nextMove( GameBoard gb ) {
		
		/* 
		 * The first thing we do, is to remember, when the this move
		 * has started, so that we can track the elapsed time.
		 */
		startTime = System.currentTimeMillis();
		
		/* Does a move exist? If not we do "passen"... */
		if ( !possibleMoveExists( gb ) ) {
			return null;
		}
		
		/* Variables used. */
		Coordinates coord = null;
		Coordinates bestCoord = null;
		int eval = Integer.MIN_VALUE;
		int bestEval = Integer.MIN_VALUE;
		
		/* We store the current situation in a TextGameBoard. */
		TextGameBoard boardAfterNextMove = new TextGameBoard( gb );
		
		/*
		 * Iteration through every field on the board.
		 */
		for ( int row = 1; row <= gb.getSize(); row++ ) {
			for ( int col = 1; col <= gb.getSize(); col++ ) {
				
				coord = new Coordinates( row, col );
				
				/* Setting the back to the initial situation... */
				boardAfterNextMove.updateBoard( gb );
				/* 
				 * If a move to this field is possible, we're going to 
				 * build and evaluate the resulting MiniMax-tree.
				 */
				
				if ( boardAfterNextMove.checkMove(color, coord) ) {
					
					
					boardAfterNextMove.makeMove( color, coord );
					eval = Math.max( eval, -miniMax( color, boardAfterNextMove, 1 ) );
					
					System.out.println("(II) Checked move (" + coord.getRow() + "," + coord.getCol() + "): eval=" + eval);
				
					/* If this move is better than a previous one, we choose this move. */
					
					if ( eval > bestEval ) {
						bestCoord = coord;
						bestEval = eval;
					}
				}
			}
		}
		
		if ( bestCoord == null ) {
			System.out.println( "(EE) Will return null, but bestEval = " + bestEval);
		}
		
		System.out.println("(II) Chose move (" + bestCoord.getRow() + "," + bestCoord.getCol() + ") which has eval=" + bestEval + ".");
		return bestCoord;
		
	} /* End of nextMove(). */
	
	/**
	 * This function performs a minimax search starting with a given situation, which
	 * means a gameboard and the information, which player has made the *previous* move!
	 * 
	 * @param color The color of the player who made the *previous* move!
	 * @param tgb The gameboard with the situation to evaluate.
	 * @param depth The current depth in the tree.
	 * @return
	 */
	private int miniMax( int colorBefore, TextGameBoard tgb, int depth ) {
		
		int result;
		int nextColor;
		Coordinates coord;

		/*
		 * Determine the color of the next player.
		 */
		if ( colorBefore == GameBoard.RED ) {
			nextColor = GameBoard.GREEN;
		} else {
			nextColor = GameBoard.RED;
		}
		
		/*
		 * TODO: Is here the place to check if enough time is left?
		 */
		if ( !timeOK() ) {
			System.out.println("(WW) Runnning out of time...");

			return evaluate( color, tgb );

		}
		
		/*
		 * We have also to check if the game is over and if yes, who has won!
		 */

		if ( isGameFinished( tgb ) ) {
			if ( tgb.countStones( color ) > 32 ) {
				System.out.println("(II) Found winning situation!");
				return Integer.MAX_VALUE - 1;
			} else if (tgb.countStones( enemyColor ) > 32 ){
				System.out.println("(II) Found loosing situation!");
				return Integer.MIN_VALUE + 1;
			} 
		}
		
		/*
		 * If we have reached the maximum depth we evaluate this situation and return.
		 */
		if ( depth >= MAX_DEPTH ) {

			return evaluate( color, tgb );

		}
		
		/* Initialize a gameboard with the current situation. */
		TextGameBoard boardAfterNextMove = new TextGameBoard( tgb );

		/* 
		 * Set the result initially to -infty. Note the "+1" which is necessary because if this is
		 * our only move, it has to be taken in nextMove().
		 */
		result = Integer.MIN_VALUE + 1;

		/* Now we iterate over all the fields while searching for possible moves. */
		for ( int row = 1; row <= tgb.getSize(); row++ ) {
			for ( int col = 1; col <= tgb.getSize(); col++ ) {
				
				coord = new Coordinates( row, col );
				
				/* Resetting the board. */
				boardAfterNextMove.updateBoard( tgb );
				
				/*
				 * If the move is possible, we do the move and thereby check the turn
				 * of which player it is.
				 * (See English Wikipedia!)
				 */
				if ( boardAfterNextMove.checkMove( nextColor, coord ) ) {
					
					boardAfterNextMove.makeMove( nextColor, coord );
					result = Math.max( result, -miniMax( nextColor, boardAfterNextMove, depth + 1 ) );
				}
				
			}
		} /* End of iteration over all fields. */
		
		return result;
	} /* End of miniMax(). */
	
	/**
	 * Checks whether we are still within the timeout limit and didn't yet 
	 * violate our saftey-zone...
	 * 
	 * @return true if ok, false else.
	 */
	private boolean timeOK() {
		long currentTime = System.currentTimeMillis();
		
		if ( timeout - (currentTime - startTime) <= ABORT_TIME ) {
			return false;
		} else {
			return true;
		}
	} /* End of timeOK(). */
	
} /* End of class MiniMaxV2. */
