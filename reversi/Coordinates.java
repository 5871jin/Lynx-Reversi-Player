package reversi;


/**
 * Die Klasse Coordinates beschreibt eine Position auf dem Spielfeld. Sie wird
 * von den Methoden <em>nextMove()</em> (Interface <em>ReversiPlayer</em>)
 * und <em>getPosition()</em> (Interface <em>GameBoard</em>) genutzt. Der
 * Ursprung des Koordinatensystems liegt in der linken oberen Ecke. G�ltige
 * Werte f�r die Koordinaten sind <code>1</code> bis
 * <code>GameBoard.getSize()</code> (beides einschlie�lich).<br/><br/>
 * 
 * <b>Achtung:</b> Im Gegensatz zu der sonst �blichen Konvention wird in einer
 * <code>Coordinates</code>-Angabe zuerst der y-Wert und dann der x-Wert
 * festgehalten!
 */
public class Coordinates
{
    private int row, col;

    /**
     * Erzeugt eine neues Coordinates-Objekt, das die Position <code>row</code>
     * (Zeile), <code>col</code> (Spalte) auf dem Spielfeld beschreibt.
     * G�ltige Werte f�r <code>row</code> und <code>col</code> sind
     * <code>1</code> bis <code>GameBoard.getSize()</code> (beides
     * einschlie�lich).
     * @param row 
     * @param col 
     */
    public Coordinates(int row, int col)
    {
        this.row = row;
        this.col = col;
    }

    /**
     * Liefert die Zeile der durch das Objekt beschriebenen Position zur�ck.
     * @return 
     */
    public int getRow()
    {
        return row;
    }

    /**
     * Liefert die Spalte der durch das Objekt beschriebenen Position zur�ck.
     * @return 
     */
    public int getCol()
    {
        return col;
    }

    /**
     * Liefert eine String-Darstellung der duch das Objekt beschriebenen
     * Position zur�ck, z.B.:&nbsp;<code>Coordinates( 4, 7
     * )</code>.
     */
    public String toString()
    {
        return "Coordinates( " + row + ", " + col + " )";
    }

    /**
     * �berpr�ft, ob diese <code>Coordinates</code>-Instanz das gleiche
     * Spielfeld bezeichnet wie der gegebene Parameter <code>o</code>.
     * 
     * @return Gibt <code>true</code> zur�ck, falls <code>o</code> eine
     *         Instanz von <code>Coordinates</code> ist und das gleiche
     *         Spielfeld bezeichnet.
     */
    public boolean equals(Object o)
    {
        if (o != null && o instanceof Coordinates)
        {
            Coordinates c = (Coordinates) o;
            return row == c.row && col == c.col;
        }
        else
        {
            return false;
        }
    }
}
