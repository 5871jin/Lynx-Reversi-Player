package reversi;


import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;


class ServerThread extends Thread
{
    ServerSocket sock = null;
    int port;
    Arena arenaMaster;

    /**
     * @param prt
     * @param a
     */
    public void init(int prt, Arena a)
    {
        port = prt;
        arenaMaster = a;
    }

    public void run()
    {
        try
        {
            sock = new ServerSocket(port);
            System.out.println("server thread is waiting for connections");
            while (!isInterrupted())
            {
                Socket client = sock.accept();
                System.out.println("*** new connection accepted");
                PrintWriter pw = new PrintWriter(client.getOutputStream());
                // send them what we've got so far
                pw.print(arenaMaster.getBacklog());
                // keep them updated
                arenaMaster.addLogStream(pw);
            }
            sock.close();
        }
        catch (IOException e)
        {
            System.out.println("Could not open socket on port " + port + ": "
                    + e);
            return;
        }
    }
}


/**
 * Die Klasse Arena führt ein Spiel zwischen zwei vom Benutzer an der
 * Kommandozeile angegebenen Spielern aus. Sie ist die Hauptklasse des
 * Reversi-Frameworks, deren <code>main</code>-Methode ein Spiel beginnt.
 * 
 * Aufrufsyntax des Programms <tt>Arena</tt>: <small>
 * 
 * <pre>
 * java -classpath . reversi.Arena [options] GameName package1.myclass1 package2.myclass2
 * </pre>
 * 
 * </small>
 * 
 * <p>
 * Der obige Aufruf startet ein Spiel zwischen zwei Computerspielern
 * <tt>package1.myclass1</tt> und <tt>package2.myclass2</tt>. Dabei
 * bezeichnet <tt>myclassX</tt> die Klasse, die das Interface
 * <tt>ReversiPlayer</tt> implementiert. <tt>packageX</tt> bezeichnet das
 * Package, in dem die Spielerklasse implementiert ist.
 * </p>
 * 
 * <p>
 * <tt>GameName</tt> ist ein beliebiger Name für das Spiel. Der Name wird beim
 * Schreiben von Spielinformationen in eine Log-Datei verwendet. Das erlaubt das
 * spätere Wiederfinden von einzelnen Spielen in einer Log-Datei, die mehrere
 * Spiele enthält.
 * </p>
 * Folgende Optionen können verwendet werden:
 * 
 * <pre>
 * -?            Hilfe anzeigen
 * -t timeout    Maximale Zugdauer in Millisekunden (0 für unbeschränkt)
 * -d delay      Verzögerung zwischen Zügen; wichtig nur zur Animation
 * -e exittime   Verzögerung, bis das Programm nach Spielende stoppt
 * -l logfile    Name der Log-Datei für das Protokollieren des Spiels
 * -s            keine graphische Oberfläche anzeigen
 * </pre>
 * 
 * 
 * 
 */

public class Arena
{
    protected static final int BOARD_LENGTH = ProgramParameters.BOARD_LENGTH;
    protected static final int TIME_TOLERANCE = ProgramParameters.TIME_TOLERANCE;

    int timeBeforeExit = 10000;

    String statusText = "";

    ReversiPlayer player_red;
    ReversiPlayer player_green;
    long timeOut;
    String name_red;
    String name_green;

    boolean verbose = false;
    String logFile;
    String matchId;

    ServerThread server_thread = null;
    Vector logStreams = null;
    StringBuffer backlog = null;

    protected ProgramParameters params;
    protected Thread performerThread;
    GameState state = new GameState();

    Visualization visualization;

    /**
     * @param params
     * @param vis
     */
    public Arena(ProgramParameters params, Visualization vis)
    {
        backlog = new StringBuffer();
        logStreams = new Vector();
        server_thread = new ServerThread();
        server_thread.init(5454, this);
        server_thread.start();

        this.params = params;

        this.logFile = params.getLogfile();
        this.matchId = params.getGameId();

        String pr = params.getRedName();
        String pg = params.getGreenName();
        long timeOut = params.getTimeout();
        // String logFile = params.getLogfile();
        // delay = (int) params.getDelay();

        timeBeforeExit = (int) params.getTimeBeforeExit();

        player_red = instantiatePlayer(pr);
        player_green = instantiatePlayer(pg);

        writeToLog("new game");
        writeToLog("red=" + pr + " green=" + pg);

        visualization = vis;

        if (!params.getSilent())
        {
            visualization.init(this);
        }
        this.timeOut = timeOut;

        // initialize both players
        player_red.initialize(GameBoard.RED, timeOut);
        player_green.initialize(GameBoard.GREEN, timeOut);

        name_red = pr.substring(pr.lastIndexOf('.') + 1);
        name_green = pg.substring(pg.lastIndexOf('.') + 1);

        if (!params.getSilent())
        {

            visualization.setInfoLine("delay=" + params.getDelay());
            visualization.setInfoLine2(name_red + " (red) vs " + name_green
                    + " (green)");
        }

        writeToLog("initialized");
    }

    boolean onBoard(int r, int c)
    {
        return r >= 0 && r < BOARD_LENGTH && c >= 0 && c < BOARD_LENGTH;
    }

    int direction[] = new int[] { -1, -1, -1, 0, -1, 1, 0, -1, 0, 1, 1, -1, 1,
            0, 1, 1 };

    /**
     * This routine computes which positions on the board have to be flipped
     * when a certain move is made. The move is specified by the position
     * <code>pos</code> and the player color <code>color</code>. The array
     * <code>flipped</code> will be filled with the result. A "0" indicates no
     * flip, while a "1" indicates that the stone at this position will be
     * flipped when the corresponding move is made.
     */
    void computeTokensToFlip(GameBoard gb, Coordinates pos, int color,
            int[][] toFlip)
    {
        int drow, dcol;
        int opposite = color == GameBoard.RED ? GameBoard.GREEN : GameBoard.RED;
        boolean oneFlipped;
        int[][] currentBoard = new int[BOARD_LENGTH][BOARD_LENGTH];
        int row = pos.getRow() - 1;
        int col = pos.getCol() - 1;

        try
        {
            for (int i = 1; i <= BOARD_LENGTH; i++)
            {
                for (int j = 1; j <= BOARD_LENGTH; j++)
                {
                    currentBoard[i - 1][j - 1] = gb
                            .getPosition(new Coordinates(i, j));
                    toFlip[i - 1][j - 1] = 0;
                }
            }
        }
        catch (OutOfBoundsException e)
        {
            System.out.println("I checked an illegal position. I am sorry.");
        }

        for (int idx = 0; idx < direction.length; idx += 2)
        {
            int delta_row = direction[idx];
            int delta_col = direction[idx + 1];
            drow = row + delta_row;
            dcol = col + delta_col;

            oneFlipped = false;
            while (onBoard(drow, dcol) && currentBoard[drow][dcol] == opposite)
            {
                oneFlipped = true;
                drow += delta_row;
                dcol += delta_col;
            }

            if (onBoard(drow, dcol) && oneFlipped
                    && currentBoard[drow][dcol] == color)
            {
                drow = row + delta_row;
                dcol = col + delta_col;
                while (currentBoard[drow][dcol] == opposite)
                {
                    toFlip[drow][dcol] = 1;
                    drow += delta_row;
                    dcol += delta_col;
                }
            }
        }
    }

    Coordinates performMove(int player, TextGameBoard board)
            throws InterruptedException, IllegalMoveException,
            TimeExceededException
    {
        Coordinates currentMove = null;
        int[][] toFlip = new int[BOARD_LENGTH][BOARD_LENGTH];
        boolean legalMove;
        statusText = "";

        if (player != GameBoard.RED && player != GameBoard.GREEN)
        {
            throw new IllegalArgumentException("Player has to be RED or GREEN!");
        }

        String playerName;
        String playerColor;
        ReversiPlayer reversiPlayer;

        if (player == GameBoard.RED)
        {
            playerColor = "Red";
            playerName = name_red;
            reversiPlayer = player_red;
        }
        else
        {
            playerColor = "Green";
            playerName = name_green;
            reversiPlayer = player_green;
        }

        if (!params.getSilent() && params.getAnimations())
        {
            // show possible moves of the current player (if any)
            visualization.showPossibleMoves(board, player);
            visualization.setStatusLine(playerName + " thinking...");
        }

        // only for animation reasons
        try
        {
            Thread.sleep(params.getDelay());
        }
        catch (InterruptedException e)
        {
        }

        try
        {
            currentMove = makeMove(reversiPlayer, board);
        }
        catch (TimeExceededException e)
        {
            writeToLog(playerColor + " exceeds time limit");
            cheatedFinish(player, GameState.CHEATED_TIME_EXCEEDED, board);
            statusText = playerColor + " exceeds time limit.";
            throw new TimeExceededException();
        }
        // the InterruptedException is passed to the caller

        writeToLog(playerColor
                + "move="
                + (currentMove == null ? "null" : currentMove.getRow() + ","
                        + currentMove.getCol()));

        if (currentMove == null)
        {
            System.out.println(playerColor + " passes.");
        }

        legalMove = board.checkMove(player, currentMove);

        if (!legalMove)
        {
            System.out.println(playerColor + " makes illegal move: "
                    + currentMove);
            writeToLog(playerColor + " makes illegal move");
            if (verbose)
            {
                System.out.println(board.toString());
            }
            cheatedFinish(player, GameState.CHEATED_ILLEGAL_MOVE, board);
            statusText = playerColor + " makes illegal move.";
            throw new IllegalMoveException("Illegal move by player "
                    + playerColor + "(" + currentMove + ")", currentMove);
        }

        if (currentMove != null)
        {
            computeTokensToFlip(board, currentMove, player, toFlip);
            if (!params.getSilent() && params.getAnimations())
            {
                visualization.animateMove(board, currentMove, player, toFlip);
            }
        }

        board.makeMove(player, currentMove);
        if (!params.getSilent())
        {
            visualization.setInfoLine2(name_red + " (red) vs " + name_green
                    + " (green): " + board.countStones(GameBoard.RED) + ":"
                    + board.countStones(GameBoard.GREEN));
        }

        return currentMove;
    }

    /**
     * Perform a match between two players.
     */
    void performMatch()
    {
        TextGameBoard board    = new TextGameBoard();
        Coordinates move_red   = new Coordinates(0, 0);
        Coordinates move_green = new Coordinates(0, 0);

        if (!params.getSilent())
        {
            visualization.update(board);            
        }

        boolean irregularFinish = false;
        statusText = "";

        while (!board.isFull())
        {
            // --------------------------------------------------------------
            // RED makes move
            // --------------------------------------------------------------

            try
            {
                move_red = performMove(GameBoard.RED, board);
            }
            catch (IllegalMoveException e)
            {
                irregularFinish = true;
                break;
            }
            catch (TimeExceededException e)
            {
                irregularFinish = true;
                break;
            }
            catch (InterruptedException e)
            {
                // this is fatal!
                System.out.println("Player was interrupted!");
                return;
            }

            if (!params.getSilent())
            {
                visualization.update(board);                
            }

            // -------------------------------------------------------------
            // GREEN makes move
            // -------------------------------------------------------------

            try
            {
                move_green = performMove(GameBoard.GREEN, board);
            }
            catch (IllegalMoveException e)
            {
                irregularFinish = true;
                break;
            }
            catch (TimeExceededException e)
            {
                irregularFinish = true;
                break;
            }
            catch (InterruptedException e)
            {
                // this is fatal!
                System.out.println("Player was interrupted!");
                return;
            }

            if (!params.getSilent())
            {
                visualization.update(board);
            }

            // -------------------------------------------------------------
            // game over?
            // -------------------------------------------------------------
            if (move_red == null && move_green == null)
            {
                System.out.println("Vorzeitiges Ende");
                // TODO: Store this in GameState
                // TODO: Test this
                writeToLog("no moves left");
                statusText = "Finished, no moves left.";
                break;
            }
        }

        if (!irregularFinish)
        {
            if (board.isFull())
            {
                statusText = "Finished.";
            }

            writeToLog("finished reds=" + board.countStones(GameBoard.RED)
                    + " greens=" + board.countStones(GameBoard.GREEN));

            int redStones = board.countStones(GameBoard.RED);
            int greenStones = board.countStones(GameBoard.GREEN);

            regularFinish(redStones, greenStones);
        }

        if (!params.getSilent())
        {
            if (state.getResult() == GameState.RESULT_DRAW_GAME)
            {
                visualization.setStatusLine(statusText + " (DRAW "
                        + state.getRedStones() + ":" + state.getGreenStones()
                        + ")");
            }
            else if (state.getResult() == GameState.RESULT_GREEN_WINS)
            {
                visualization.setStatusLine(statusText + " (GREEN "
                        + name_green + " WON " + state.getGreenStones() + ":"
                        + state.getRedStones() + ")");
            }
            else if (state.getResult() == GameState.RESULT_RED_WINS)
            {
                visualization.setStatusLine(statusText + " (RED " + name_red
                        + " WON " + state.getRedStones() + ":"
                        + state.getGreenStones() + ")");
            }
        }

        // -----------------------------------------------------------------
        // Quit program after some time
        // -----------------------------------------------------------------
        if (timeBeforeExit > 0)
        {
            try
            {
                Thread.sleep(timeBeforeExit);
            }
            catch (InterruptedException e)
            {
            }
            //System.exit(0);
        }
        else
        {
            // wait forever
            while (true)
            {
                try
                {
                    Thread.sleep(500);
                }
                catch (InterruptedException e)
                {
                    break;
                }
            }
        }
    }

    /**
     * Make a move under supervised conditions.
     */
    Coordinates makeMove(ReversiPlayer player, GameBoard board)
            throws TimeExceededException, InterruptedException
    {
        PlayerManager manager = new PlayerManager(board, player);
        Thread t = new Thread(manager);

        t.start();
        try
        {
            t.join(timeOut + TIME_TOLERANCE);
        }
        catch (InterruptedException e)
        {
            t.interrupt();
            throw e;
        }
        
        if (!manager.moveDone)
        {
            t.interrupt();
            throw new TimeExceededException();
        }
        return manager.coord;
    }

    void cheatedFinish(int player, int reason, TextGameBoard board)
    {
        if (player == GameBoard.GREEN)
        {
            // BONUS for winner!
            int redStones = Math.max( board.countStones(GameBoard.RED), 32 );
            state.cheatedFinish(GameBoard.GREEN, reason, redStones, 0);
        }
        else
        {
            // BONUS for winner!
            int greenStones = Math.max( board.countStones(GameBoard.GREEN), 32 );           
            state.cheatedFinish(GameBoard.RED, reason, 0, greenStones);
        }

        // state.aborted = false;
        System.out.println("CHEATED : " + state);
    }

    void regularFinish(int redStones, int greenStones)
    {
        // BONUS for early finish!
        if (greenStones == 0)
        {
            redStones = 64;
        }
        else if (redStones == 0)
        {
            greenStones = 64;
        }

        state.regularFinish(redStones, greenStones);
        System.out.println("REGULAR FINISH: " + state);
    }

    static ReversiPlayer instantiatePlayer(String name)
    {
        ReversiPlayer p = null;
        Class c = null;

        try
        {
            c = Class.forName(name);
            Constructor con = c.getConstructor((Class[]) null);
            Object o = con.newInstance((Object[]) null);

            if (!(o instanceof ReversiPlayer))
            {
                System.err.println("The class " + name + " does not implement "
                        + "the interface ReversiPlayer");
                System.exit(1);
            }

            p = (ReversiPlayer) o;
        }
        catch (Exception e)
        {
            System.err
                    .println("********************************************************************");
            System.err.println("Error: could not instantiate \"" + name
					+ "\" !");
            System.err.println("PLEASE MAKE SURE THAT THE PLAYER CLASS HAS A");
            System.err.println("CONSTRUCTOR THAT TAKES NO PARAMETERS");
            System.err
                    .println("********************************************************************");
            e.printStackTrace();
            System.exit(1);
        }
        // this is to catch subtle errors like "randomPlayer.Randomplayer"
        catch (NoClassDefFoundError e)
        {
            System.err
                    .println("********************************************************************");
            System.err.println("Error: could not instantiate \"" + name
					+ "\" !");
            System.err
                    .println("PLEASE MAKE SURE THAT YOU USE THE CORRECT CASE,");
            System.err.println("SINCE CLASSNAMES ARE CASE-SENSITIVE!");
            System.err
                    .println("********************************************************************");
            e.printStackTrace();
            System.exit(1);
        }

        return p;
    }

    /**
     * 
     */
    public void startMatch()
    {
        if (!params.getSilent())
        {
            visualization.setVisible(true);
            // visualization.main_frame.setVisible(true);
        }
        performMatch();
    }

    /**
     * Arena main method.
     * @param args 
     */
    public static void main(String[] args)
    {
        System.out.println("java.class.path="
                + System.getProperties().getProperty("java.class.path"));

        ProgramParameters params;
        params = new ProgramParameters(args);
        Arena arena = new Arena(params, new Visualization2D());
        //Arena arena = new Arena(params, new SwingVisualization2D());

        arena.startMatch();
    }

    void computePossibleMoves(TextGameBoard board, int player,
            int[][] possibleMoves)
    {
        for (int row = 0; row < BOARD_LENGTH; row++)
        {
            for (int col = 0; col < BOARD_LENGTH; col++)
            {
                Coordinates coord = new Coordinates(row + 1, col + 1);
                if (board.checkMove(player, coord))
                {
                    possibleMoves[row][col] = 1;
                    // System.out.println( "Possible move: " + coord );
                }
                else
                {
                    possibleMoves[row][col] = 0;
                }
            }
        }
    }

    ProgramParameters getParams()
    {
        return params;
    }
    
    // -------------------------------------------------------------------------
    // Logging stuff
    // -------------------------------------------------------------------------
    
    String getBacklog()
    {
        return backlog.toString();
    }

    synchronized void addLogStream(PrintWriter pw)
    {
        logStreams.add(pw);
    }

    /**
     * @param t
     */
    public void setTimeBeforeExit(int t)
    {
        timeBeforeExit = t;
    }

    synchronized void writeToLog(String s)
    {
        StringBuffer b = new StringBuffer();
        try
        {
            FileWriter w = new FileWriter(logFile, true);
            b.append("id=" + matchId + " ");
            b.append("time=" + System.currentTimeMillis() + " ");
            b.append(s);
            b.append("\n");
            w.write(b.toString());
            System.out.print("===== " + b.toString());
            w.close();
        }
        catch (IOException e)
        {
            System.err.println("Could not write to log file");
        }

        for (int i = 0; i < logStreams.size(); i++)
        {
            PrintWriter pw = (PrintWriter) logStreams.elementAt(i);
            pw.print(b.toString());
            pw.flush();
        }

        backlog.append(b);
    }   

}
