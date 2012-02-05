package alphaBeta;

/* We need stuff from the reversi framework. */
import reversi.*;
import lookOneAheadPlayer.*;


/**
 * This is just a dummy player to show off the alpha-beta-pruning. It uses now
 * the evaluate()-function of LookOneAheadPlayer.
 * 
 * @author Jonas Huber
 * @author Benedikt Koeppel
 */
public class AlphaBeta2 extends LookOneAheadPlayer {
	
	/*
	 * This constant defines ReversiPlayerthe minimum time (ms) that have to remain so
	 * that we don't abort the alpha-beta-search.
	 */
	private final long ABORT_TIME_THRESHOLD = 100;
	
	/*
	 * This constant defines the maximum depth we go down our tree.
	 * TODO: This has to be solved otherwise, because the *really* limiting
	 * factor is the *time*, not the depth!
	 */
	private final long MAX_DEPTH = 4;
	
	/*
	 * The size of the game board (number of fields in one direction). There
	 * should not be any need to change this, but better prepare for the un-
	 * expected...
	 */
	private final int SIZE = 8;
	
	/* 
	 * Timeout and the own color are set by the framework with initialize().
	 * And because it is a useful information, we have also the other
	 * color in an own variable.
	 */
	private long timeout;
	private int ownColor;
	private int enemyColor;
	
	/* The time when we started our current move. */
	private long startTime;
	
	/* This stores the coordinates of the best move found. */
	Coordinates bestMove = null;
	
	/**
	 * This method is called by the framework at the beginning of a game
	 * and it allows the color of this player and the move timeout to be
	 * set.
	 * 
	 * @param color The color this player should be assigned to.
	 * @param timeout The maximum amount of ms this player has to make its
	 * move.
	 */
	public void initialize( int color, long timeout ) {
		
		/* Setting the own color and the timeout. */
		this.ownColor = color;
		this.timeout = timeout;

		/* Printing who we are and determining the color of the enemy. */
		if ( ownColor == GameBoard.RED ) {	
			System.out.println("(II) Lynx is player RED.");
			enemyColor = GameBoard.GREEN;
		} else {	
			System.out.println("(II) Lynx is player GREEN.");
			enemyColor = GameBoard.RED;
		}
		
	} /* End of initialize(). */

	
	/**
	 * This player makes its move using alpha-beta-pruning in a minimax-search.
	 * 
	 * Note that it purposely doesn't have a competitive evaluation function to
	 * not reveal too much at this point of time. ;-)
	 * 
	 * @see player.Player#nextMove(reversi.GameBoard)
	 */
	public Coordinates nextMove( GameBoard gb ) {
		
		/* The first thing we do is setting the start time. */
		startTime = System.currentTimeMillis();
		
		/* Storing the current situation in a new object. */
		TextGameBoard currentBoard = new TextGameBoard( gb );
		
		/* Does a move exist? */
		if ( !currentBoard.isMoveAvailable( ownColor ) ) {
			return null;
		}
		
		/*
		 * We are a maximizing node, so we act appropriate. maxValue writes the
		 * coordinates of the best move it finds into the global variable
		 * bestMove.
		 */
		double bestEval = maxValue( currentBoard, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0 );
		
		/* Did we make a mistake? */
		if ( bestMove == null ) {
			System.out.println( "(EE) Will return null, but bestEval = " + bestEval);
		}
		
		/* Debug output. */
		System.out.println("(II) Best move has eval " + bestEval );
		
		/* Now we make the best move that we've found. */
		return bestMove;
		
	} /* End of nextMove(). */
	
	
	/**
	 * This is a "max"-node within the MiniMax-algorithm. Because this player itself
	 * acts like a max-node, this method should also return the coordinates of the
	 * best move it has found (at least the instance of this method which is the
	 * root-node of the tree should do so). Because it is not possible to return two
	 * values, this method writes the bestCoordinates it finds into a global
	 * Variable which can then be processed by nextMove.
	 * 
	 * @param situation The current situation.
	 * @param depth The current depth.
	 * @return The value of this node.
	 */
	private double maxValue( TextGameBoard situation, double alpha, double beta, int depth ) {
		
		/* 
		 * Did we reach the maximum depth? 
		 * 
		 * TODO: isTimeOK has to be handled elsewhere!!!
		 */
		if ( depth >= MAX_DEPTH || !isTimeOk() || gameOver(situation) ) {
			return evaluate( situation );
		}
		
		/* Copy the current situation in a new TextGameBoard to perform our next move on. */
		TextGameBoard boardAfterNextMove = new TextGameBoard( situation );
		
		/* Is there a possible move for *us*? */
		if ( !situation.isMoveAvailable( ownColor) ) {
			
			/* If not, we pass, but we go still further down in our tree!
			 * 
			 * FIXME: Is it right to just pass alpha and beta?
			 */
			return minValue( situation, alpha, beta, depth + 1);
		}
		
		/*
		 * We need some variables.
		 */
		Coordinates coord = null;
		Coordinates localBestMove = null;
		
		 /* Setting bestResult initially to -infty. */
		double bestResult = Double.NEGATIVE_INFINITY;
		
		/*
		 * Now we iterate over every field and search the move leading to the
		 * greatest result (we are the max node!).
		 */
		for ( int row = 1; row <= SIZE; row++ ) {
			for ( int col = 1; col <= SIZE; col++ ) {
				
				/* Preparing the coordinates of this move. */
				coord = new Coordinates( row, col );
				
				/*
				 * If the move is valid, we do it and check whether it's value is 
				 * greater than one we've found before. Because this is a max-node
				 * we use minValue to evaluate the resulting situation.
				 */
				if ( boardAfterNextMove.checkMove( ownColor, coord) ) {
					boardAfterNextMove.makeMove( ownColor, coord );
					
					/* Recursion */
					alpha = minValue( boardAfterNextMove, alpha, beta, depth+1);
					
					/* Is this move better than another found before? */
					if ( alpha > bestResult ) {
						 localBestMove = coord;
						 bestResult = alpha;
					}
				}
				
				/*
				 * Now we test if we can do a break...
				 */
				if ( alpha >= beta ) {
					//System.out.println("(II) Beta cut!");
					break;
				}
				
				/*
				 * Resetting our board!
				 */
				boardAfterNextMove.updateBoard( situation );
			}
		} /* End of looping through the fields... */
		
		/*
		 * We would have to return *two* values: The evaluation of the best move found 
		 * *and* its coordinates. Because this is impossible we write the coordinates
		 * into a global variable which then is accessed by nextMove().
		 */
		bestMove = localBestMove;
		return alpha;
		
	} /* End of maxValue(). */
	
	
	/**
	 * This is a "min"-node within the MiniMax-algorithm using alpha-beta pruning.
	 * 
	 * @param situation The current situation.
	 * @param depth The current depth.
	 * @return The value of this node.
	 */
	private double minValue( TextGameBoard situation, double alpha, double beta, int depth ) {
		
		/* 
		 * Did we reach the maximum depth?
		 * 
		 * TODO: isTimeOK has to be handled elsewhere!!!
		 */
		if ( depth >= MAX_DEPTH || !isTimeOk() || gameOver(situation) ) {
			return evaluate( situation );
		}
		
		/*
		 * TODO: Check for winning/loosing situation, check if game is over, time left, etc.
		 */
		
		/* Copy the current situation in a new TextGameBoard to perform our next move on. */
		TextGameBoard boardAfterNextMove = new TextGameBoard( situation );
		
		/* Is there a possible move for the *enemy*? */
		if ( !situation.isMoveAvailable( enemyColor ) ) {
			
			/* If not, we pass, but we go still further down in our tree! 
			 * 
			 * FIXME: Is it right to just pass alpha and beta on?
			 */
			return maxValue( situation, alpha, beta, depth + 1);
		}
		
		/*
		 * We need some variables.
		 */
		Coordinates coord = null;
		
		/*
		 * Now we iterate over every field and search the move leading to the
		 * lowest result (we are the min node!).
		 */
		for ( int row = 1; row <= SIZE; row++ ) {
			for ( int col = 1; col <= SIZE; col++ ) {
				
				/* Preparing the coordinates of this move. */
				coord = new Coordinates( row, col );
				
				/*
				 * If the move is valid, we do it and check whether it's value is 
				 * greater than one we've found before. Because this is a max-node
				 * we use minValue to evaluate the resulting situation.
				 */
				if ( boardAfterNextMove.checkMove( enemyColor, coord) ) {
					boardAfterNextMove.makeMove( enemyColor, coord );
					beta = Math.min( beta, maxValue( boardAfterNextMove, alpha, beta, depth + 1) );
				}
				
				/*
				 * Check if we can do a break...
				 */
				if ( beta <= alpha ) {
					//System.out.println("(II) Alpha cut!");
					break;
				}
				/*
				 * Resetting our board!
				 */
				boardAfterNextMove.updateBoard( situation );
			}
		} /* End of looping through the fields... */
		
		return beta;
	} /* End of minValue(). */
	
	/**
	 * This is just a helper method which checks, if the game is finished.
	 * 
	 * @param situation The situation to check.
	 * @return true if the game is finished, false else.
	 */
	private boolean gameOver( TextGameBoard situation ) {
		
		/*
		 * TODO: Maybe here can be found a better (faster) implementation...
		 */
		if ( situation.isFull() ) {
			return true;
		}
		
		if ( situation.isMoveAvailable( ownColor ) || situation.isMoveAvailable( enemyColor ) ) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * This time checks whether we are already near our timeout and therefore should
	 * soon return a move or if we still have plenty of time to do further calculations.
	 * 
	 * @return true if everything is ok, false else.
	 */
	private boolean isTimeOk() {
		if ( timeout - (System.currentTimeMillis() - startTime) <= ABORT_TIME_THRESHOLD ) {
			System.out.println("(WW) Runnning out of time...");
			return false;
		}
		return true;
	} /* End of isTimeOk(). */
	
	
	/**
	 * Wrapper around the evaluation-function of LookOneAheadPlayer.
	 * @param situation The situation to evaluate.
	 * @return The value of this situation.
	 */
	private double evaluate( TextGameBoard situation ) {
		
		return super.evaluate( ownColor, situation);
		
	} /* End of evaluate(). */
	
} /* End of class AlphaBeta. */

