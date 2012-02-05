package reversi;


import java.awt.*;
import java.awt.event.*;


class Visualization2D extends Visualization implements BoardMaster
{
    /**
     * stupid idiot
     */
    public static final int BOARD_LENGTH = ProgramParameters.BOARD_LENGTH;
    /**
     * fucking developer
     */
    public static final int TIME_TOLERANCE = ProgramParameters.TIME_TOLERANCE;
    
    protected static Color boardGray  = Color.gray;
    protected static Color boardWhite = Color.white;
    protected static Color boardFont  = Color.black;
    protected static Color playGreen  = Color.green;
    protected static Color playRed    = Color.red;

    protected static final Color RED_TRANSPARENT = new Color(255, 0, 0, 100);
    protected static final Color GREEN_TRANSPARENT = new Color(0, 255, 0, 100);
    
    final static Color animBoxColor = Color.blue;   
    
    Arena arena;
    ProgramParameters params;

    private boolean showPossibleMoves = false;
    /*
     * Here we can turn on and off the animations.
     */
    private boolean showAnimations    = false;
    private boolean highlightTurn     = false;

    protected Image boardImage = null; // used for offscreen drawing
    
    protected Image progressImage = null;

    protected Frame main_frame;
    protected BoardCanvas boardArea;
    protected Label infoLine;
    protected Label infoLine2;
    protected Label statusLine;
    protected Button quitButton;
    protected Button make_slower;
    protected Button make_quicker;


    // how large should a stone be painted?
    int tileWidth = 80;
    int tileHeight = 80;
    int w_offset = tileWidth / 20;
    int h_offset = tileHeight / 20;

    
    void init(Arena a)
    {
        arena = a;
        params = arena.getParams();     
        
        Font myFont = new Font("Arial", Font.BOLD, 16);

        boardArea    = new BoardCanvas();
        boardArea.setMaster(this);
        infoLine     = new Label("[info line]");
        infoLine2    = new Label("[info line2]");
        statusLine   = new Label("[status line]");
        make_slower  = new Button("increase delay");
        make_quicker = new Button("decrease delay");
        quitButton   = new Button("Quit");

        
        infoLine.setFont(myFont);
        infoLine2.setFont(myFont);
        statusLine.setFont(myFont);
        make_slower.setFont(myFont);
        make_quicker.setFont(myFont);
        quitButton.setFont(myFont);

        Panel topPanel = new Panel();
        topPanel.setLayout(new BorderLayout());
        Panel topPanel2 = new Panel();
        topPanel2.setLayout(new FlowLayout());

        topPanel.add(infoLine, BorderLayout.CENTER);
        topPanel.add(infoLine2, BorderLayout.SOUTH);
        topPanel.add(topPanel2, BorderLayout.EAST);
        topPanel2.add(make_slower);
        topPanel2.add(make_quicker);
        topPanel2.add(quitButton);

        MenuBar menu_bar = new MenuBar();
        Menu opt_menu = new Menu("Options");
        MenuItem noExitTimeItem = new MenuItem("Don't exit after match");
        MenuItem someExitTimeItem = new MenuItem("10 sec delay before exit");
        MenuItem moreExitTimeItem = new MenuItem("1 min delay before exit");
        MenuItem exit_item = new MenuItem("Exit");

        CheckboxMenuItem item_showAnimations 
            = new CheckboxMenuItem("Show animations", showAnimations);
        CheckboxMenuItem item_showPossibleMoves 
            = new CheckboxMenuItem("Show possible moves", showPossibleMoves);
        CheckboxMenuItem item_highlightTurn
            = new CheckboxMenuItem("Highlight turn", highlightTurn);
        
        opt_menu.add(item_showPossibleMoves);
        opt_menu.add(item_showAnimations);
        opt_menu.add(item_highlightTurn);
        
        opt_menu.add(noExitTimeItem);
        opt_menu.add(someExitTimeItem);
        opt_menu.add(moreExitTimeItem);
        opt_menu.add(exit_item);
        menu_bar.add(opt_menu);

        main_frame = new Frame("Reversi Arena");
        main_frame.setMenuBar(menu_bar);
        main_frame.setLayout(new BorderLayout());
        main_frame.add(topPanel, BorderLayout.NORTH);
        main_frame.add(statusLine, BorderLayout.SOUTH);
        main_frame.add(boardArea, BorderLayout.CENTER);
        main_frame.pack();

        main_frame.setSize(660, 660);
        boardImage    = boardArea.createImage(640, 640);
        
        // add listeners...

        item_showPossibleMoves.addItemListener(new ItemListener()
        {
            public void itemStateChanged(ItemEvent i)
            {
                showPossibleMoves = i.getStateChange() == ItemEvent.SELECTED;
            }
        });

        item_showAnimations.addItemListener(new ItemListener()
        {
            public void itemStateChanged(ItemEvent i)
            {
                showAnimations = i.getStateChange() == ItemEvent.SELECTED;
            }
        });
        
        item_highlightTurn.addItemListener(new ItemListener()
        {
            public void itemStateChanged(ItemEvent i)
            {
                highlightTurn = i.getStateChange() == ItemEvent.SELECTED;
            }
        });     

        exit_item.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent s)
            {
                arena.writeToLog("quit button pressed");
                System.exit(2);
                main_frame.dispose();
                if (arena.performerThread != null)
                    arena.performerThread.interrupt();
            }
        });

        noExitTimeItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                arena.setTimeBeforeExit(0);
            }
        });

        someExitTimeItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                arena.setTimeBeforeExit(10000);
            }
        });

        moreExitTimeItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                arena.setTimeBeforeExit(60000);
            }
        });     
        
        make_slower.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                params.setDelay(params.getDelay() + 50);
                // delay += 50;
                infoLine.setText("delay=" + params.getDelay());
            }
        });

        make_quicker.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                if (params.getDelay() >= 50)
                {
                    params.setDelay(params.getDelay() - 50);
                }
                else
                {
                    params.setDelay(0);
                }
                infoLine.setText("delay=" + params.getDelay());
            }
        });

        quitButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                arena.writeToLog("quit button pressed");
                System.exit(2);
            }
        });
    }

    /**
     * Draw the layout of a game board without any stones on it. This is
     * essentially only the white and gray fields on the board.
     */
    void drawTheBoardBase()
    {
        String strpos;
        final int fsize = 20;

        Graphics g = boardImage.getGraphics();

        // store current font and set new font
        Font default_font = g.getFont();
        g.setFont(new Font(null, Font.PLAIN, fsize));
        FontMetrics fm = g.getFontMetrics();

        for (int row = 0; row < BOARD_LENGTH; row++)
        {
            for (int col = 0; col < BOARD_LENGTH; col++)
            {
                if ((row + col) % 2 == 0)
                {
                    g.setColor(boardGray);
                }
                else
                {
                    g.setColor(boardWhite);
                }
                g.fillRect(col * tileWidth, row * tileHeight, tileWidth,
                        tileHeight);

                // draw coordinates only if boardFont not null:
                if (boardFont != null)
                {
                    strpos = String.valueOf(row + 1)
                            + String.valueOf((char) (col + 'A'));

                    int xxx = (col * tileWidth);
                    int yyy = ((row + 1) * tileHeight);

                    xxx = xxx - fm.stringWidth(strpos) / 2;
                    yyy = yyy + fsize / 2;

                    g.setColor(boardFont);
                    g.drawString(strpos, xxx + tileWidth / 2, yyy - tileHeight
                            / 2);
                }
            }
        }
        g.setFont(default_font);
    }

    /**
     * Draw a game board with all the stones on it. <code>repaint()</code> is
     * actually not called, since the context of this method is not known in
     * advance.
     */
    void drawTheBoard(GameBoard board)
    {
        int row, col;
        int player;

        if (boardImage == null)
        {
            System.out.print("[sorry - no boardImage]");
            return; // come back later
        }

        //System.out.print("[drawing]");
        drawTheBoardBase();
        Graphics g = boardImage.getGraphics();

        for (row = 1; row <= BOARD_LENGTH; row++)
        {
            for (col = 1; col <= BOARD_LENGTH; col++)
            {
                try
                {
                    player = board.getPosition(new Coordinates(row, col));
                }
                catch (OutOfBoundsException e)
                {
                    player = GameBoard.EMPTY;
                }
                switch (player)
                {
                    case GameBoard.GREEN:
                        g.setColor(playGreen);
                        break;
                    case GameBoard.RED:
                        g.setColor(playRed);
                        break;
                    default:
                        continue;
                }
                g.fillOval((col - 1) * tileWidth + w_offset, (row - 1)
                        * tileHeight + h_offset, tileWidth - 2 * w_offset,
                        tileHeight - 2 * h_offset);
            }
        }
    }

    /**
     * Helper function: draw filled circle with (x,y,xrad,yrad)
     */
    protected void drawFilledOval(Graphics g, int x, int y, int xrad, int yrad)
    {
        g.fillOval(x - xrad, y - yrad, xrad + xrad, yrad + yrad);
    }

    /**
     * Animate a move by turning opponent's stones.
     */
    void animateMove(GameBoard oldBoard, Coordinates move, int player,
            int[][] toFlip)
    {
        int newRow = move.getRow() - 1;
        int newCol = move.getCol() - 1;

        Color mycolor = (player == GameBoard.RED ? playRed : playGreen);
        Color opposite = (player == GameBoard.RED ? playGreen : playRed);

        Graphics g = boardImage.getGraphics();

        int step = 0;

        int B = tileWidth - 2 * w_offset; // Durchmesser eines Steines
        int Bh = tileHeight - 2 * h_offset; // Durchmesser eines Steines in
        // y-Richtung

        if ( highlightTurn )
        {
            animateNewPosition(player, move);
        }

        // clear the possible moves
        drawTheBoard(oldBoard);
        boardArea.repaint();

        while ( showAnimations && step < 100)
        {

            // ---------------------------------------------
            // Draw the new stone
            // ---------------------------------------------
            if ((newRow + newCol) % 2 == 0)
            {
                g.setColor(boardGray);
            }
            else
            {
                g.setColor(boardWhite);
            }
            g.fillRect(newCol * tileWidth, newRow * tileHeight, tileWidth,
                    tileHeight);
            g.setColor(mycolor);
            int X = (B * step) / 200;
            int Y = (Bh * step) / 200;
            int x = newCol * tileWidth + tileWidth / 2;
            int y = newRow * tileHeight + tileHeight / 2;
            drawFilledOval(g, x, y, X, Y);

            // ---------------------------------------------
            // turn the stones
            // ---------------------------------------------
            for (int row = 0; row < BOARD_LENGTH; row++)
            {
                for (int col = 0; col < BOARD_LENGTH; col++)
                {
                    if (toFlip[row][col] == 1)
                    {
                        // clear position
                        if ((row + col) % 2 == 0)
                        {
                            g.setColor(boardGray);
                        }
                        else
                        {
                            g.setColor(boardWhite);
                        }
                        g.fillRect(col * tileWidth, row * tileHeight,
                                tileWidth, tileHeight);

                        int q = 0;
                        if (step < 50)
                        {
                            // phase 1: shrink
                            g.setColor(opposite);
                            q = step * B / 100;
                        }
                        else
                        {
                            // phase 2: expand
                            g.setColor(mycolor);
                            q = (100 - step) * B / 100;
                        }
                        g.fillOval(col * tileWidth + w_offset + q, row
                                * tileHeight + h_offset, tileWidth - 2
                                * w_offset - 2 * q, tileHeight - 2 * h_offset);

                    }
                }
            }

            // ---------------------------------------------
            // repaint board
            // ---------------------------------------------
            if (!params.getSilent())
            {
                boardArea.repaint();
            }

            // ---------------------------------------------
            // tune for smoothness of animation...
            // ---------------------------------------------
            step += 3;
            try
            {
                Thread.sleep(15);
            }
            catch (InterruptedException e)
            {
            }
        } // end while
    }

    private void animateNewPosition(int player, Coordinates move)
    {
        int newRow = move.getRow() - 1;
        int newCol = move.getCol() - 1;

        Color mycolor = (player == GameBoard.RED ? playRed : playGreen);
        Graphics g = boardImage.getGraphics();

        // draw lines

        // let the new stone blink three times
        for (int idx = 0; idx < 3; idx++)
        {
            g.setColor(mycolor);
            g.fillOval(newCol * tileWidth + w_offset, newRow * tileHeight
                    + h_offset, tileWidth - 2 * w_offset, tileHeight - 2
                    * h_offset);
            if (!params.getSilent())
            {
                boardArea.repaint();
            }
            try
            {
                Thread.sleep(300);
            }
            catch (InterruptedException e)
            {
            }

            // clear position
            if ((newRow + newCol) % 2 == 0)
            {
                g.setColor(boardGray);
            }
            else
            {
                g.setColor(boardWhite);
            }
            g.fillRect(newCol * tileWidth, newRow * tileHeight, tileWidth,
                    tileHeight);
            if (!params.getSilent())
            {
                boardArea.repaint();
            }
            try
            {
                Thread.sleep(300);
            }
            catch (InterruptedException e)
            {
            }
        }
    }

    void drawPossibleMoves(int player, int[][] possibleMoves)
    {
        Graphics g = boardImage.getGraphics();

        Color playerColor = (player == GameBoard.RED) ? RED_TRANSPARENT
                : GREEN_TRANSPARENT;
        g.setColor(playerColor);

        for (int row = 0; row < BOARD_LENGTH; row++)
        {
            for (int col = 0; col < BOARD_LENGTH; col++)
            {
                if (possibleMoves[row][col] == 1)
                {

                    g.fillOval(col * tileWidth + w_offset, row * tileHeight
                            + h_offset, tileWidth - 2 * w_offset, tileHeight
                            - 2 * h_offset);
                }
            }
        }
    }

    public void showPossibleMoves(TextGameBoard board, int player)
    {
        if (showPossibleMoves)
        {
            int[][] possibleMoves = new int[BOARD_LENGTH][BOARD_LENGTH];

            arena.computePossibleMoves(board, player, possibleMoves);
            drawTheBoard(board);
            drawPossibleMoves(player, possibleMoves);
            boardArea.repaint();
        }
    }

    public void setInfoLine(String text)
    {
        infoLine.setText(text);
    }

    public void setInfoLine2(String text)
    {
        infoLine2.setText(text);
    }

    public void setStatusLine(String text)
    {
        statusLine.setText(text);
    }

    /**
     * fuck off
     */
    public void repaint()
    {
        boardArea.repaint();
    }

    public Image getImage()
    {
        return boardImage;
    }

    public void setVisible(boolean visible)
    {
        main_frame.setVisible(visible);
    }

    public void dispose()
    {
        main_frame.dispose();
    }

    public void update(TextGameBoard board)
    {
        drawTheBoard(board);
        repaint();
    }
}
