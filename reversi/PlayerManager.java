package reversi;


class PlayerManager implements Runnable
{
    private GameBoard gameBoard;
    private ReversiPlayer reversiPlayer;
    boolean moveDone;
    Coordinates coord;

    PlayerManager(GameBoard g, ReversiPlayer r)
    {
        gameBoard = g;
        reversiPlayer = r;
        coord = null;
    }

    public void run()
    {
        // this variable tells the move() method in ReversiGame if the
        // computation of nextMove() was finished (moveDone == true),
        // or prematurely interrupted (moveDone == false).
        moveDone = false;
        // this thread is interrupted when the calculation exceeds the
        // timeout

        try
        {
            coord = reversiPlayer.nextMove(gameBoard);
        }
        catch (java.lang.StackOverflowError e)
        {
            System.err.println(e.getMessage());
            e.printStackTrace();
            return; 
        }
        catch (OutOfMemoryError e)
        {
            System.err.println(e.getMessage());
            e.printStackTrace();
            return; 
        }
        catch (ThreadDeath e)
        {
            System.err
                    .println("[Thread was stopped due to time limit expiration]");
            return; 
        }
        catch (Throwable e)
        {
            // catch all other exceptions and errors
            System.err.println(e.getMessage());
            e.printStackTrace();
            return; 
        }

        // when the thread is interrupted, control never gets here;
        // moveDone then is false
        moveDone = true;
    }
} 
