package Components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

public class GOL extends JPanel {

    int[][] data;

    int sizeX;
    int sizeY;

    public GOL(int x, int y) {

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                data[e.getX() / (getSize().width / x) % x][e.getY() / (getSize().height / y) % y] = 1;
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                for (int i = 0; i < data.length; i++)
                    for (int j = 0; j < data[i].length; j++) {
                        data[i][j] = new Random().nextInt(2);
                    }
            }
        });

        sizeX = x;
        sizeY = y;

        data = new int[sizeX][];
        for (int i = 0; i < sizeX; i++) {
            data[i] = new int[sizeY];
            for (int j = 0; j < sizeY; j++) {
                data[i][j] = new Random().nextInt(2);
            }
        }
    }

    public void loop() {
        int[][] newData = new int[sizeX][];
        for (int i = 0; i < sizeX; i++) {
            newData[i] = new int[sizeY];
            for (int j = 0; j < sizeY; j++) {

                //scan area for block
                int total = 0;
                for (int k = -1; k < 2; k++)
                    for (int l = -1; l < 2; l++) {

                        int x = i + k;
                        int y = j + l;

                        if (x < 0)
                            x = sizeX -1;
                        if (y == -1)
                            y = sizeY- 1;

                        x %= sizeX;
                        y %= sizeY;

                        total += data[x][y];
                    }
                    total -= data[i][j];

                boolean c = false;
                if(data[i][j] == 1)
                    c = true;

                newData[i][j] = c && (total == 2 || total == 3) || !c && total == 3 ? 1:0;

            }
        }

        data = newData;

        repaint();

        //try { Thread.sleep((long) 128); }catch(Throwable T){}

    }

    public void paint(Graphics g) {
        super.paintComponent(g);

        int sizeX = this.getWidth();
        int sizeY = this.getHeight();

        //draw black background
        g.setColor(Color.black);
        g.fillRect(0, 0, sizeX, sizeY);

        int x = getSize().width / data.length;
        int y = getSize().height / data[0].length;
        for (int i = 0; i < data.length; i++)
            for (int j = 0; j < data[i].length; j++) {

                Color clr = new Color[]{Color.white, Color.black}[data[i][j]];
                g.setColor(clr);

                //outside edge sense
                if (i + 1 == data.length && j + 1 == data[i].length) {
                    g.fillRect(i * x + 1, j * y + 1, x - 2, y - 2);
                } else if (i + 1 == data.length) {
                    g.fillRect(i * x + 1, j * y + 1, x - 2, y - 1);
                } else if (j + 1 == data[i].length) {
                    g.fillRect(i * x + 1, j * y + 1, x - 1, y - 2);
                } else {
                    g.fillRect(i * x + 1, j * y + 1, x - 1, y - 1);
                }
            }

        loop();
    }
}
