/*
 * DISCLAIMER:
 * This is my personal implementation or an implementation based on a solution
 * of a student of my group and is by no means the only correct solution. In 
 * fact, it is not necessarily correct. Do not use this code to study for an 
 * examination, and do not refer to it during an examination. I can not be held 
 * personally resposible for any damage inflicted by this program, both 
 * technical and personal.
 * 
 */

package examplePlayers;

import reversi.*;

/**
 * L&ouml;sungsvorschlag zur &Uuml;bungsserie Nr. 7, Aufgabe 2)<br/>
 * 
 * AbstractPlayer stellt allen Playern gemeinsam zu benutzende Methoden 
 * zur Verfügung. Dazu implementiert die Klasse das Interface ReversiPlayer,
 * ohne jedoch die Methoden zu implementieren - diese werden stattdessen als 
 * abstract deklariert.
 *  
 * @see reversi.ReversiPlayer
 * @author Team x (Apfel Sine, Zi Trone)
 * @version 1
 */
abstract public class AbstractPlayer implements ReversiPlayer
{
    /**
     * Die Farbe des Spielers.
     */
    protected int color = 0;

    /**
     * Das Timeout des Spiels (nicht relevant im Blatt 7).
     */
    protected long timeout = 0;

    /**
     * Speichert die Farbe und den Timeout-Wert in Instanzvariablen ab. Diese
     * Methode wird vor Beginn des Spiels von {@link Arena} aufgerufen.
     * Muss von den Unterklassen imlementiert werden.
     * 
     * @param color Farbe dieses Spielers.
     * @param timeout Zeitlimit für einen Spielzug.
     */
    abstract public void initialize(int color, long timeout);
    
    /**
     * Soll den nächsten Zug zurückliefern. 
     * Muss von Unterklassen implementiert werden.
     * 
     * @param gb Aktuelle Spielsituation
     * @return Koordinaten des nächsten Spielzuges. 
     */
    abstract public Coordinates nextMove(GameBoard gb); 

    /**
     * Testet, ob dieser Player in der Spielsituation gb an der Position zug 
     * einen Stein setzen darf. Auch ein Passen (zug ist null) wird überprüft. 
     * 
     * @param gb Aktuelle Spielsituation
     * @param zug Zu überprüfender Zug
     * @return true, wenn der Zug erlaubt ist, sonst false.
     */
    public boolean checkMove(Coordinates zug, GameBoard gb)
    {   
        try
        {
            // passen ist nur erlaubt, wenn man nicht ziehen kann
            if (zug == null)
            {
                return !moveExists(gb);
            }
        
            if (gb.getPosition(zug) != GameBoard.EMPTY)
            {
                // wenn das zu besetzende Feld nicht leer ist, ist der Zug
                // auf jeden Fall nicht erlaubt
                return false;
            }
            else
            {
                // ansonsten müssen wir alle 8 "Richtungen" prüfen
                return checkTrace(zug, new Coordinates(1, 1), gb)
                        || checkTrace(zug, new Coordinates(1, 0), gb)
                        || checkTrace(zug, new Coordinates(1, -1), gb)
                        || checkTrace(zug, new Coordinates(0, -1), gb)
                        || checkTrace(zug, new Coordinates(-1, -1), gb)
                        || checkTrace(zug, new Coordinates(-1, 0), gb)
                        || checkTrace(zug, new Coordinates(-1, 1), gb)
                        || checkTrace(zug, new Coordinates(0, 1), gb);
            }
        } 
        catch (OutOfBoundsException e)
        {
            return false;
        }
    }

    /**
     * Testet, ob in der Spielsituation gb durch Setzen eines Steins an der 
     * Position pos in der Richtung dir Steine des Gegners umgedreht umgedreht 
     * werden können.
     * 
     * @param pos Position des neuen Steins
     * @param dir Richtung, in die getestet wird
     * @param gb Aktuelle Spielsituation
     *  
     * @return true, falls gegnerische Steine geschlagen werden können, 
     *         sonst false
     */
    private boolean checkTrace(Coordinates pos, Coordinates dir,
                               GameBoard gb)
    {
        boolean otherStonesBetween = false;
        try
        {
            while (true)
            {
                pos = new Coordinates(pos.getRow() + dir.getRow(), 
                        pos.getCol() + dir.getCol());
                
                if (pos.getRow() < 1 || pos.getRow() > gb.getSize() ||
                    pos.getCol() < 1 || pos.getCol() > gb.getSize() ||
                    gb.getPosition(pos) == GameBoard.EMPTY )
                {
                    return false;
                }
                else if (gb.getPosition(pos) == color)
                {
                    return otherStonesBetween;
                }
                else
                {
                    otherStonesBetween = true;
                }
            }
        } 
        catch (OutOfBoundsException e)
        {
            return false;
        }
    }

    /**
     * Testet, ob es für diesen Player auf dem Feld gb einen legalen Zug gibt.
     * 
     * @param gb Aktuelle Spielsituation.
     * @return true, wenn es einen legalen Zug für diesen Spieler gibt, 
     *         sonst false.
     */
    public boolean moveExists(GameBoard gb)
    {
        // Einfach Ansatz: Alle Felder des Spielbrettes der Reihe nach 
        // durchgehen und dabei überprüfen, ob der aktueller Spieler einen
        // gültigen Zug machen könnte.
        for (int row = 1; row <= gb.getSize(); row++)
        {
            for (int col = 1; col <= gb.getSize(); col++)
            {
                if (checkMove(new Coordinates(row, col), gb))
                {
                    return true;
                }
            }
        }
        return false;
    }
}
