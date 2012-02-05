package maxPlayer;

import player.Player;
import reversi.*;

/**
 * This is a simpley player who choses its moves by the only criterium 
 * that it leads to to biggest number of turned enemy stones.
 * 
 * @author Jonas Huber
 */
public class MaxPlayer extends Player {

	/**
	 * Constructor printing some info.
	 */
	public MaxPlayer() {
		System.out.println("Instance of MaxPlayer created...");
	} /* End of constructor RandomPlayer(). */

	
	/**
	 * MaxPlayer makes its moves by maximizing the number of enemy stones
	 * that get turned as a consequenze of that move.
	 * 
	 * NOTE: This player is defenitely BAD!!! :-D
	 * 
	 * @see player.Player#nextMove(reversi.GameBoard)
	 * 
	 * @param gb The gameboard on which we are operating.
	 */
	public Coordinates nextMove(GameBoard gb) {
		
		/*
		 * If no possible move exists, we return null.
		 */
		if ( !possibleMoveExists( gb ) ) {
			return null;
		}
		
		/*
		 * Now we go through all possible moves and therby search the one
		 * which leads to a maximum of turned enemy stones.
		 */
		Coordinates bestCoord = null;
		Coordinates tempCoord = null;
		int maxTurned = 0;
		int tempTurned = 0;
		
		for ( int r=1; r<=gb.getSize(); r++ ) {
			for ( int c=1; c<=gb.getSize(); c++ ) {
				
				tempCoord = new Coordinates( r, c );
				if ( !checkMove( tempCoord, gb ) ) {
					continue;
				}
				
				tempTurned = getNumOfChanges( tempCoord, gb );
				
				if ( tempTurned > maxTurned ) {
					bestCoord = tempCoord;
					maxTurned = tempTurned;
				}
				
			}
		} 
		
		return bestCoord;
		
	} /* End of nextMove(). */
	
} /* End of class MaxPlayer. */
