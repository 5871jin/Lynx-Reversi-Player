package randomPlayer;

import player.Player;
import reversi.*;

import java.util.Random;

/**
 * This class inherits from Player and implements the abstract method
 * nextMove() by choosing random coordinated for the next move until a
 * valid pair of coordinates is found.
 * 
 * @author Jonas Huber
 * @author Benedikt Koeppel
 */
public class RandomPlayer extends Player {

	/**
	 * Constructor printing some info.
	 */
	public RandomPlayer() {
		System.out.println("Instance of RandomPlayer created...");
	} /* End of constructor RandomPlayer(). */

	
	/**
	 * The RandomPlayer makes its move by randomly choosing a position
	 * for the next move.
	 * 
	 * @see player.Player#nextMove(reversi.GameBoard)
	 * 
	 * @param gb The gameboard on which we are operating.
	 */
	public Coordinates nextMove(GameBoard gb) {
		
		Random rnd = new Random();
		
		int x = rnd.nextInt( gb.getSize() ) + 1;
		int y = rnd.nextInt( gb.getSize() ) + 1;
		Coordinates coord = new Coordinates( y, x );
		
		/* 
		 * We check first *if* possible move exists. If this is the case, we try with random-generator
		 * to play.
		 * TODO: It isn't really fast.
		 */
		if ( possibleMoveExists( gb ) ) {
			while ( !checkMove (coord, gb ) ) {
				x = rnd.nextInt (gb.getSize() ) + 1;
				y = rnd.nextInt (gb.getSize() ) + 1;
				coord = new Coordinates( y, x );
			}
		} else { 
			/* 
			 * This is necessary, because we have to return null, if 
			 * no move is possible...
			 */
			coord = null;
		}
		return coord;
		
	} /* End of nextMove(). */
	
} /* End of class RandomPlayer */