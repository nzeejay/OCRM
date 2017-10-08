package Components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;

public class DrawTable extends JPanel {
    public Preset[] presets;
    //int representing the x y's preset
    public int[][] data;

    private Point mouseDown;
    private Point currentMouse;

    public ArrayList<Point> currentSelection;

    public boolean isMouseDown = false;

    public void ini(int x, int y) {
        //transform x and y dimensions into 2d array
        data = new int[x][];
        for (int i = 0; i < x; i++) {
            data[i] = new int[y];
        }

        currentSelection = new ArrayList<>();

        presets = new Preset[1];
        presets[0] = new Preset("Empty", 255, 255, 255, false);

        setupSelection();
    }

    public void setupSelection() {

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if(isMouseDown) {
                    currentMouse = e.getPoint();
                    repaint();
                }
            }
        });


        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                isMouseDown = true;
                mouseDown = e.getPoint();
            }

            public void mouseReleased(MouseEvent e) {
                isMouseDown = false;
                if ((e.getModifiers() & ActionEvent.CTRL_MASK) != ActionEvent.CTRL_MASK) {
                    if (currentSelection.size() > 0)
                        currentSelection.clear();
                }

                Point mouseUp = e.getPoint();

                int upperX = 0;
                int lowerX = 0;

                int upperY = 0;
                int lowerY = 0;

                if (mouseUp.x > mouseDown.x && mouseUp.y > mouseDown.y) {
                    upperX = mouseUp.x;
                    lowerX = mouseDown.x;

                    upperY = mouseUp.y;
                    lowerY = mouseDown.y;
                } else if (mouseUp.x > mouseDown.x && mouseUp.y < mouseDown.y) {
                    upperX = mouseUp.x;
                    lowerX = mouseDown.x;

                    upperY = mouseDown.y;
                    lowerY = mouseUp.y;
                } else if (mouseUp.x < mouseDown.x && mouseUp.y > mouseDown.y) {
                    upperX = mouseDown.x;
                    lowerX = mouseUp.x;

                    upperY = mouseUp.y;
                    lowerY = mouseDown.y;
                } else {
                    upperX = mouseDown.x;
                    lowerX = mouseUp.x;

                    upperY = mouseDown.y;
                    lowerY = mouseUp.y;
                }

                int w = getWidth() / data.length;
                int h = getHeight() / data[0].length;

                upperX = Math.floorDiv(upperX, w) + 1;
                lowerX = Math.floorDiv(lowerX, w);
                upperY = Math.floorDiv(upperY, h) + 1;
                lowerY = Math.floorDiv(lowerY, h);

                for (int x = lowerX; x < upperX; x++)
                    for (int y = lowerY; y < upperY; y++) {
                        currentSelection.add(new Point(x, y));
                    }

                repaint();
            }
        });
    }

    //draw table to screen
    public void paint(Graphics g) {
        super.paintComponent(g);
        if (data != null && presets != null) {
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());

            int x = getSize().width / data.length;
            int y = getSize().height / data[0].length;
            for (int i = 0; i < data.length; i++)
                for (int j = 0; j < data[i].length; j++) {
                    //region grid and colour
                    //new colour from preset data
                    Color clr = new Color(presets[data[i][j]].rgb[0],
                            presets[data[i][j]].rgb[1],
                            presets[data[i][j]].rgb[2]);

                    //draw black outline rect
                    g.setColor(Color.black);
                    g.fillRect(i * x, j * y, x, y);
                    //colour rect
                    g.setColor(clr);

                    boolean isSelected = false;

                    if (currentSelection.size() != 0)
                        for (int k = 0; k < currentSelection.size(); k++) { //compare with selection
                            if (currentSelection.get(k).x == i && currentSelection.get(k).y == j) {
                                isSelected = !isSelected;
                            }
                        }

                    if (isSelected)
                        g.setColor(new Color((clr.getRed() + 75)/2,(clr.getGreen() + 110)/2,(clr.getBlue() + 175)/2));

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
                    //endregion

                    //region draw text

                    if(!presets[data[i][j]].text.equals("Empty") && x > presets[data[i][j]].text.length()*6 && y > 14 ){
                        g.setColor(Color.black);
                        if((clr.getRed() + clr.getGreen() + clr.getBlue())/3 < 72)
                            g.setColor(Color.white);

                        if(presets[data[i][j]].isMoveable) {
                            g.setFont(new Font(g.getFont().getName(), Font.ITALIC, 12));
                        } else {
                            g.setFont(new Font(g.getFont().getName(), Font.PLAIN, 12));
                        }
                        g.drawString(presets[data[i][j]].text, i * x + 2, (j * y) + 14);
                    }

                    //endregion

                    //draw selection preview
                    if (isMouseDown) {
                        g.setColor(Color.black);
                        g.drawLine(currentMouse.x, currentMouse.y, mouseDown.x, currentMouse.y);
                        g.drawLine(mouseDown.x, mouseDown.y, mouseDown.x, currentMouse.y);
                        g.drawLine(currentMouse.x, mouseDown.y, mouseDown.x, mouseDown.y);
                        g.drawLine(currentMouse.x, currentMouse.y, currentMouse.x, mouseDown.y);
                    }
                }
        }
    }
}
