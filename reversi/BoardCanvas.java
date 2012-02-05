package reversi;


import java.awt.*;


class BoardCanvas extends Canvas
{
    private static final long serialVersionUID = 1L;
    private BoardMaster master = null;

    public void update(Graphics g)
    {
        paint(g);
    }

    public synchronized void paint(Graphics g)
    {
        Dimension d = this.getSize();
        g.drawImage(master.getImage(), 0, 0, d.width, d.height, Color.white,
                this);
    }

    /**
     * @param bm
     */
    public void setMaster(BoardMaster bm)
    {
        master = bm;
    }
}
