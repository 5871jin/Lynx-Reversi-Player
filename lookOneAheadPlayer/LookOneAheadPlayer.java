/**
 * This contains the player LookOneAheadPlayer.
 * run this with sth like 'java reversi.Arena MyGame lookOneAheadPlayer.LookOneAheadPlayer humanPlayer.HumanPlayer'
 */
package lookOneAheadPlayer;

import java.io.IOException;

import player.Player;
import reversi.Coordinates;
import reversi.GameBoard;
import reversi.OutOfBoundsException;
import reversi.TextGameBoard;

/**
 * This player implements a method to evaluate a game situation. It tries to
 * find the best move by simulating each possible move and then evaluating the
 * resulting situation.
 * 
 * @author Jonas Huber
 * @author Benedikt Koeppel
 */
public class LookOneAheadPlayer extends Player {
	
	/**
	 * The value of each field on the gameboard
	 */
	protected int[][] valueField = {
			{	9999,	5,		500,	200,	200,	500,	5,		9999	},
			{	5,		1,		50,		150,	150,	50,		1,		5		},
			{	500,	50,		250,	100,	100,	250,	50,		500		},
			{	200,	150,	100,	50,		50,		100,	150,	200		},
			{	200,	150,	100,	50,		50,		100,	150,	200		},
			{	500,	50,		250,	100,	100,	250,	50,		500		},
			{	5,		1,		50,		150,	150,	50,		1,		5,		},
			{	9999,	5,		500,	200,	200,	500,	5,		9999	}
	};

	/**
	 * how many fields have to be free (maximum) to change to Maximum-Strategy
	 * 
	 * TODO: Experimented a bit and found 20 to be a good value... ;-)
	 */
	protected int changeToMaxStrategyThreshold = 20;
		
	/**
	 * This method looks one move ahead and then makes the move which has
	 * produced the simulation of *one* move ahead in the game.
	 * 
	 * @param gb The current gameboard.
	 * @return The coordinates of our move.
	 */
	public Coordinates nextMove(GameBoard gb) {
		
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
		int bestEval = -999999;
		
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
					eval = evaluate( color, boardAfterNextMove );
					System.out.println("Playing coordinates (" + (char)(coord.getCol()+64) + "" + coord.getRow() + ") returns value " + eval);
			/*		try {
						System.in.read();
					} catch (IOException e) {
					}*/
					
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
		}

		/* Now we make the best move that we've found. */
		return bestCoord;
		
	} /* End of method nextMove(). */
	
	
	/**
	 * This method evaluates the situation on a given gameboard from the point of 
	 * view of the player specified ).
	 * 
	 * It returns an integer value which has the following meaning:
	 * - Greater than zero: The situation is better for us than our enemy.
	 * - Equal to zero: The situation is not better nor worse for us or our enemy.
	 * - Smaller than zero: The situation is better for our enemy.
	 * The bigger the number is, the better is the situation; the smaller it is, the
	 * worse is the situation.
	 * 
	 * E.g. 200 is better than 50, but 50 is better than 0 or -1 or -200.
	 * 
	 * Idea for implementation
	 * ***********************
	 * We define some ranges of powers of ten in order to weigh the influence
	 * of certain fields and situations on the field. Say, if we have a corner, then
	 * we add 1000 (10^3) to our result. If we own a X- or a C-field we subtract say,
	 * 100 (10^2). Finally we add the difference (ownstones-enemystones) for a
	 * maximizing paradigm and (enemystone-ownstones) for a minimizing paradigm.
	 * 
	 * This approach makes sure, that for example if we have the possibility to
	 * set a stone in two corners, we would chose the move that also fits best our
	 * min- or max-paradigm.
	 * 
	 * That was just an idea; the realisation must be much more specific. And
	 * questions like: shall we subtract 1000 for an enemy to possess a corner, 
	 * and so on have to be answered.
	 * 
	 * Pseudocode
	 * **********
	 * result = 0
	 * for each c in corners do:
	 *   if c.color = mycolor: result += 100
	 * 
	 * for each x in x-fields do:
	 *   if x.corresponding_corner.color = empty and x.color = mycolor: result -= 100
	 * 
	 * for each c in c-fields do:
	 *   if c.coreponding_corner.color = empty and c.color = mycolor: result -= 75
	 * 
	 * result += -5 * ( gameboard.myfields.count - gameboard.enemyfields.count )
	 * return result
	 *  
	 * TODO: return -9999 (or sth like that^^) if the game will be finished with that move and we've lost
	 * TODO: return +9999 if the game is finished and we've won :-)
	 * 
	 * 
	 * @param p The color of the player we're interested in.
	 * @param tgb The TextGameBoard to evaluate.
	 * @return An integer representing the good- or badness of the situation.
	 */
	protected int evaluateOld( int p, TextGameBoard tgb ) {
		int quality = 0;
		
		int enemy;
		/* Determining the color of our enemy. */
		if ( p == GameBoard.GREEN ) {
			enemy = GameBoard.RED;
		} else {
			enemy = GameBoard.GREEN;
		}
		
		/* If we have none of our own stones, this move is fatal.
		 * Note that we return something in the range of 10^5, whereas
		 * the initial value of the worst move is in the range of 10^6,
		 * that meanst we would make this move instead of "passen" if
		 * there's no other possibility.
		 */
		if ( tgb.countStones( p ) == 0 ) {
			return -99999;
		}
		
		/*
		 * of certain fields and situations on the field. Say, if we have a corner, then
		 * we add 1000 (10^3) to our result. If we own a X- or a C-field we subtract say,
		 * 100 (10^2). Finally we add the difference (ownstones-enemystones) for a
		 * maximizing paradigm and (enemystone-ownstones) for a minimizing paradigm.
		 */

		try {
			/*
			 * check if we can reach a corner. If yes, add 100 for each gained corner
			 */
			if ( tgb.checkMove(p, new Coordinates(1,1)) && tgb.getPosition(new Coordinates(1,1)) == p ) {
				quality += 100;
			}
			if ( tgb.checkMove(p, new Coordinates(1,8)) && tgb.getPosition(new Coordinates(1,8)) == p ) {
				quality += 100;
			}
			if ( tgb.checkMove(p, new Coordinates(8,8)) && tgb.getPosition(new Coordinates(8,8)) == p ) {
				quality += 100;
			}
			if ( tgb.checkMove(p, new Coordinates(8,1)) && tgb.getPosition(new Coordinates(8,1)) == p ) {
				quality += 100;
			}

			/*
			 * If the enemy has the corners, this is not good for us!
			 * TODO: Does this make sense? This questions applies also to the same at
			 * the X- and C-field checks below, where I did the same additions as here.
			 */
			if ( tgb.checkMove(p, new Coordinates(1,1)) && tgb.getPosition(new Coordinates(1,1)) == enemy ) {
				quality -= 90;
			}
			if ( tgb.checkMove(p, new Coordinates(1,8)) && tgb.getPosition(new Coordinates(1,8)) == enemy ) {
				quality -= 90;
			}
			if ( tgb.checkMove(p, new Coordinates(8,8)) && tgb.getPosition(new Coordinates(8,8)) == enemy ) {
				quality -= 90;
			}
			if ( tgb.checkMove(p, new Coordinates(8,1)) && tgb.getPosition(new Coordinates(8,1)) == enemy ) {
				quality -= 90;
			}
			
			/*
			 * TODO: only check X-fields, if the corresponding corner is still free
			 * check if we'd play a X filed. If yes, subtract 100 for each field
			 * I guess this weight must be the same as the weight for a corner, as 
			 * playing an X-field means (often) loosing the corner
			 */
			if ( tgb.getPosition(new Coordinates(1,1)) == GameBoard.EMPTY &&
					tgb.getPosition(new Coordinates(2,2)) == p ) {
				quality -= 100;
			}
			if ( tgb.getPosition(new Coordinates(1,8)) == GameBoard.EMPTY &&
					tgb.getPosition(new Coordinates(2,7)) == p ) {
				quality -= 100;
			}
			if ( tgb.getPosition(new Coordinates(8,8)) == GameBoard.EMPTY &&
					tgb.getPosition(new Coordinates(7,7)) == p ) {
				quality -= 100;
			}
			if ( tgb.getPosition(new Coordinates(8,1)) == GameBoard.EMPTY &&
					tgb.getPosition(new Coordinates(7,2)) == p ) {
				quality -= 100;
			}
			/*
			 * If the enemy has the X-fields (while not having the corner), thats good for us.
			 */
			if ( tgb.getPosition(new Coordinates(1,1)) == GameBoard.EMPTY &&
					tgb.getPosition(new Coordinates(2,2)) == enemy ) {
				quality += 90;
			}
			if ( tgb.getPosition(new Coordinates(1,8)) == GameBoard.EMPTY &&
					tgb.getPosition(new Coordinates(2,7)) == enemy ) {
				quality += 90;
			}
			if ( tgb.getPosition(new Coordinates(8,8)) == GameBoard.EMPTY &&
					tgb.getPosition(new Coordinates(7,7)) == enemy ) {
				quality += 90;
			}
			if ( tgb.getPosition(new Coordinates(8,1)) == GameBoard.EMPTY &&
					tgb.getPosition(new Coordinates(7,2)) == enemy ) {
				quality += 90;
			}
			
			/*
			 * check for both c-fields of a corner
			 * subtract 75
			 */
			if ( tgb.getPosition(new Coordinates(1,1)) == GameBoard.EMPTY && 
					( tgb.getPosition(new Coordinates(1,2)) == p || 
					  tgb.getPosition(new Coordinates(2,1)) == p ) ) {
				quality -= 75;
			}
			else if ( tgb.getPosition(new Coordinates(1,8)) == GameBoard.EMPTY && 
					( tgb.getPosition(new Coordinates(1,7)) == p || 
					  tgb.getPosition(new Coordinates(2,8)) == p ) ) {
				quality -= 75;
			}
			else if ( tgb.getPosition(new Coordinates(8,8)) == GameBoard.EMPTY && 
					( tgb.getPosition(new Coordinates(8,7)) == p || 
					  tgb.getPosition(new Coordinates(7,8)) == p ) ) {
				quality -= 75;
			}
			else if ( tgb.getPosition(new Coordinates(8,1)) == GameBoard.EMPTY && 
					( tgb.getPosition(new Coordinates(8,1)) == p || 
					  tgb.getPosition(new Coordinates(2,8)) == p ) ) {
				quality -= 75;
			}
			/*
			 * Again, if the enemy has this fields, this is good for us.
			 */
			if ( tgb.getPosition(new Coordinates(1,1)) == GameBoard.EMPTY && 
					( tgb.getPosition(new Coordinates(1,2)) == enemy || 
					  tgb.getPosition(new Coordinates(2,1)) == enemy ) ) {
				quality += 65;
			}
			else if ( tgb.getPosition(new Coordinates(1,8)) == GameBoard.EMPTY && 
					( tgb.getPosition(new Coordinates(1,7)) == enemy || 
					  tgb.getPosition(new Coordinates(2,8)) == enemy ) ) {
				quality += 65;
			}
			else if ( tgb.getPosition(new Coordinates(8,8)) == GameBoard.EMPTY && 
					( tgb.getPosition(new Coordinates(8,7)) == enemy || 
					  tgb.getPosition(new Coordinates(7,8)) == enemy ) ) {
				quality += 65;
			}
			else if ( tgb.getPosition(new Coordinates(8,1)) == GameBoard.EMPTY && 
					( tgb.getPosition(new Coordinates(8,1)) == enemy || 
					  tgb.getPosition(new Coordinates(2,8)) == enemy ) ) {
				quality += 65;
			}
			
			
			/*
			 * TODO: check if we can win stable fields at the sides of the field
			 */
			
			/*
			 * TODO: bonus for staying within sweet-16
			 */
						
			/*
			 * check how many fields get changed
			 * in the first part of the game, play defensively
			 * in the last part, play offensively
			 * Weight for this is 5
			 * TODO: Changed the weight to 2. Then this player wins against minPlayer! ;-)
			 */
			quality += 2*evaluateMinMax(p, tgb);			
			
		} catch (OutOfBoundsException e) {
			/*
			 * Does this make sense???
			 * if the positions above were out of bounds, this is not a good idea to play
			 * => we return -99999 which means that this move is a terribly bad move
			 * This should not happen, so we print the stack trace.
			 */
			e.printStackTrace();
			System.out.println("(EE) This should not happen! Fix code in LookOneAheadPlayer.evaluate!");
			return -99999;
		}
		
		return quality;  
	}
	
	protected int evaluate( int p, TextGameBoard tgb ) {
		int result = 0;
		
		/*
		 * check, hwo has played which field, and what's the value of those fields
		 */
		int fieldColor;
		for ( int row=1; row<tgb.getSize(); row++ ) {
			for ( int col=1; col<tgb.getSize(); col++ ) {
				try {
					fieldColor = tgb.getPosition(new Coordinates(row-1,col-1));
				} catch (OutOfBoundsException e) {
					// this can't happen
					continue;
				}
				/*
				 * if the field has our color
				 */
				if ( fieldColor == this.color ) {
					result += valueField[row][col];
				} else if ( fieldColor != GameBoard.EMPTY ) {
					/*
					 * if the field hasn't our color, but isn't empty
					 */
					result -= valueField[row][col];
				}
			}
		}
		
		result += 2*evaluateMinMax(p, tgb);
		
		return result;
	}
	
	/**
	 * 
	 * @param p The color of the player we're interested in.
	 * @param tgb The TextGameBoard to evaluate.
	 * @return An integer representing the good- or badness of the situation.
	 */
	private int evaluateMinMax( int p, TextGameBoard tgb ) {
		
		/* This variable is returned at the end of this method. */
		int result = 0;
		
		/*
		 * if lots of fields are free, play with Minimum-Strategy
		 */
		if ( getFreeFields( tgb ) > changeToMaxStrategyThreshold ) {
			/* Performing a simple MinPlayer-like evaluation... */
			result = evaluateMinPlayer( p, tgb );
		} else {
			/*
			 * as soon as less than changeToMaxStrategyThreshold fields are free, play offensively
			 */
			result = evaluateMaxPlayer( p, tgb );
		}
		
		return result;
		
	} /* End of method evaluateGameboard(). */
	
	
	/**
	 * This is just an *example*. It does a very basic evaluation of the situation
	 * on tgb by returning the difference between the number of own and enemy
	 * stones. That leads to moves which have the gameboard with the most of our own 
	 * stones as a consequence.
	 * 
	 * @param p The color of the player we're interested in.
	 * @param tgb The TextGameBoard to evaluate.
	 * @return An integer representing the good- or badness of the situation.
	 */
	private int evaluateMaxPlayer( int p, TextGameBoard tgb ) {

		/* This variable is returned at the end of this method. */
		int result = 0;
		
		int enemy = 0;
		
		/* Determining the color of our enemy. */
		if ( p == GameBoard.GREEN ) {
			enemy = GameBoard.RED;
		} else {
			enemy = GameBoard.GREEN;
		}
		
		/*
		 * NOTE: You could make a basic minPlayer out of this MaxPlayer by just
		 * swapping the next two commands:
		 * 
		 * result = tgb.countStones( enemy ) - tgb.countStones( p );
		 */
		result = tgb.countStones( p ) - tgb.countStones( enemy );
		
		return result;
	} /* End of evaluateMaxPlayer(). */


	/**
	 * This is just an *example*. It does a very basic evaluation of the situation
	 * on tgb by returning the difference between the number of enemy and own
	 * stones. That leads to moves which have the gameboard with the least of our own 
	 * stones as a consequence.
	 * 
	 * @param p The color of the player we're interested in.
	 * @param tgb The TextGameBoard to evaluate.
	 * @return An integer representing the good- or badness of the situation.
	 */
	private int evaluateMinPlayer( int p, TextGameBoard tgb ) {

		/* This variable is returned at the end of this method. */
		int result = 0;

		int enemy = 0;

		/* Determining the color of our enemy. */
		if ( p == GameBoard.GREEN ) {
			enemy = GameBoard.RED;
		} else {
			enemy = GameBoard.GREEN;
		}

		result = tgb.countStones( enemy ) - tgb.countStones( p );
				
		return result;
	} /* End of evaluateMinPlayer(). */	
} /* End of class LookOneAheadPlayer(). */

