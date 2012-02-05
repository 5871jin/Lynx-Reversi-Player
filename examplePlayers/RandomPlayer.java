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

import java.util.Vector;

import reversi.Arena;
import reversi.Coordinates;
import reversi.GameBoard;

/**
 * L&ouml;sungsvorschlag zur &Uuml;bungsserie Nr. 7, Aufgabe 2)<br/>
 * 
 * Diese Klasse implementiert einen "Zufallsspieler", welcher zufällig gültige
 * Spielzüge auswählt.
 *  
 * @author Team x (Apfel Sine, Zi Trone)
 * @version 1
 */
public class RandomPlayer extends AbstractPlayer
{
    /**
     * Speichert die Farbe und den Timeout-Wert in Instanzvariablen ab. Diese
     * Methode wird vor Beginn des Spiels von {@link Arena} aufgerufen.
     * 
     * @param color Farbe dieses Spielers.
     * @param timeout Zeitlimit für einen Spielzug.
     * 
     * @see reversi.ReversiPlayer
     */
    public void initialize(int color, long timeout)
    {
        this.color = color;
        if (color == GameBoard.RED)
        {
            System.out.println("RandomPlayer ist Spieler RED.");
        }
        else if (color == GameBoard.GREEN)
        {
            System.out.println("RandomPlayer ist Spieler GREEN.");
        }
        this.timeout = timeout;
    }
    
    /**
     * Bestimmt einen zufälligen (aber gültigen) Zug für die aktuelle 
     * Spielsituation.
     * 
     * @see reversi.ReversiPlayer
     * 
     * @param gb Die aktuelle Spielsituation. 
     * @return Der Zug des HumanPlayers.
     */ 
    public Coordinates nextMove(GameBoard gb)
    {
        Vector validMoves = new Vector(gb.getSize() * gb.getSize());

        for (int row = 1; row <= gb.getSize(); row++)
        {
            for (int col = 1; col <= gb.getSize(); col++)
            {
                Coordinates coord = new Coordinates(row, col);
                if (checkMove(coord, gb))
                {
                    validMoves.add(coord);
                }
            }
        }

        if ( validMoves.size() > 0 )
        {
            int idx = (int) (Math.random() * validMoves.size());
            return (Coordinates) validMoves.elementAt(idx);
        }
        else
        {
            return null;
        }
    }
}
