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
     * Übergibt dem Spieler seine zugewiesene Farbe und die für beide Spieler
     * identische Zeitbeschränkung in Millisekunden. Die Methode wird ein
     * einziges Mal zu Beginn eines Spieles aufgerufen.
     * 
     * @param myColor
     *            Die diesem Spieler von der Arena zugewiesene Farbe der
     *            Spielsteine, entweder GameBoard.RED für rote Spielsteine oder
     *            GameBoard.GREEN für grüne.
     * 
     * @param timeLimit
     *            Die maximale Zeit, die eine Implementierung des Interfaces für
     *            das Bearbeiten der Methode nextMove() benötigen darf,
     *            angegeben in Millisekunden.
     */
    public void initialize(int myColor, long timeLimit);

    /**
     * Berechnet auf der Basis des übergebenen Spielfeldes den nächsten
     * Spielzug. Die Methode wird von der Reversi-Arena abwechselnd für jeden
     * Spieler aufgerufen. Die Methode muss eine Instanz der Klasse Coordinates
     * zurückgegeben, die die Position symbolisiert, an der der nächste
     * Spielstein dieses Spielers platziert werden soll. Falls kein Zug möglich
     * ist muss <code>null</code> zurückgeben werden. Implementierungen, die
     * einen ungültigen Zug zurückliefern oder die für die Berechnung des Zuges
     * länger als <code>timeLimit</code> ms benötigen, werden disqualifiziert.
     * 
     * @param gb
     *            Beschreibt die augenblickliche Spielsituation.
     * 
     * @return Einen gültigen Zug, dessen Koordinaten innerhalb der Grenzen des
     *         Spielfeldes liegen, wenn ein Zug möglich ist, sonst
     *         <code>null</code>.
     */
    public Coordinates nextMove(GameBoard gb);

} 
