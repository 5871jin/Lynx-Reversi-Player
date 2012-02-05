package reversi;


/**
 * Implementation eines Reversi-Spielbretts inklusive nützlicher
 * Hilfsfunktionen.
 * 
 * @see Arena
 */
public class TextGameBoard implements GameBoard
{

    // Horizontal/vertical size of game board. Note that the the actual size is
    // SIZE-2! This trick is done to simplify the implementation of
    // isMoveAvailable()
    private static final int SIZE = 10;

    // fields for the board
    private int[][] theBoard = new int[SIZE][SIZE];

    // other private fields
    private int filled; // number of tokens on the board
    private boolean left, upleft, up, upright;
    private boolean right, downright, down, downleft;
    // used in makeMove()
    private int lastRow = -1, lastCol = -1;

    /** Stores the Coordinates of the last call to <code>checkMove</code>.* */
    private Coordinates lastCheckMove = null;

    /**
     * Erstellt eine neue Instanz von TextGameBoard, initialisiert mit der bei
     * Reversi üblichen Startsituation aus 4 Steinen.
     */
    public TextGameBoard()
    {
        initBoard();
    }

    /**
     * Erstellt eine neue Instanz von TextGameBoard, die eine genaue Kopie der
     * übergebenen Spielsituation <code>gb</code> repräsentiert.
     * 
     * @param gb
     *            eine gegebene Spielsituation
     */
    public TextGameBoard(GameBoard gb)
    {
        updateBoard(gb);
    }

    public int getSize()
    {
        return SIZE - 2;
    } // getSize()

    /**
     * Check whether a given coordinate is valid.
     * 
     * @param c
     *            Coordinate to be verified.
     * @return true if it is within the bounds of the game board, false
     *         otherwise.
     */
    private boolean validCoordinates(Coordinates c)
    {

        return (c != null && validCoordinates(c.getCol(), c.getRow()));

        /*
         * return (c != null && c.getRow() > 0 && c.getRow() < SIZE-1 &&
         * c.getCol() > 0 && c.getCol() < SIZE-1);
         */
    } 

    /**
     * Check whether a given coordinate is valid.
     * 
     * @param x
     *            The value for the first coordinate (the column).
     * @param y
     *            The value for the second coordinate (the row).
     * @return true if it is within the bounds of the game board, false
     *         otherwise.
     */
    private boolean validCoordinates(int x, int y)
    {
        return (x > 0 && x < SIZE - 1 && y > 0 && y < SIZE - 1);
    } // validCoordinates()

    /**
     * Setzt das Spielbrett auf die bei Reversi üblichen Startsituation aus 4
     * Steinen zurück.
     */
    private void initBoard()
    {
        for (int i = 0; i < SIZE; i++)
        {
            for (int j = 0; j < SIZE; j++)
            {
                theBoard[i][j] = EMPTY;
            }
        }

        theBoard[4][4] = RED;
        theBoard[5][4] = RED;
        theBoard[4][5] = GREEN;
        theBoard[5][5] = GREEN;
        filled = 4;
    } // initBoard

    /**
     * Kopiert die als Parameter übergebene Spielsituation in die aktuelle 
     * Instanz.
     * @param gb 
     */
    public void updateBoard(GameBoard gb)
    {
        try
        {
            for (int x = 1; x <= getSize(); x++)
            {
                for (int y = 1; y <= getSize(); y++)
                {
                    Coordinates c = new Coordinates(x, y);
                    int color = gb.getPosition(c);
                    theBoard[y][x] = color;
                    // BUGFIX: Increase stone counter accordingly
                    if ( color != EMPTY )
                    {
                        filled++;
                    }
                }
            }
        }
        catch (OutOfBoundsException e)
        {
            System.err.println("TextGameBoard.updateBoard: internal error");
            e.printStackTrace();
        }
    }

    /**
     * Ermöglicht es, ein Feld der in dieser Instanz gespeicherten
     * Spielsituation abzufragen.
     * 
     * @param c
     *            die Koordinaten des Spielfelds, welches abgefragt wird.
     * @return Entweder GameBoard.RED, GameBoard.GREEN oder GameBoard.EMPTY.
     * @exception OutOfBoundsException
     *                wenn sich die in <code>c<code> angegebenen Koordinaten 
     * ausserhalb des Spielfelds befinden
     */
    public int getPosition(Coordinates c) throws OutOfBoundsException
    {
        if (validCoordinates(c))
        {
            return theBoard[c.getCol()][c.getRow()];
        }
        else
        {
            throw (new OutOfBoundsException(c.toString(), c));
        }
    } // getPosition()

    /**
     * Wie {@link #getPosition(Coordinates)}, lässt aber als Parameter ein
     * (x,y)-Koordinatenpaar zu. Kann alternativ zu
     * <code>getPosition(Coordinates)</code> benutzt werden.
     * 
     * @throws OutOfBoundsException
     * @see #getPosition(Coordinates)
     */
    protected int getPosition(int x, int y)
    {
        return theBoard[x][y];
    } 

    /**
     * Hilfsfunktion für isMoveAvailable() und checkMove(). Überprüft ob in eine
     * gegebene Richtung erst Steine das Gegners und dann eigene Steine folgen.
     * Die Richtung wird mittels des Vektors (i,j) angegeben, welche jeweils -1,
     * 0, or 1 enthalten können (es gibt insgesamt 9 Richtungen). Die Methode
     * nimmt an, dass gültige (i,j) angegeben werden.
     * 
     * @param i
     *            x-Richtung des Vektors (kann nur -1, 0 oder 1 gesetzt werden)
     * @param j
     *            y-Richtung des Vektors (kann nur -1, 0 oder 1 gesetzt werden)
     */
    private boolean check(int player, int i, int j, int iinc, int jinc)
    {
        int other = 3 - player;
        int k, l;
        boolean once = false;

        k = i + iinc;
        l = j + jinc;
        while (theBoard[k][l] == other)
        {
            k += iinc;
            l += jinc;
            once = true;
        }
        // while should be done at least once!
        return (once && theBoard[k][l] == player);
    } // check()

    /**
     * Hilfsfunktion für makeMove().
     */
    private void flip(int player, int i, int j, int iinc, int jinc)
    {
        int other = 3 - player;
        int k, l;

        k = i + iinc;
        l = j + jinc;
        while (theBoard[k][l] == other)
        {
            theBoard[k][l] = player;
            k += iinc;
            l += jinc;
        }
    } // flip()

    /**
     * Gibt <code>true</code> zurück, wenn ein gültiger Zug für den
     * übergebenen Spieler existiert.
     * 
     * @param player
     *            Spezifiziert den Spieler (entweder GameBoard.RED oder
     *            GameBoard.GREEN), für den überprüft werden soll.
     * 
     * @return <code>True</code> falls ein gültiger Zug existiert,
     *         <code>false</code> sonst.
     */
    public boolean isMoveAvailable(int player)
    {
        // int other = 3 - player;

        for (int i = 1; i < SIZE - 1; i++)
        {
            for (int j = 1; j < SIZE - 1; j++)
            {
                if (theBoard[i][j] == EMPTY)
                {
                    for (int a = -1; a < 2; a++)
                    {
                        for (int b = -1; b < 2; b++)
                        {
                            if (check(player, i, j, a, b))
                            {
                                return true;
                            }
                        }
                    }
                }
            }
        }

        // else not found
        return false;
    } // isMoveAvailable()

    /**
     * Diese Methode überprüft ob der Zug <code>c</code> einen gültigen Zug
     * für den Spieler <code>player</code> darstellt.
     * 
     * @param player
     *            der Spieler, entweder <code>GameBoard.RED</code> or
     *            <code>GameBoard.GREEN</code>.
     * @param c
     *            die Koordinaten, die überprüft werden sollen.
     * @return <code>true</code>, wenn Zug gültig
     */
    public boolean checkMove(int player, Coordinates c)
    {
        lastCheckMove = c;
        if (c == null)
        {
            return !isMoveAvailable(player);
        }
        int i = c.getCol(), j = c.getRow();

        if (!validCoordinates(c) || !(theBoard[i][j] == EMPTY))
            return false;
        left = check(player, i, j, -1, 0);
        upleft = check(player, i, j, -1, -1);
        up = check(player, i, j, 0, -1);
        upright = check(player, i, j, 1, -1);
        right = check(player, i, j, 1, 0);
        downright = check(player, i, j, 1, 1);
        down = check(player, i, j, 0, 1);
        downleft = check(player, i, j, -1, 1);
        return (left || upleft || up || upright || right || downright || down || downleft);
    } // checkMove()

    // * ALT, DOC: Important: assumes that checkMove()
    // * has been called before with the same move and player. This makes
    // * a more efficient implementation possible.

    /**
     * Diese Methode setzt einen Stein für den angegebenen Spieler auf dem
     * angegebenen Feld, und dreht Steine des Gegners gemäss den Regeln um.
     * WICHTIG: In der gegenwärtigen Implementation von
     * <code>TextGameBoard</code> muss einem Aufruf von <code>makeMove</code>
     * immer ein Aufruf von <code>checkMove</code> mit den gleichen
     * Koordinaten vorausgehen. Nachfolgend ein Beispiel:
     * 
     * <p>
     * <code>
     * public Coordinates nextMove( GameBoard gb ) { <br>
     * <blockquote><code>
     *      // ... <br>
     *      TextGameBoard tgb=new TextGameBoard(gb); <br>
     *      Coordinates c=new Coordinates(3,4); <br>
     *      if(tgb.checkMove(GameBoard.GREEN,c)) {<br>
     *      <blockquote><code>
     *      tgb.makeMove(GameBoard.GREEN,c); <br>
     *      </code></blockquote> <code>}</code><br>
     * </code></blockquote>
     * </code>
     * </p>
     * 
     * Dies ermöglicht eine effizientere Implementation von
     * <code>TextGameBoard</code>: In <code>makeMove</code> können
     * Berechnungsschritte eingespart werden, die in <code>checkMove</code>
     * schon gemacht worden sind, allerdings nur, wenn sich die Aufrufe auf das
     * gleiche Spielfeld beziehen (wie z.B. oben auf das Spielfeld
     * <code>c</code>).
     * 
     * @param player
     *            der Spieler für den ein Stein gesetzt werden soll, entweder
     *            GameBoard.RED oder GameBoard.GREEN
     * @param c
     *            Koordinaten, wo der Stein gesetzt werden soll
     * @throws UnsupportedMethodInvocationException
     *             falls vor dem Aufruf von <code>makeMove</code> kein
     *             entsprechender Aufruf <code>checkMove</code> erfolgt ist
     */
    public void makeMove(int player, Coordinates c)
    {

        if (c == null)
        {
            System.out.println("Monitor: Move skipped ...");
            return;
        }

        if (!c.equals(lastCheckMove))
            throw new UnsupportedMethodInvocationException(c, lastCheckMove);

        int i = c.getCol(), j = c.getRow();

        theBoard[i][j] = player;
        filled++;

        // redraw last move in non-highlighted color
        // if( lastRow != -1 )
        // canvas.drawPiece(lastRow, lastCol);
        // lastRow = i;
        // lastCol = j;

        if (left)
            flip(player, i, j, -1, 0);
        if (upleft)
            flip(player, i, j, -1, -1);
        if (up)
            flip(player, i, j, 0, -1);
        if (upright)
            flip(player, i, j, 1, -1);
        if (right)
            flip(player, i, j, 1, 0);
        if (downright)
            flip(player, i, j, 1, 1);
        if (down)
            flip(player, i, j, 0, 1);
        if (downleft)
            flip(player, i, j, -1, 1);

    } // makeMove()

    /**
     * Überprüft, ob das Spielfeld voll ist.
     * 
     * @return true wenn ja, false sonst.
     */
    public boolean isFull()
    {
        return (filled == (SIZE - 2) * (SIZE - 2));
    } // isFull()

    /**
     * Zählt die Anzahl der Steine für einen gegebenen Spieler.
     * 
     * @return die Anzahl der gezählten Steine
     * @param player
     *            der Spieler
     */
    public int countStones(int player)
    {
        int count = 0;

        for (int i = 1; i < SIZE - 1; i++)
        {
            for (int j = 1; j < SIZE - 1; j++)
            {
                if (theBoard[i][j] == player)
                {
                    count++;
                }
            }
        }

        return count;
    } // countStones()

    /**
     * Erstellt einen mehrzeiligen String, der eine Textdarstellung der in
     * dieser Instanz gespeicherten Spielsituation enthält.
     */
    public String toString()
    {
        String gb = " |1|2|3|4|5|6|7|8|\n";

        for (int i = 1; i < SIZE - 1; i++)
        {
            gb = gb + i;
            for (int j = 1; j < SIZE - 1; j++)
            {
                if (theBoard[j][i] == GameBoard.RED)
                {
                    gb = gb + "|r";
                }
                else if (theBoard[j][i] == GameBoard.GREEN)
                {
                    gb = gb + "|g";
                }
                else
                {
                    gb = gb + "| ";
                }
            }
            gb = gb + "|\n";
        }
        gb = gb + "\n";

        return gb;
    }

} // TextGameBoard
