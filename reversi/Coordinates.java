package reversi;


/**
 * Die Klasse Coordinates beschreibt eine Position auf dem Spielfeld. Sie wird
 * von den Methoden <em>nextMove()</em> (Interface <em>ReversiPlayer</em>)
 * und <em>getPosition()</em> (Interface <em>GameBoard</em>) genutzt. Der
 * Ursprung des Koordinatensystems liegt in der linken oberen Ecke. Gültige
 * Werte für die Koordinaten sind <code>1</code> bis
 * <code>GameBoard.getSize()</code> (beides einschließlich).<br/><br/>
 * 
 * <b>Achtung:</b> Im Gegensatz zu der sonst üblichen Konvention wird in einer
 * <code>Coordinates</code>-Angabe zuerst der y-Wert und dann der x-Wert
 * festgehalten!
 */
public class Coordinates
{
    private int row, col;

    /**
     * Erzeugt eine neues Coordinates-Objekt, das die Position <code>row</code>
     * (Zeile), <code>col</code> (Spalte) auf dem Spielfeld beschreibt.
     * Gültige Werte für <code>row</code> und <code>col</code> sind
     * <code>1</code> bis <code>GameBoard.getSize()</code> (beides
     * einschließlich).
     * @param row 
     * @param col 
     */
    public Coordinates(int row, int col)
    {
        this.row = row;
        this.col = col;
    }

    /**
     * Liefert die Zeile der durch das Objekt beschriebenen Position zurück.
     * @return 
     */
    public int getRow()
    {
        return row;
    }

    /**
     * Liefert die Spalte der durch das Objekt beschriebenen Position zurück.
     * @return 
     */
    public int getCol()
    {
        return col;
    }

    /**
     * Liefert eine String-Darstellung der duch das Objekt beschriebenen
     * Position zurück, z.B.:&nbsp;<code>Coordinates( 4, 7
     * )</code>.
     */
    public String toString()
    {
        return "Coordinates( " + row + ", " + col + " )";
    }

    /**
     * Überprüft, ob diese <code>Coordinates</code>-Instanz das gleiche
     * Spielfeld bezeichnet wie der gegebene Parameter <code>o</code>.
     * 
     * @return Gibt <code>true</code> zurück, falls <code>o</code> eine
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
