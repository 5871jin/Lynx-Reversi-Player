package reversi;


/**
 * Diese Exception wird bei einem nicht unterstützten Aufruf von
 * {@link TextGameBoard#makeMove(int, Coordinates)} ausgelöst. In der
 * gegenwärtigen Implementation von <code>TextGameBoard</code> muss einem
 * Aufruf von <code>makeMove</code> immer ein Aufruf von
 * <code>checkMove</code> mit den gleichen Koordinaten vorausgehen.
 * Nachfolgend ein Beispiel eines möglichen Aufrufs:
 * 
 * <code>
 *      TextGameBoard t=new TextGameBoard();
 *      Coordinates c=new Coordinates(3,4);
 *      if(t.checkMove(c)) {
 *          t.makeMove(c);
 *      }
 * </code>
 * 
 * Dies ermöglicht eine effizientere Implementation von
 * <code>TextGameBoard</code>: In <code>makeMove</code> können
 * Berechnungsschritte eingespart werden, die in <code>checkMove</code> schon
 * gemacht worden sind, allerdings nur, wenn sich die Aufrufe auf das gleiche
 * Spielfeld beziehen (wie z.B. oben auf das Spielfeld <code>c</code>).
 * 
 * @author chfrank
 */
public class UnsupportedMethodInvocationException extends RuntimeException
{

    private static final long serialVersionUID = 1L;

    /**
     * Erstellt eine UnsupportedMethodInvocationException.
     * 
     * @param makeMoveCoord
     *            Koordinaten die beim Aufruf von makeMove verwendet wurden
     * @param checkMoveCoord
     *            Koordinaten die beim letzten Aufruf von checkMove verwendet
     *            wurden
     */
    UnsupportedMethodInvocationException(Coordinates makeMoveCoord,
            Coordinates checkMoveCoord)
    {
        super("Es wurde makeMove(" + makeMoveCoord
                + ") aufgerufen, ohne dass zuvor " + "checkMove("
                + makeMoveCoord + ") aufgerufen wurde. "
                + "Diese Benutzung wird nicht unterstützt, siehe Javadoc.");
    }
}
