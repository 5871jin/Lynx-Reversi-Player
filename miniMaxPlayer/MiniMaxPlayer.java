/**
 * This contains the player LookOneAheadPlayer.
 * run this with sth like 'java reversi.Arena MyGame lookOneAheadPlayer.LookOneAheadPlayer humanPlayer.HumanPlayer'
 */
package miniMaxPlayer;

import lookOneAheadPlayer.LookOneAheadPlayer;
import reversi.Coordinates;
import reversi.GameBoard;
import reversi.TextGameBoard;

/**
 * This player implements a method to evaluate a game situation. It tries to
 * find the best move by simulating each possible move and then evaluating the
 * resulting situation.
 * 
 * @author Jonas Huber
 * @author Benedikt Koeppel
 */
public class MiniMaxPlayer extends LookOneAheadPlayer {

	/**
	 * how many fields have to be free (maximum) to change to Maximum-Strategy
	 * 
	 * TODO: Experimented a bit and found 20 to be a good value... ;-)
	 */
	protected int changeToMaxStrategyThreshold = 20;
	
	/**
	 * how deep does minimax go?
	 * 
	 * TODO: find a value here, which is good :-D
	 */
	protected int maximumDepth = 6;
	
	/**
	 * how much time has to be left, bevore we stop minimax?
	 * => we don't want to get disqualified by exceeding the timelimit
	 * abortTime in Milliseconds
	 */
	protected int abortTime = 1000;
	
	/**
	 * at this time, the current move started
	 */
	protected long starttime=0;

	
	/**
	 * This method looks one move ahead and then makes the move which has
	 * produced the simulation of *one* move ahead in the game.
	 * 
	 * @param gb The current gameboard.
	 * @return The coordinates of our move.
	 */
	public Coordinates nextMove(GameBoard gb) {
		starttime = System.currentTimeMillis();
		
		/* Does a move exist? */
		if ( !possibleMoveExists( gb ) ) {
			return null;
		}
		
		/* Temporary variable. */
		Coordinates coord = null;
		int eval = 0;
		
		/* 
		 * This two variables store the best evaluation result gained so far and
		 * also the coordinates of the move that led to this result.
		 */
		Coordinates bestCoord = null;
		int bestEval = Integer.MIN_VALUE;
		
		/* Storing the current situation in a new object. */
		TextGameBoard boardAfterNextMove = new TextGameBoard( gb );
		
		/*
		 * First we search for each possible move and then play that move on
		 * a *copy* of the current gameboard. After that we evaluate the new
		 * situation on the gameboard and chose the best to decide which move
		 * to play.
		 */
		for ( int row = 1; row <= gb.getSize(); row++ ) {
			for ( int col = 1; col <= gb.getSize(); col++ ) {
				
				coord = new Coordinates( row, col );
				
				/* 
				 * We set the board on which we want to test our move back to
				 * the initial position.
				 */
				boardAfterNextMove.updateBoard( gb );
				
				if ( boardAfterNextMove.checkMove( color, coord ) ) {
					/*
					 * We set the board on which we want to test our move back to
					 * the initial position and then make our move. Then we evaluate
					 * the resulting situation on the board.
					 */
					boardAfterNextMove.makeMove( color, coord );
					eval = minimax( color, boardAfterNextMove, 1 );
					
					/* Is this move better than another we tried before? */
					if ( eval > bestEval ) {
						bestCoord = coord;
						bestEval = eval;
					}
					
				} /* End if. */
			} /* End for. */
		} /* End for. */
		
		if ( bestCoord == null ) {
			System.out.println( "(EE) Will return null, but bestEval = " + bestEval);
			/*
			 * TODO: try to recover, if we haven't found any "best move"
			 * TODO: this is ugly and shouldn't happen! :-(
			 * return the first coordinates which are valid
			 */
			for (int i=1; i<=gb.getSize(); i++) {
				for (int j=1; j<=gb.getSize(); j++) {
					coord = new Coordinates(i,j);
					if ( checkMove(coord, gb) ) {
						return coord;
					}
				}
			}
			System.out.println("This shouldn't happen! There was no 'bestMove' found, and returned the first possible move!");
			
		}

		/* Now we make the best move that we've found. */
		return bestCoord;
		
	} /* End of method nextMove(). */

	/**
	 * Evaluates the gameboard with minimax-algorithm
	 * @param color The color of the player we're interested in.
	 * @param boardAfterNextMove The TextGameBoard to evaluate.
	 * @return Returns the quality of the board, so that we can decide which move is the best
	 */
	private int minimax(int color, TextGameBoard boardAfterNextMove, int depth) {
		int result;
		
		/*
		 * who's the next player?
		 */
		int nextcolor;
		if ( color==GameBoard.RED ) {
			nextcolor = GameBoard.GREEN;
		} else {
			nextcolor = GameBoard.RED;
		}
		
		/*
		 * if the game is already finished, or we've reached the maximum depth, or we're running out of time
		 * then we evaluate the actual board
		 */
		System.out.println("(II) Max depth: " + maximumDepth + " Current depth: " + depth );
		if ( depth>=maximumDepth ) {
			System.out.println("(II) Reached the maximum depth of recursion. Max depth: " + maximumDepth + " Current depth: " + depth );
			return evaluate(color, boardAfterNextMove);
		}
		if ( getTimeLeft()<abortTime ) {
			System.out.println("(WW) Low on time!!!");
			return evaluate(color, boardAfterNextMove);
		}
		if ( isGameFinished(boardAfterNextMove) ) {
			/*
			 * TODO: check here, if we win and return Integer.MAX_VALUE
			 */
			return evaluate(color, boardAfterNextMove);
		}
		
		/*
		 * else, we go deeper with MiniMax-algorithm
		 */
		// take MIN_VALUE instead of  -infinity
		result = Integer.MIN_VALUE;
		
		
		/*
		 * for each possible coordinate in boardAfterNextMove-gameboard, 
		 */
		for( int i=1; i<=boardAfterNextMove.getSize(); i++) {
			for (int j=1; j<=boardAfterNextMove.getSize(); j++) {
				Coordinates coord = new Coordinates(i,j);
				
				/*
				 * check if these coordinates are a valid move
				 */
				if ( boardAfterNextMove.checkMove(nextcolor, coord) ) {
					boardAfterNextMove.makeMove(nextcolor, coord);
					/*
					 * try to play this move and see, of which quality it is
					 * if it's my move, i want to maximize my chances
					 */
					if (nextcolor == color) {
						result = Math.max(result, minimax(nextcolor, boardAfterNextMove, depth+1));
					} else {
						/*
						 * it's not my move. my opponent wants to minimize my chances
						 */
						result = Math.min(result, minimax(nextcolor, boardAfterNextMove, depth+1));
					}
				}
			}
		}
		
		
		return result;
	}

	private long getTimeLeft() {
		long timeLeft = timeout - (System.currentTimeMillis() - starttime);
		System.out.println("(II) Time left:" + timeLeft);
		return timeLeft;
	}
	
	
	
	
} /* End of class LookOneAheadPlayer(). */

