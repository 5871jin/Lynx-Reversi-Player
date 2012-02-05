package reversi;




/**
 * Das Interface <code>GameBoard</code> stellt f�r Implementierungen von
 * <code>ReversiPlayer</code> die Schnittstelle zum Spielbrett dar. Zudem
 * definiert es die (int) Konstanten GameBoard.RED, GameBoard.GREEN und
 * GameBoard.EMPTY.
 */
public interface GameBoard
{

    /**
     * Diese Konstante symbolisiert eine leere Position auf dem Spielfeld. Sie
     * wird von der Methode getPosition() zur�ckgegeben, wenn sich an der
     * spezifizierten Position (noch) kein Spielstein befindet.
     */
    public static final int EMPTY = 0;

    /**
     * Diese Konstante symbolisiert einen roten Stein auf dem Spielfeld. Sie
     * wird von der Methode getPosition() zur�ckgegeben, wenn sich an der
     * spezifizierten Position ein roter Spielstein befindet.
     */
    public static final int RED = 1;

    /**
     * Diese Konstante symbolisiert einen gr�nen Stein auf dem Spielfeld. Sie
     * wird von der Methode getPosition() zur�ckgegeben, wenn sich an der
     * spezifizierten Position ein gr�ner Spielstein befindet.
     */
    public static final int GREEN = 2;

    /**
     * Gibt die Ausdehnung des Spielfeldes zur�ck. Das Spielfeld ist
     * 2-Dimensional und hat in beide Richtungen die selbe Ausdehnung.
     * @return 
     */
    public int getSize();

    /**
     * Gibt eine Konstante zur�ck, die den Spielstein an der spezifizierten
     * Position beschreibt. Die Spielsteine sind durch die Konstanten
     * GameBoard.RED (roter Spielstein), GameBoard.GREEN (gr�ner Spielstein) und
     * GameBoard.EMPTY (kein Spielstein) beschrieben.
     * 
     * @param coord
     *            Ein Coordinates-Objekt, welches die Position auf dem Spielfeld
     *            spezifiziert.
     * 
     * @return Eine der Konstanten: GameBoard.RED (roter Spielstein),
     *         GameBoard.GREEN (gr�ner Spielstein) und GameBoard.EMPTY (kein
     *         Spielstein).
     * 
     * @exception OutOfBoundsException
     *                Wird ausgel�st, wenn das Coordinates-Objekt
     *                <code>coord</code> eine ung�ltige Position beschreibt.
     *                G�ltige Werte sind: 1 <= row, col <= GameBoard.getSize().
     */
    public int getPosition(Coordinates coord) throws OutOfBoundsException;

} // GameBoard
