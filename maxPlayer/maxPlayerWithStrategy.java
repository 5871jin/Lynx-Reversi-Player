package maxPlayer;

import player.Player;
import reversi.Coordinates;
import reversi.GameBoard;

/**
 * Strategy: always play the move which will change the least fields to my own
 * color
 * TODO: in the end (last third) of
 * the game, minPlayer has to play like maxPlayer (play Coordinates that will
 * change the most fields)
 * TODO: => for this, we need a method to count, how
 * many fields are already played (=>in Player.java)
 * 
 * @author Benedikt Koeppel
 * @author Jonas Huber
 * 
 */
public class maxPlayerWithStrategy extends Player {
	
	
	/**
	 * TODO: if it's possible to get a large piece of a side under my control, take this
	 * 
	 * Creates arrays of fields and lets smallestNumOfChangeOfArray find the best coordinates 
	 * @see player.Player#nextMove(reversi.GameBoard)
	 */
	public Coordinates nextMove(GameBoard gb) {
		Coordinates bestCoord = null;
		
		/*
		 * coordinates, to check at once (maximum 64 coordinates)
		 */
		int checkCoords[][] = new int[64][2];
		int checkCoordsIdx = 0;
		
		/*
		 * corners
		 */
		checkCoords = addCheckCoords(checkCoords, checkCoordsIdx++, 1, 1);
		checkCoords = addCheckCoords(checkCoords, checkCoordsIdx++, 1, 8);
		checkCoords = addCheckCoords(checkCoords, checkCoordsIdx++, 8, 1);
		checkCoords = addCheckCoords(checkCoords, checkCoordsIdx++, 8, 8);
		bestCoord = coordsWithGreatestNumOfChangeOfArray(checkCoords, gb);
		if (bestCoord != null) {
			return bestCoord;
		}
		checkCoords = clearCheckCoords();
		checkCoordsIdx = 0;
		
		/*
		 * sweet 16
		 */
		for (int i=3; i<=6; i++) {
			for (int j=3; j<=6; j++) {
				checkCoords = addCheckCoords(checkCoords, checkCoordsIdx++, i, j);
			}
		}
		bestCoord = coordsWithGreatestNumOfChangeOfArray(checkCoords, gb);
		if (bestCoord != null) {
			return bestCoord;
		}
		checkCoords = clearCheckCoords();
		checkCoordsIdx = 0;
		
		
		/*
		 * inner borderlines
		 * and x fields corresponding to an already played corner
		 */
		for (int i=3; i<=6; i++) {
			checkCoords = addCheckCoords(checkCoords, checkCoordsIdx++, 2, i);
			checkCoords = addCheckCoords(checkCoords, checkCoordsIdx++, i, 2);
			checkCoords = addCheckCoords(checkCoords, checkCoordsIdx++, 7, i);
			checkCoords = addCheckCoords(checkCoords, checkCoordsIdx++, i, 7);
		}
		try {	/* if corner 1/1 is set, add x-field 2/2 */
			if ( gb.getPosition(new Coordinates(1,1)) != GameBoard.EMPTY ) {
				checkCoords = addCheckCoords(checkCoords, checkCoordsIdx++, 2, 2);
			}
		} catch (Exception e) {}	/* Exception can not occur, because Coordinates(1,1) are valid */
		try {	/* if corner 1/8 is set, add x-field 2/7 */
			if ( gb.getPosition(new Coordinates(1,8)) != GameBoard.EMPTY ) {
				checkCoords = addCheckCoords(checkCoords, checkCoordsIdx++, 2, 7);
			}
		} catch (Exception e) {}
		try {	/* if corner 8/1 is set, add x-field 7/2 */
			if ( gb.getPosition(new Coordinates(8,1)) != GameBoard.EMPTY ) {
				checkCoords = addCheckCoords(checkCoords, checkCoordsIdx++, 7, 2);
			}
		} catch (Exception e) {}
		try {	/* if corner 8/8 is set, add x-field 7/7 */
			if ( gb.getPosition(new Coordinates(8,8)) != GameBoard.EMPTY ) {
				checkCoords = addCheckCoords(checkCoords, checkCoordsIdx++, 7, 7);
			}
		} catch (Exception e) {}
		bestCoord = coordsWithGreatestNumOfChangeOfArray(checkCoords, gb);
		if (bestCoord != null) {
			return bestCoord;
		}
		checkCoords = clearCheckCoords();
		checkCoordsIdx = 0;
		
		/*
		 * outer borderlines and c-fields, if corner already played
		 */
		for (int i=3; i<=6; i++) {
			checkCoords = addCheckCoords(checkCoords, checkCoordsIdx++, 1, i);
			checkCoords = addCheckCoords(checkCoords, checkCoordsIdx++, i, 1);
			checkCoords = addCheckCoords(checkCoords, checkCoordsIdx++, 8, i);
			checkCoords = addCheckCoords(checkCoords, checkCoordsIdx++, i, 8);
		}
		try {	/* if corner 1/1 is set, add c-fields 1/2 and 2/1 */
			if ( gb.getPosition(new Coordinates(1,1)) != GameBoard.EMPTY ) {
				checkCoords = addCheckCoords(checkCoords, checkCoordsIdx++, 1, 2);
				checkCoords = addCheckCoords(checkCoords, checkCoordsIdx++, 2, 1);
			}
		} catch (Exception e) {}	/* Exception can not occur, because Coordinates(1,1) are valid */
		try {	/* if corner 8/1 is set, add c-fields 8/2 and 7/1 */
			if ( gb.getPosition(new Coordinates(1,1)) != GameBoard.EMPTY ) {
				checkCoords = addCheckCoords(checkCoords, checkCoordsIdx++, 8, 2);
				checkCoords = addCheckCoords(checkCoords, checkCoordsIdx++, 7, 1);
			}
		} catch (Exception e) {}
		try {	/* if corner 1/8 is set, add c-fields 1/7 and 2/8 */
			if ( gb.getPosition(new Coordinates(1,1)) != GameBoard.EMPTY ) {
				checkCoords = addCheckCoords(checkCoords, checkCoordsIdx++, 1, 7);
				checkCoords = addCheckCoords(checkCoords, checkCoordsIdx++, 2, 8);
			}
		} catch (Exception e) {}
		try {	/* if corner 8/8 is set, add c-fields 7/8 and 8/7 */
			if ( gb.getPosition(new Coordinates(1,1)) != GameBoard.EMPTY ) {
				checkCoords = addCheckCoords(checkCoords, checkCoordsIdx++, 7, 8);
				checkCoords = addCheckCoords(checkCoords, checkCoordsIdx++, 8, 7);
			}
		} catch (Exception e) {}

		bestCoord = coordsWithGreatestNumOfChangeOfArray(checkCoords, gb);
		if (bestCoord != null) {
			return bestCoord;
		}
		checkCoords = clearCheckCoords();
		checkCoordsIdx = 0;
		
		/*
		 * C-fields
		 */
		checkCoords = addCheckCoords(checkCoords, checkCoordsIdx++, 1, 2);
		checkCoords = addCheckCoords(checkCoords, checkCoordsIdx++, 2, 1);
		checkCoords = addCheckCoords(checkCoords, checkCoordsIdx++, 1, 7);
		checkCoords = addCheckCoords(checkCoords, checkCoordsIdx++, 2, 8);
		checkCoords = addCheckCoords(checkCoords, checkCoordsIdx++, 7, 1);
		checkCoords = addCheckCoords(checkCoords, checkCoordsIdx++, 8, 2);
		checkCoords = addCheckCoords(checkCoords, checkCoordsIdx++, 7, 8);
		checkCoords = addCheckCoords(checkCoords, checkCoordsIdx++, 7, 8);
		bestCoord = coordsWithGreatestNumOfChangeOfArray(checkCoords, gb);
		if (bestCoord != null) {
			return bestCoord;
		}
		checkCoords = clearCheckCoords();
		checkCoordsIdx = 0;

		
		/*
		 * X-fields
		 */
		checkCoords = addCheckCoords(checkCoords, checkCoordsIdx++, 2, 2);
		checkCoords = addCheckCoords(checkCoords, checkCoordsIdx++, 2, 7);
		checkCoords = addCheckCoords(checkCoords, checkCoordsIdx++, 7, 2);
		checkCoords = addCheckCoords(checkCoords, checkCoordsIdx++, 7, 7);
		bestCoord = coordsWithGreatestNumOfChangeOfArray(checkCoords, gb);
		if (bestCoord != null) {
			return bestCoord;
		}
		checkCoords = clearCheckCoords();
		checkCoordsIdx = 0;
		
		/*
		 * no possible field found
		 */
		return null;
	}
	

	
	/**
	 * Checks, if playing testCoord will change more slices than previousNumOfChanges
	 * @param previousNumOfChanges how many fields would the best move change?
	 * @param testCoord for which coordinates should i check, if they're better?
	 * @param gb on which gameboard?
	 * @return returns the maximum value of fields which will be changed. <br>
	 * 			returns 0 if testCoord is not a valid move <br>
	 * 			returns a negative number if testCoord would change less fields (i.e.) fields changed by testCoord * (-1) = return value<br>
	 * 			returns a positive number if testCoord would be a better (or equal) move 
	 */
	int greaterNumOfChanges(int previousNumOfChanges, Coordinates testCoord, GameBoard gb) {
		
		/*
		 * see if testCoord is a valid move
		 */
		if (checkMove(testCoord, gb)) {
			
			/*
			 * how many slices will be changed with this move?
			 */
			int numOfChanges = getNumOfChanges(testCoord, gb);
			
			/*
			 * if it will change less sliced than all previous moves
			 */
			if ( previousNumOfChanges == 0 || numOfChanges > previousNumOfChanges ) {
				return numOfChanges;
			} else {
				return (-1)*numOfChanges;
			}
		} else {
			/*
			 * if it isn't a valid move, just return the old value of how many slices will be changed
			 */
			return 0;
		}
	}
	
	/**
	 * Looks for the best move (which will change the most fields) out of the array of coordinates
	 * @param coords 2D-integer array (coordinates), which will be checked
	 * @param gb the gameboard :-)
	 * @return null, if no "best" move is found <br>
	 * returns the coordinates of the best move, if such one is found
	 */
	Coordinates coordsWithGreatestNumOfChangeOfArray( int coords[][], GameBoard gb ) {
		Coordinates bestCoord = null;
		int bestNumOfChanges = 0;
		
		Coordinates testCoord;
		int numOfChanges;
		for( int i=0; i<coords.length; i++ ) {
			testCoord = new Coordinates( coords[i][0], coords[i][1] );
			if (checkMove(testCoord, gb) ) {
				numOfChanges = greaterNumOfChanges(bestNumOfChanges, testCoord, gb);
				
				/*
				 * if bestNumOfChanges is still == 0, then take the tested coordinates as better ones ^^
				 * if numOfChanges > 0, greaterNumOfChanges has found a better or equal coordinate
				 * if numOfChanges is equal to bestNumOfChanges, it was not a better coordinate, only equal
				 */
				if (bestNumOfChanges == 0  ||  ( (numOfChanges > 0) && (numOfChanges != bestNumOfChanges) )) {
					bestCoord = testCoord;
					bestNumOfChanges = numOfChanges;
				} else if ( numOfChanges == bestNumOfChanges ) {
					/*
					 * TODO: here we can choose randomly (?) the previous bestCoord, or the new testCoord as new bestCoord, because they'll change both the same amount of slides
					 */
				}
			}
		}
		return bestCoord;
	}


	/**
	 * Adds a pair (i,j) to array at position idx
	 * @param coords array, in which i and j should be set
	 * @param idx position, where i and j should be set
	 * @param i coordinate i
	 * @param j coordinate j
	 * @return returns coords, but with i and j at position idx
	 */
	int[][] addCheckCoords(int[][] coords, int idx, int i, int j) {
		coords[idx][0] = i;
		coords[idx][1] = j;
		return coords;
	}
	
	/**
	 * Generates a new, empty 2D-array for checkCoords
	 * @return returns a int-array in 2 dimensions (64x2)
	 */
	int[][] clearCheckCoords() {
		return new int[64][2];
	}

}
