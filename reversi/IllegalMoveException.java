package reversi;

class IllegalMoveException extends Exception
{
    private static final long serialVersionUID = 1L;
    /**
     * 
     */
    public Coordinates coord;
    
    /**
     * @param msg
     */
    public IllegalMoveException( String msg )
    {
        super( msg );
    }
    
    /**
     * @param msg
     * @param c
     */
    public IllegalMoveException( String msg, Coordinates c )
    {
        super( msg );
        coord = c;      
    }   
}
