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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import reversi.Arena;
import reversi.Coordinates;
import reversi.GameBoard;

/**
 * L&ouml;sungsvorschlag zur &Uuml;bungsserie Nr. 7, Aufgabe 2)<br/>
 * 
 * In diesem Lösungsvorschlag implementiert HumanPlayer nicht das Interface 
 * ReversiPlayer, sondern erweitert die abstrakte Klasse AbstractPlayer. Anstatt 
 * einen Zug zu berechnen, fordert <code>HumanPlayer</code> den Benutzer auf, 
 * einen Zug über die Konsole einzugeben.
 *
 * @see examplePlayers.AbstractPlayer
 * @author Team x (Apfel Sine, Zi Trone)
 * @version 1
 */
public class HumanPlayer extends AbstractPlayer
{
    /**
     * Konstante, die vom Benutzer eigegeben werden kann, um zu passen.
     */
    private final static String PASSEN = "p";

    /**
     * Konstruktor, der bei der Gründung eines HumanPlayer eine Meldung auf den
     * Bildschirm ausgibt.
     */
    public HumanPlayer()
    {
        System.out.println("HumanPlayer erstellt.");
    }

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
            System.out.println("HumanPlayer ist Spieler RED.");
        }
        else if (color == GameBoard.GREEN)
        {
            System.out.println("HumanPlayer ist Spieler GREEN.");
        }
        this.timeout = timeout;
    }

    /**
     * Macht einen Zug für den HumanPlayer, indem der Benutzer zur Eingabe eines
     * Zuges aufgefordert wird. Diese Methode wird von {@link reversi.Arena}
     * abwechselnd aufgerufen.
     * 
     * @see reversi.ReversiPlayer
     * 
     * @param gb Die aktuelle Spielsituation.
     * @return Der Zug des HumanPlayers.
     */
    public Coordinates nextMove(GameBoard gb)
    {
        boolean validMove = false;
        Coordinates coord = null;
        
        // Die überprüfung, ob überhaupt ein Zug möglich ist, wird in 
        // checkMove() gemacht. 

        System.out.print("HumanPlayer ");
        if (color == GameBoard.RED)
        {
            System.out.print("(RED)");
        }
        else if (color == GameBoard.GREEN)
        {
            System.out.print("(GREEN)");
        }
        System.out.print(", gib deinen Zug ein (passen mit '" + PASSEN + "'): ");
        
        do
        {
            coord = readMoveFromKeyboard();         
            
            if ( !checkMove(coord, gb) )
            {
                System.out.println("Ungültiger Zug, bitte anderen Zug auswählen:");
            }
            else
            {
                validMove = true;
            }
        } while ( !validMove );

        return coord;
    } // end nextMove()

    /**
     * Liest einen Zug vom Benutzer ein. Gültige Eingaben sind entweder ein
     * Koordinatenpaar bestehend aus Zeile und Spalte (z.B. '6d') oder ein 'p',
     * um zu passen falls kein Zug möglich ist. Methode wiederholt die
     * Eingabeaufforderung so lange bis eine gültige Eingabe gemacht wurde.
     * 
     * @return Gibt die eingelesenen Koordinaten zurück, bzw. <code>null</code>,
     *         wenn der Benutzer "Passen" ausgewählt hat.
     */
    public static Coordinates readMoveFromKeyboard()
    {
        Coordinates result = null;
        while (result == null)
        {
            System.out.print(">");
            String str = null;
            int row = 0, column = 0;

            BufferedReader d = new BufferedReader(new InputStreamReader(
                    System.in));

            // String einlesen
            try
            {
                str = d.readLine();
            } catch (IOException e)
            {
                e.printStackTrace();
            }

            // gelesenen String sezieren
            str.trim();
            str = str.toLowerCase();

            // falls der Zug "passen" bedeutet, beende Schleife (gib 'null'
            // zurück)
            if (str.equals(PASSEN))
            {
                System.out.println("Zug 'PASSEN' wurde ausgewaehlt.");
                break;
            }

            if (str.length() != 2)
            {
                System.out.println("Ungueltige Eingabe: mehr als 2 Zeichen.");
                continue;
            }

            // ist das erste Zeichen eine Ziffer zwischen 0..8?
            row = (int) (str.charAt(0)) - (int) '0';
            // Zeilen 0 und 9 sind ungueltig
            if (row < 1 || row > 8)
            {
                System.out.println("Ungueltige Eingabe: die Zeilennummer muss "
                        + "zwischen 1 und 8 liegen.");
                continue;
            }

            // ist das zweite Zeichen ein Buchstabe zwischen a..h?
            column = (int) (str.charAt(1)) - (int) 'a' + 1;

            if (column < 1 || column > 8)
            {
                System.out.println("Ungueltige Eingabe: die Spaltenummer muss "
                        + "zwischen A und H liegen.");
                continue;
            }

            result = new Coordinates(row, column);
        }
        return result;
    } // end readMoveFromKeyboard()
}
