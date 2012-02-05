/**
 * This packages contains the Reversi Player lynx.Lynx.
 */
package lynx;

/* We need stuff from the reversi framework. */
import reversi.*;

/* We need also some utilities provided by Java. */
import java.util.Random;
import java.util.Vector;


/**
 * Lynx is the Reversi player for the contest at the end of the lecture
 * "Informatik II fuer D-ITET" in 2008.
 * 
 * It makes its next move using a Minimax-search using alpha-beta-pruning.
 * 
 * We use an Artificial Neural Network as evaluation function as described by
 * Edward P. Manning in [1]. We used this source for the structure of our net
 * and we took also the weights he found in training. The actual implementation
 * was done by ourselves from scratch, as well as everything else that hasn't 
 * anything to do with the ANN (e.g. alpha-beta, time-management, etc.).
 * 
 * [1] Manning, Edward P., (2007), "Temporal Difference Learning of an Othello
 * Evaluation Function for a Small Neural Network with Shared Weights," Proceedings
 * of the 2007 IEEE Symposium on Computational Intelligence and Games (CIG 2007).
 * 
 * @author Jonas Huber
 * @author Benedikt Koeppel
 */
public class Lynx implements ReversiPlayer {
	
	/*
	 * The amount of time in milisecods that have to remain before we
	 * decide to abort our search.
	 *   Some experimenting (there was no case where more than 10ms were used
	 * after the abort-bit was set) lead us to chose 20ms as the abort threshold.
	 */
	private final double ABORT_TIME_THRESHOLD = 20;
	
	/*
	 * The min- or max-node sets this to true if it detects that we're running
	 * out of time. If abort is true this will cause all other nodes to
	 * return immediately and nextMove to stop building the tree deeper.
	 */
	private boolean abort = false;
	
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
	
	/* 
	 * Our artificial neural network, class "Net".
	 */
	private Net myNet;
	
	/* The time when we started our current move. */
	private long startTime;
	
	/* This stores the coordinates of the best move found. This is done by
	 * every max node and since it's a recursive thing, the max-node which
	 * is the root-node is the last to write its result into this variable.
	 *   This avoids the problem, that it is not possible to return more 
	 * than one value in a function.
	 */
	private Coordinates bestMove = null;
	
	
	/**
	 * This method is called by the framework at the beginning of a game
	 * and it allows the color of this player and the move timeout to be
	 * set.
	 * 
	 * It stores also the color of the enemy in a variable and initializes
	 * the ANN used by this player.
	 * 
	 * @param color The color this player should be assigned to.
	 * @param timeout The maximum amount of ms this player has to make its
	 * move.
	 */
	public void initialize( int color, long timeout ) {
		
		/* Setting the own color and the timeout. */
		this.ownColor = color;
		this.timeout = timeout;

		/* Printing who we are and determining the color of the enemy. 
		 * I guess the viewers know that and aren't interested in our output... 
		 */
		if ( ownColor == GameBoard.RED ) {	
			//System.out.println("(II) Lynx is player RED.");
			enemyColor = GameBoard.GREEN;
		} else {	
			//System.out.println("(II) Lynx is player GREEN.");
			enemyColor = GameBoard.RED;
		}
		
		/* Initialize our neural net. */
		myNet = new Net();
		
	} /* End of initialize(). */

	
	/**
	 * This player makes its move by using a neural network (see head and Net.java for
	 * details).
	 * 
	 * Implemented:
	 * - MiniMax-search with alpha-beta-pruning
	 * - Evaluation-function: ANN
	 * - Time management: Builds the tree to depth+1 if enough time is left.
	 *   (iterative deepening)
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
		
		/* TIME MANAGEMENT (Iterative Deepening)
		 *****************
		 * We build our tree to an initial depth of e.g. 2, then to the next deeper
		 * level and so on. This happens until one of the node detects that there
		 * is no time left. Then *all other nodes of that level* will immediately
		 * return and not perform any analysis.
		 *   The move we made will be the best one found in the last tree that could
		 * be built fully.
		 * 
		 * **********************************************************************
		 * FIXME: Towards the end of the game, the tree is built down to a depth
		 * of around 32000+. This bears the danger of an integer overflow and of
		 * course it is useless. So the cutoff-test in the min and max-node have
		 * to be altered.
		 *   Ok, it is not a very bad problem, because it does no harm to the
		 * resulting move.
		 * **********************************************************************
		 * 
		 * TODO: Maybe it is possible to store the last calculated level so that it
		 * would be possible to start right away if we go deeper instead of building
		 * the whole tree each time.
		 */
		
		/* 
		 * The time-limit in the contest is 5s. This will surely allow to build the
		 * tree down to level 5 as some experiments have shown, so maxDepth has 
		 * initially to be set to 4 because it will be incremented once before the
		 * first call to maxValue().
		 */
		int maxDepth = 4; 
		
		/* This will store the result of the last level of the tree we have analyzed. */
		Coordinates lastBestMove = null;
		double lastBestEval = 0;
		int lastDepth = 2;
		double bestEval = 0;
		
		/* 
		 * Reset the global bestMove because if it should happen that we're running
		 * out of time while building the tree to its initial depth, lastBestMove would
		 * have the value of the bestMove of the *last* move and therefore this move
		 * now would be illegal.
		 */
		bestMove = null;
		
		/* We must abort set to false! */
		abort = false;
		
		/* Now we build the tree deeper and deeper as long as there is time (abort == false). */
		while ( !abort ) {
			
			lastDepth = maxDepth++;
			
			/* Storing the result of the last depth... */
			lastBestMove = bestMove;
			lastBestEval = bestEval;
			
			/* Building the tree down to the new depth. */
			//System.out.println("(II) Lynx: Building tree down to level " + maxDepth );
			bestEval = maxValue( currentBoard, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, maxDepth );
			
			
			//System.out.println("(II) Used " + (System.currentTimeMillis() - startTime) + "ms to go to level " + maxDepth);
		}
		
		/*
		 * Note: We have to take the results of the last *completed* level in the
		 * search. So we'll return lastBestMove instead of bestMove, et cetera.
		 */
		
		/*
		 * Fallback: Did we make a mistake? 
		 * If yes we just look one move ahead and chose the best one. This takes not really
		 * time and is a lot better than choosing a random move.
		 */
		if ( lastBestMove == null ) {
			System.out.println( "(EE) Lynx: Error during Minimax. This should not happen. Falling back to 1ply.");
			
			/* Some variables. */
			Coordinates coord;
			double eval;
			TextGameBoard boardAfterNextMove = new TextGameBoard( gb );
			
			bestEval = Double.NEGATIVE_INFINITY;
			
			/* Looping through the moves. */
			for ( int row = 1; row <= SIZE; row++ ) {
				for ( int col = 1; col <= SIZE; col++ ) {
					
					/* Preparing the coordinates of this move. */
					coord = new Coordinates( row, col );
					/* Resetting the board. */
					boardAfterNextMove.updateBoard( gb );
					
					if ( boardAfterNextMove.checkMove( ownColor, coord ) ) {
						boardAfterNextMove.makeMove( ownColor, coord );
						
						eval = evaluate( boardAfterNextMove );
						
						/* Did we find a better move than before? */
						if ( eval > bestEval ) {
							bestEval = eval;
							bestMove = coord;
						}
					}
					
				}
			}
			
			/* We return immediately the move we've found. */
			return bestMove;
		} /* End of fallback. */
		
		/* Debug output. 
		 * DONE: Maybe comment out for the contest.
		 */
		//System.out.println("(II) Aborted while building tree down to level " + maxDepth);
		//System.out.println("(II) Lynx: Evaluated nodes at depth " + lastDepth );
		//System.out.println("(II) Lynx: Best move has eval " + lastBestEval );
		
		//DONE: removed System.out
		//System.out.println("Unused time: " + (startTime+timeout-System.currentTimeMillis()));
		
		/* Now we make the best move that we've found. */
		return lastBestMove;
		
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
		 * If abort is set, then we have to return immediately without wasting some time.
		 */
		if ( abort ) {
			return Double.NEGATIVE_INFINITY;
		}

		/*
		 * We have to check here if we're *really* running out of time. If yes we set
		 * the abort-bit and return immediately.
		 */
		if ( !isTimeOk() ) {
			abort = true;
			return Double.NEGATIVE_INFINITY;
		}
		
		/*
		 * Check whether the game is over and if yes, who did win. If *we* win, the value
		 * 3 + evaluate(situation) is returned. This makes sure that we chose the best (in
		 * the net's opinion) of all winning moves (if there are more than one).
		 *   It remains to say that the evaluate()-function's range of output values is
		 * [-1,1], thus the values 3 and -3.
		 */
		if ( gameOver(situation) ) {
			int diff = situation.countStones( ownColor ) - situation.countStones( enemyColor );
			if ( diff > 0 ) {
				return 3 + evaluate(situation);
			} else if ( diff < 0 ) {
				return -3 + evaluate(situation);
			} else {
				return evaluate( situation );
			}
		}
		
		/* 
		 * Did we reach the maximum depth? If yes we stop here and evaluate the current
		 * situation.
		 */
		if ( depth <= 0 ) {
			return evaluate( situation );
		}
		
		/* Copy the current situation in a new TextGameBoard to perform our next move on. */
		TextGameBoard boardAfterNextMove = new TextGameBoard( situation );
		
		/* Is there a possible move for *us*? */
		if ( !situation.isMoveAvailable( ownColor) ) {
			
			/* If not, we pass, but we go still further down in our tree! */
			return minValue( situation, alpha, beta, depth - 1);
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
					alpha = minValue( boardAfterNextMove, alpha, beta, depth-1);
					
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
	 * This is a "min"-node within the MiniMax-algorithm.
	 * 
	 * @param situation The current situation.
	 * @param depth The current depth.
	 * @return The value of this node.
	 */
	private double minValue( TextGameBoard situation, double alpha, double beta, int depth ) {
		
		/*
		 * If abort is set, then we have to return immediately without wasting some time.
		 */
		if ( abort ) {
			return Double.NEGATIVE_INFINITY;
		}

		/*
		 * We have to check here if we're *really* running out of time. If yes, we set
		 * the abort-bit and return immediately.
		 */
		if ( !isTimeOk() ) {
			abort = true;
			return Double.NEGATIVE_INFINITY;
		}
		
		
		/* Check whether the game is over and if yes, who did win. If *we* win, the value
		 * 3 + evaluate(situation) is returned. This makes sure that we chose the best
		 * of all winning moves or the least worse of loosing ones (if there are more than
		 * one).
		 *   It remains to say that the evaluate()-function's range of output values is
		 * [-1,1], thus the values 3 and -3.
		 */
		if ( gameOver(situation) ) {
			int diff = situation.countStones( ownColor ) - situation.countStones( enemyColor );
			if ( diff > 0 ) {
				return 3 + evaluate(situation);
			} else if ( diff < 0 ) {
				return -3 + evaluate(situation);
			} else {
				return evaluate( situation );
			}
		}
		
		/* 
		 * Did we reach the maximum depth? If yes we stop here and evaluate the current
		 * situation.
		 */
		if ( depth <= 0 ) {
			return evaluate( situation );
		}
		
		/* Copy the current situation in a new TextGameBoard to perform our next move on. */
		TextGameBoard boardAfterNextMove = new TextGameBoard( situation );
		
		/* Is there a possible move for the *enemy*? */
		if ( !situation.isMoveAvailable( enemyColor ) ) {
			
			/* If not, we pass, but we go still further down in our tree! */
			return maxValue( situation, alpha, beta, depth - 1);
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
					beta = Math.min( beta, maxValue( boardAfterNextMove, alpha, beta, depth - 1) );
				}
				
				/*
				 * Check if we can do a break...
				 */
				if ( beta <= alpha ) {
					// System.out.println("(II) Alpha cut!");
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
			//DONE: removed System.out
			//System.out.println("(WW) Runnning out of time...");
			return false;
		}
		return true;
	} /* End of isTimeOk(). */
	
	
	/**
	 * This is just a wrapper function around the network's output in order
	 * to have more readable code. It let's the neural network evaluate the
	 * given situation.
	 * 
	 * NOTE: The evaluation is *always* done from our own perspective, say:
	 * Greater values are good for us, lower values are bad for us!
	 *  
	 * @param situation The situation to evaluate.
	 * @return The value of this situation.
	 */
	private double evaluate( TextGameBoard situation ) {
		try {
			return myNet.getOutput( genInputVector( situation ) );
		} catch ( Exception e ) {
			/*
			 * If we get an exception something has gone terribly wrong within
			 * the net. This should NEVER happen. We return 0 in the hope, that
			 * there may be other moves having greater or lower values so that
			 * our player doesn't get stuck here because of this error. But this
			 * is probably an illusion...
			 */
			System.out.println("(EE) An error occured within Net.genOutput()!");
			System.out.println( e.getMessage() );
			return 0;
		}
	} /* End of evaluate(). */
	
	
	/**
	 * Generates a vector (array) of the current situation of the board so that it
	 * can be processed by the neural network.
	 * 
	 * @param tgb The situation to transform.
	 * @return A vector (array) containing the current situation on the board.
	 */
	private double[] genInputVector( TextGameBoard tgb ) {
		
		int index = 0;
		double [] result = new double[ 64 ];
		
		int testField = 0;
		
		for ( int row = 1; row <= 8; row++ ) {
			for (int col = 1; col <= 8; col++ ) {
				try {
					testField = tgb.getPosition( new Coordinates(row, col) );
				} catch ( OutOfBoundsException e ) {
					/* This should not happen! */
					System.out.println( e.getMessage() );
					e.printStackTrace();
				}
				
				/*
				 * We set a field to 1 if it bears a stone of our own color and to -1 if
				 * it's an enemy's stone. This assures, that the outputs of the net can
				 * always be interpreted such as that bigger values are *good* for us and
				 * lower values are *bad* for us.
				 */
				if ( testField == ownColor ) {
					result[index] = 1;
				} else if ( testField == GameBoard.EMPTY ) {
					result[index] = 0;
				} else {
					result[index] = -1;
				}
				index++;
			}
		}
		return result;
	} /* End of genInputVector(). */

} /* End of class Lynx. */
