package reversi;


/**
 * ProgramParameters stores the program parameters for the Reversi monitor.
 */

class ProgramParameters
{
    /**
     * 
     */
    public static final int BOARD_LENGTH = 8;
    /**
     * 
     */
    public static final int TIME_TOLERANCE = 100; // time in ms that a player
                                                    // may overrun

    private long timeOut; // timeout for each move
    private long delay; // delay between moves (for smoother animation)
    private String redName; // package.class name for red player
    private String greenName; // dito for green
    private String game_id; // name of match (written to logfile)
    private String logfile; // name of log file (use chart script for readable
                            // output)
    private boolean silent; // if set, no GUI is shown
    private boolean noAnimations; // if set, no animations are shown
    private long timeBeforeExit; // wait before quitting program

    /**
     * 
     */
    public ProgramParameters()
    {
        // defaults
        timeOut = 5000;
        delay = 0;
        logfile = "reversilog";
        silent = false;
        timeBeforeExit = 5000;
        noAnimations = false;
    }

    /**
     * @param args
     */
    public ProgramParameters(String[] args)
    {
        // defaults
        this();

        // scan program arguments
        int index = 0;

        try
        {
            // help (?)
            index = indexOfArg("-h", args);
            if (index >= 0)
            {
                printUsage();
                System.exit(0);
            }
            // timeout
            index = indexOfArg("-t", args);
            if (index >= 0)
            {
                timeOut = Integer.parseInt(args[index + 1]);
                if (timeOut == 0)
                {
                    timeOut = Long.MAX_VALUE / 4;
                }
                System.out.println("TIMEOUT: " + timeOut);
            }
            // delay
            index = indexOfArg("-d", args);
            if (index >= 0)
            {
                delay = Integer.parseInt(args[index + 1]);
                System.out.println("DELAY: " + delay);
            }
            // logfile
            index = indexOfArg("-l", args);
            if (index >= 0)
            {
                logfile = args[index + 1];
                System.out.println("LOGFILE: " + logfile);
            }
            // silent
            index = indexOfArg("-s", args);
            if (index >= 0)
            {
                silent = true;
                System.out.println("SILENT: " + silent);
            }
            // silent
            index = indexOfArg("-n", args);
            if (index >= 0)
            {
                noAnimations = true;
                System.out.println("ANIMATIONS: " + !noAnimations);
            }
            // exittime
            index = indexOfArg("-e", args);
            if (index >= 0)
            {
                timeBeforeExit = Integer.parseInt(args[index + 1]) * 1000;
                System.out.println("DELAYTIMEBEFOREEXIT: " + timeBeforeExit);
            }

            if (args.length < 3)
            {
                printUsage();
                System.exit(1);
                return;
            }

            game_id = args[args.length - 3];
            redName = args[args.length - 2];
            greenName = args[args.length - 1];

            System.out.println("GAMEID: " + game_id + ", RED: " + redName
                    + ", GREEN: " + greenName);
        }
        catch (Exception e)
        {
            printUsage();
            System.exit(1);
        }
    }

    // -----------------------------------------------------------
    // indexOfArg()
    // -----------------------------------------------------------
    // returns the index of the first occurrence of arg in args or -1
    // if not found
    int indexOfArg(String arg, String[] args)
    {
        int i;

        for (i = 0; i < args.length; i++)
        {
            if (args[i].startsWith(arg))
                return i;
        }
        return -1;
    }

    // -----------------------------------------------------------
    // printUsage()
    // -----------------------------------------------------------
    // usage string
    private static final String usage_str = "Usage:\n"
            + "java Arena [-t timeout] [-d delay] [-l logfile] [-s] gameId redName greenName\n"
            + "\tValid args are:\n"
            + "\t-t timeout : timeout for each move in ms. A player is\n"
            + "\t             interrupted and looses the game if it takes\n"
            + "\t             more than the specified amount of time.\n"
            + "\t-d delay   : delay after each move in ms.\n"
            + "\t-l logfile : name of logfile to be used\n"
            + "\t-e exittime: time before exit (in seconds; 0=wait forever)\n"
            + "\t-s         : silent (no GUI displayed)\n";

    /**
     * Prints the usage for Monitor. Start the class with no arguments to print
     * this.
     */
    static void printUsage()
    {
        System.out.println(usage_str);
        System.exit(0);
    } // end printUsage()

    // -----------------------------------------------------------
    // Various accessor methods
    // -----------------------------------------------------------
    long getTimeout()
    {
        return timeOut;
    }

    boolean getAnimations()
    {
        return !noAnimations;
    }

    long getDelay()
    {
        return delay;
    }

    String getGameId()
    {
        return game_id;
    }

    String getRedName()
    {
        return redName;
    }

    String getGreenName()
    {
        return greenName;
    }

    String getLogfile()
    {
        return logfile;
    }

    boolean getSilent()
    {
        return silent;
    }

    long getTimeBeforeExit()
    {
        return timeBeforeExit;
    }

    // -----------------------------------------------------------
    // Various mutator methods, always return the previous value
    // -----------------------------------------------------------
    long setTimeOut(long new_value)
    {
        long prev = timeOut;
        timeOut = new_value;
        return prev;
    }

    long setDelay(long new_value)
    {
        long prev = delay;
        delay = new_value;
        return prev;
    }

    void setRedName(String red)
    {
        this.redName = red;
    }

    void setGreenName(String green)
    {
        this.greenName = green;
    }

    void setGameId(String gameid)
    {
        this.game_id = gameid;
    }

    public String toString()
    {
        return "";
    }

} // end class ProgramParameters
