/**
 * This contains the reversi player "The Net".
 */
package theNet;

import java.util.Random;
import java.util.Vector;
import player.Player;
import reversi.*;


/**
 * This is a reversi player that uses the class Net as an
 * artificial neural network (ANN) in order to implement its evaluation
 * function. See Net.java for details.
 * 
 * @author Jonas Huber
 * @author Benedikt Koeppel
 * 
 * @see theNet.Net
 */
public class TheNet extends Player {

	/*
	 * This constant defines the probability for this player to make a
	 * random move. It is called \epsilon in the Article.
	 * 
	 * TODO: For the contest this has (probably) to be set to ZERO! Or
	 * maybe it would be even better to remove the code which implements
	 * the random moves...
	 */
	private final static double EPSILON = 0.0;
	
	/* Our artificial neural network. */
	private Net myNet;
	
	
	/**
	 * We have to write our own initialize method in which we build the
	 * structure of the neural net and initialize it.
	 * 
	 * The initialization of color and timeout is done using our parents
	 * constructor.
	 */
	public void initialize(int color, long timeout) {
		
		super.initialize(color, timeout);
		
		/* Initialize our neural net. */
		myNet = new Net();
		
	} /* End of initialize(). */

	
	/**
	 * This player makes its move by using a neural network (see Net.java for
	 * details).
	 * 
	 * @see player.Player#nextMove(reversi.GameBoard)
	 */
	//@SuppressWarnings("unchecked") das versteht Java 1.4.2 irgendwie nicht :-(
	public Coordinates nextMove( GameBoard gb ) {
		
		/* Does a move exist? */
		if ( !possibleMoveExists( gb ) ) {
			return null;
		}
		
		/*
		 * We want do to a total random move with probability EPSILON.
		 */
		Random r = new Random();
		if ( r.nextDouble() < EPSILON ) {
			
			int x = r.nextInt( gb.getSize() ) + 1;
			int y = r.nextInt( gb.getSize() ) + 1;
			Coordinates coord = new Coordinates( y, x );

			while ( !checkMove (coord, gb ) ) {
				x = r.nextInt (gb.getSize() ) + 1;
				y = r.nextInt (gb.getSize() ) + 1;
				coord = new Coordinates( y, x );
			}
			System.out.println("(WW) " + this.getClass().getName() + " made a random move!");
			return coord;
		}
		
		/* Temporary variable. */
		Coordinates coord = null;
		double eval = 0;
		
		/* 
		 * This two variables store the best evaluation result gained so far and
		 * also the coordinates of the move that led to this result.
		 */
		Vector bestCoord = new Vector();
		bestCoord.add( coord );
		
		/* 
		 * We have to initialize bestValue according to our color! 
		 */
		double bestEval;
		if ( color == GameBoard.RED ) {
			bestEval = Integer.MIN_VALUE;
		} else {
			bestEval = Integer.MAX_VALUE;
		}

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
					try {
						
						eval = myNet.getOutput( genInputVector( boardAfterNextMove ) );
					
					} catch ( Exception e ) {
						System.out.println("(EE) Error within Net.getOutput: ");
						System.out.println( e.getMessage() );
					}
					
					/* 
					 * Is this move better than another we tried before?
					 * 
					 * Note: It does matter, which color we are: The Net returns bigger values if
					 * the situation is good for black/RED.
					 */
					System.out.println("(II) " + eval + " (" + coord.getRow() + "," + coord.getCol() + ")");
					
					if ( eval == bestEval ) {
						
						/* 
						 * If we have the same value twice, we add this coordinates to our vector.
						 * Note that we don't have to alter the bestEval value!
						 */
						bestCoord.addElement( coord );
						
					} else if ( color == GameBoard.RED ) {

							if ( eval > bestEval ) {
								/* Resetting our vector, because  we have a new best value! */
								bestCoord = new Vector();
								bestCoord.addElement( coord );
								bestEval = eval;
							}

						} else {

							if ( eval < bestEval ) {
								/* Resetting our vector, because  we have a new best value! */
								bestCoord = new Vector();
								bestCoord.addElement( coord );
								bestEval = eval;
							}
						}
					
				} /* End if. */
			} /* End for. */
		} /* End for. */

		/* FIXME: Debug output... */
		System.out.println("(II) Number of equal evaluations: " + bestCoord.size() );
		
		/* Now we select a move randomly out of those with the same values. */
		Coordinates result;
		result = (Coordinates)bestCoord.get( r.nextInt( bestCoord.size() ) );
		
		/* Did we make a mistake? */
		if ( result == null ) {
			System.out.println( "(EE) Will return null, but bestEval = " + bestEval);
		}
		
		/* Debug output. */
		System.out.println("(II) Best move has eval " + bestEval );
		
		/* Now we make the best move that we've found. */
		return result;
		
	} /* End of nextMove(). */
	
	
	/**
	 * Generates an vector of the current situation of the board so that it
	 * can be processed by the neural network.
	 * 
	 * @param tgb The situation to transform.
	 * @return A vector containing the current situation on the board.
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
				 * Note: Black corresponds to RED, and white to GREEN. The network produces
				 * an output that is bigger if the situation is good for black/RED and an 
				 * smaller (negative) value if it is a good situation for white/GREEN.
				 */
				if ( testField == GameBoard.RED ) {
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

} /* End of class TheNet. */
