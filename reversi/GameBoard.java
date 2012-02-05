package reversi;




/**
 * Das Interface <code>GameBoard</code> stellt für Implementierungen von
 * <code>ReversiPlayer</code> die Schnittstelle zum Spielbrett dar. Zudem
 * definiert es die (int) Konstanten GameBoard.RED, GameBoard.GREEN und
 * GameBoard.EMPTY.
 */
public interface GameBoard
{

    /**
     * Diese Konstante symbolisiert eine leere Position auf dem Spielfeld. Sie
     * wird von der Methode getPosition() zurückgegeben, wenn sich an der
     * spezifizierten Position (noch) kein Spielstein befindet.
     */
    public static final int EMPTY = 0;

    /**
     * Diese Konstante symbolisiert einen roten Stein auf dem Spielfeld. Sie
     * wird von der Methode getPosition() zurückgegeben, wenn sich an der
     * spezifizierten Position ein roter Spielstein befindet.
     */
    public static final int RED = 1;

    /**
     * Diese Konstante symbolisiert einen grünen Stein auf dem Spielfeld. Sie
     * wird von der Methode getPosition() zurückgegeben, wenn sich an der
     * spezifizierten Position ein grüner Spielstein befindet.
     */
    public static final int GREEN = 2;

    /**
     * Gibt die Ausdehnung des Spielfeldes zurück. Das Spielfeld ist
     * 2-Dimensional und hat in beide Richtungen die selbe Ausdehnung.
     * @return 
     */
    public int getSize();

    /**
     * Gibt eine Konstante zurück, die den Spielstein an der spezifizierten
     * Position beschreibt. Die Spielsteine sind durch die Konstanten
     * GameBoard.RED (roter Spielstein), GameBoard.GREEN (grüner Spielstein) und
     * GameBoard.EMPTY (kein Spielstein) beschrieben.
     * 
     * @param coord
     *            Ein Coordinates-Objekt, welches die Position auf dem Spielfeld
     *            spezifiziert.
     * 
     * @return Eine der Konstanten: GameBoard.RED (roter Spielstein),
     *         GameBoard.GREEN (grüner Spielstein) und GameBoard.EMPTY (kein
     *         Spielstein).
     * 
     * @exception OutOfBoundsException
     *                Wird ausgelöst, wenn das Coordinates-Objekt
     *                <code>coord</code> eine ungültige Position beschreibt.
     *                Gültige Werte sind: 1 <= row, col <= GameBoard.getSize().
     */
    public int getPosition(Coordinates coord) throws OutOfBoundsException;

} // GameBoard
