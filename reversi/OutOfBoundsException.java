package reversi;


/**
 * Diese Exception wird ausgelöst, wenn versucht wird, auf eine ungültige
 * Spielfeldposition zuzugreifen. Gültige Werte für Positionen auf dem Spielfeld
 * sind: 1 <= Zeile bzw. Spalte <= GameBoard.getSize().
 * 
 * @see reversi.GameBoard
 * @see reversi.TextGameBoard
 */
public class OutOfBoundsException extends Exception
{
    private static final long serialVersionUID = 1L;

    /** Enthält die "illegale" Koordinate */
    public Coordinates coord; 

    /**
     * @param msg
     * @param c
     */
    public OutOfBoundsException(String msg, Coordinates c)
    {
        super(msg);
        coord = new Coordinates( c.getRow(), c.getCol() );
    }
    
    /**
     * @param msg
     * @param row
     * @param col
     */
    public OutOfBoundsException(String msg, int row, int col)
    {
        super(msg);
        coord = new Coordinates( row, col );
    }   
}
