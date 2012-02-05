package reversi;


/**
 * Interface, das von jedem Reversispieler implementiert werden muss.
 */
public interface ReversiPlayer
{

    /**
     * Not a stateless player, initialized once.
     */

    /**
     * �bergibt dem Spieler seine zugewiesene Farbe und die f�r beide Spieler
     * identische Zeitbeschr�nkung in Millisekunden. Die Methode wird ein
     * einziges Mal zu Beginn eines Spieles aufgerufen.
     * 
     * @param myColor
     *            Die diesem Spieler von der Arena zugewiesene Farbe der
     *            Spielsteine, entweder GameBoard.RED f�r rote Spielsteine oder
     *            GameBoard.GREEN f�r gr�ne.
     * 
     * @param timeLimit
     *            Die maximale Zeit, die eine Implementierung des Interfaces f�r
     *            das Bearbeiten der Methode nextMove() ben�tigen darf,
     *            angegeben in Millisekunden.
     */
    public void initialize(int myColor, long timeLimit);

    /**
     * Berechnet auf der Basis des �bergebenen Spielfeldes den n�chsten
     * Spielzug. Die Methode wird von der Reversi-Arena abwechselnd f�r jeden
     * Spieler aufgerufen. Die Methode muss eine Instanz der Klasse Coordinates
     * zur�ckgegeben, die die Position symbolisiert, an der der n�chste
     * Spielstein dieses Spielers platziert werden soll. Falls kein Zug m�glich
     * ist muss <code>null</code> zur�ckgeben werden. Implementierungen, die
     * einen ung�ltigen Zug zur�ckliefern oder die f�r die Berechnung des Zuges
     * l�nger als <code>timeLimit</code> ms ben�tigen, werden disqualifiziert.
     * 
     * @param gb
     *            Beschreibt die augenblickliche Spielsituation.
     * 
     * @return Einen g�ltigen Zug, dessen Koordinaten innerhalb der Grenzen des
     *         Spielfeldes liegen, wenn ein Zug m�glich ist, sonst
     *         <code>null</code>.
     */
    public Coordinates nextMove(GameBoard gb);

} 
