import Components.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import com.intellij.uiDesigner.core.GridConstraints;

public class RoomManager {
    public JPanel ContentPane;

    private JComboBox cbxPresets;
    private JTextField txtPreset;
    private JSlider sldrRed;
    private JSlider sldrGreen;
    private JSlider sldrBlue;
    private JCheckBox chkMoveable;
    private JButton addButton;
    private DrawTable roomDisplay;
    private JFormattedTextField txtClient;
    private JButton loadButton;
    private JFormattedTextField txtSite;
    private JFormattedTextField txtRoom;
    private JFormattedTextField txtDate;
    private JButton applyButton;
    private JButton roboCodeButton;
    private JButton buttonClearRoom;
    private JComboBox cbxSelectPreset;
    private JComboBox cbxSelectionChange;
    private JButton buttonClear;
    private JButton deleteButton;
    private JButton buttonSave;
    private JPanel FileData;
    private JPanel FileOps;
    private JPanel Display;
    private JPanel PresetEditor;
    private JPanel Controls;
    private JButton buttonNew;
    private JButton selectButton;
    private JLabel GOLStart;

    public RoomManager(int x, int y) {
        roomDisplay.ini(x, y);

        iniLstn();

        borderTextFields();

        savePreset();

        presetDropdown();

        roomDisplay.currentSelection = new ArrayList<>();
        GOLStart.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                roomDisplay.setVisible(false);
                roomDisplay.setEnabled(false);
                GOL game = new GOL(roomDisplay.data.length, roomDisplay.data[0].length);
                game.setMinimumSize(new Dimension(500, 500));
                game.setLayout(roomDisplay.getLayout());
                game.setEnabled(true);

                GridConstraints gc = new GridConstraints();

                gc.setFill(3);
                gc.setColSpan(1);
                gc.setRowSpan(1);

                Display.add(game, gc);
                Display.remove(roomDisplay);
            }
        });
    }

    public RoomManager(String dir) {
        roomData rd = dataMgr.readData(dir);

        roomDisplay.data = rd.RoomData;
        roomDisplay.presets = rd.Presets;
        roomDisplay.setupSelection();

        txtClient.setText(rd.Client);
        txtSite.setText(rd.Site);
        txtClient.setText(rd.Client);
        txtDate.setText(rd.Date);

        roomDisplay.repaint();

        iniLstn();

        borderTextFields();

        savePreset();

        presetDropdown();

        roomDisplay.currentSelection = new ArrayList<>();
    }

    //region listeners

    //region init listeners
    //ini listeners
    private void iniLstn() {
        roboCodeButton.addActionListener(e -> {
            JFileChooser jf = new JFileChooser("C:\\");
            jf.setFileFilter(new FileNameExtensionFilter("Comma Separated Values (*.csv)", "csv"));
            jf.showSaveDialog(null);
            File file = jf.getSelectedFile();
            if(file != null) {
                String fileStr = file.getAbsolutePath();
                if(!fileStr.endsWith(".csv"))
                    fileStr = fileStr.concat(".csv");

                dataMgr.writeRobo(new roomData(new String[]{txtClient.getText(), txtSite.getText(), txtRoom.getText(), txtDate.getText()}, roomDisplay.presets, roomDisplay.data), fileStr);
            }
        });

        buttonSave.addActionListener(e -> {
            JFileChooser jf = new JFileChooser("C:\\");
            jf.setFileFilter(new FileNameExtensionFilter("Comma Separated Values (*.csv)", "csv"));
            jf.showSaveDialog(null);
            File file = jf.getSelectedFile();
            if(file != null) {
            String fileStr = file.getAbsolutePath();
            if(!fileStr.endsWith(".csv"))
                fileStr = fileStr.concat(".csv");

                dataMgr.writeData(new roomData(new String[]{txtClient.getText(), txtSite.getText(), txtRoom.getText(), txtDate.getText()}, roomDisplay.presets, roomDisplay.data), fileStr);
            }
        });

        loadButton.addActionListener(e -> {

            FileDialog fd = new FileDialog((Frame) null, "Choose a file", FileDialog.LOAD);
            fd.setDirectory("C:\\");
            fd.setFile("*.csv");
            fd.setVisible(true);
            String filename = fd.getDirectory() + fd.getFile();

            roomData temp = dataMgr.readData(filename);

            txtClient.setText(temp.Client);
            txtSite.setText(temp.Site);
            txtRoom.setText(temp.Room);
            txtDate.setText(temp.Date);

            roomDisplay.presets = temp.Presets;
            roomDisplay.data = temp.RoomData;

            roomDisplay.repaint();
            presetDropdown();
        });
        buttonNew.addActionListener(e -> {
            JSpinner sizeX = new JSpinner();
            sizeX.setModel(new SpinnerNumberModel(16,1,128,1));
            JSpinner sizeY = new JSpinner();
            sizeY.setModel(new SpinnerNumberModel(16,1,128,1));
            Object[] message = {
                    "Size X:", sizeX,
                    "Size Y:", sizeY
            };


            int option = JOptionPane.showConfirmDialog(null, message, "New Room", JOptionPane.OK_CANCEL_OPTION);

            if(option == JOptionPane.OK_OPTION)
            {
                roomDisplay.ini((int)sizeX.getValue(), (int)sizeY.getValue());

                roomDisplay.repaint();
                presetDropdown();
            }
        });

        deleteButton.addActionListener(e -> {

            if(cbxPresets.getSelectedIndex() != 0) {
                Preset[] newPres = new Preset[roomDisplay.presets.length-1];

                int carry = 0;
                for (int i = 0; i < newPres.length; i++) {
                    newPres[i] = roomDisplay.presets[i+carry];

                    if(i == cbxPresets.getSelectedIndex()) {
                        i--;
                        carry++;
                    }
                }
                roomDisplay.presets = newPres;

                presetDropdown();
            } else {
                JOptionPane.showMessageDialog(null,  "Cannot delete 'Empty'","Delete", JOptionPane.WARNING_MESSAGE);
            }
        });

        //look into a better way to do this
        applyButton.addActionListener(e -> {
            int ind = cbxSelectionChange.getSelectedIndex();

            if(ind >-1) {
                //array of cells to be changed
                ArrayList<Point> singleList = new ArrayList<>();

                //go through every item
                for (Point pnt : roomDisplay.currentSelection) {
                    boolean isOn = false;
                    //and compare it to every other item
                    for (Point pnt2 : roomDisplay.currentSelection) {
                        if (pnt.x == pnt2.x && pnt.y == pnt2.y)
                            isOn = !isOn;
                    }

                    //test if it already exists
                    if (isOn) {
                        boolean exists = false;
                        for (Point pnt2 : singleList) {
                            if (pnt == pnt2) {
                                exists = true;
                                break;
                            }
                        }

                        if (!exists) {
                            singleList.add(pnt);
                        }
                    }
                }

                for (Point pnt : singleList) {
                    roomDisplay.data[pnt.x][pnt.y] = ind;
                }
                roomDisplay.repaint();
            }
        });

        buttonClear.addActionListener(e -> {
            roomDisplay.currentSelection.clear();
            roomDisplay.repaint();
        });

        cbxPresets.addActionListener(e -> {
            //update editor info
                    int ind = cbxPresets.getSelectedIndex();
                    if(ind > -1) {
                        txtPreset.setText(roomDisplay.presets[ind].text);
                        int[] clr = roomDisplay.presets[ind].rgb;
                        sldrRed.setValue(clr[0]);
                        sldrGreen.setValue(clr[1]);
                        sldrBlue.setValue(clr[2]);
                        chkMoveable.setSelected(roomDisplay.presets[ind].isMoveable);
                    }
        });

        selectButton.addActionListener(e -> {
            roomDisplay.currentSelection.clear();

            for (int i = 0; i < roomDisplay.data.length; i++) {
                for (int j = 0; j < roomDisplay.data[i].length; j++) {
                    if(roomDisplay.data[i][j] == cbxSelectPreset.getSelectedIndex())
                        roomDisplay.currentSelection.add(new Point(i,j));
                }
            }

            roomDisplay.repaint();
        });

        buttonClearRoom.addActionListener(e -> {

            int yesNo = JOptionPane.showConfirmDialog(null, "Are you sure?", "Clear Data", JOptionPane.YES_NO_OPTION);


            if(yesNo == 0) { //if yes
                for (int i = 0; i < roomDisplay.data.length; i++)
                    for (int j = 0; j < roomDisplay.data[i].length; j++) {
                        roomDisplay.data[i][j] = 0;
                    }

                roomDisplay.repaint();
            }
        });
        sldrRed.addChangeListener(e -> savePreset());
        sldrGreen.addChangeListener(e -> savePreset());
        sldrBlue.addChangeListener(e -> savePreset());
        chkMoveable.addActionListener(e -> savePreset());
        txtPreset.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    int ind = cbxPresets.getSelectedIndex();
                    if (ind != 0) {

                        boolean isOriginal = true;
                        for (int i = 0; i < roomDisplay.presets.length; i++) {
                            if (txtPreset.getText().equals(roomDisplay.presets[i].text)) {
                                isOriginal = false;
                                break;
                            }
                        }

                        if (isOriginal) {
                            roomDisplay.presets[ind].text = txtPreset.getText();
                        } else {
                            JOptionPane.showMessageDialog(null, "Name already exists!", "Change preset name", JOptionPane.WARNING_MESSAGE);
                        }

                        presetDropdown();

                        roomDisplay.repaint();
                    } else {
                        JOptionPane.showMessageDialog(null, "Cannot change empty!", "Change preset name", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });
        addButton.addActionListener(e -> addPreset());
    }
    //endregion

    private void presetDropdown() {

        int[] old = new int[]{  cbxPresets.getSelectedIndex(),
                                cbxSelectPreset.getSelectedIndex(),
                                cbxSelectionChange.getSelectedIndex()};


        cbxPresets.removeAllItems();
        cbxSelectPreset.removeAllItems();
        cbxSelectionChange.removeAllItems();

        for(Preset pre : roomDisplay.presets) {
            cbxPresets.addItem(pre.text);
            cbxSelectPreset.addItem(pre.text);
            cbxSelectionChange.addItem(pre.text);
        }

        for (int i = 0; i < old.length; i++) {
            if (old[i] < 0)
                old[i] = 0;

            if (old[i] > roomDisplay.presets.length-1)
                old[i] = roomDisplay.presets.length-1;
        }


        cbxPresets.setSelectedIndex(old[0]);
        cbxSelectPreset.setSelectedIndex(old[1]);
        cbxSelectionChange.setSelectedIndex(old[2]);

    }

    //endregion

    private void borderTextFields() {
        setBorder(txtClient);
        setBorder(txtSite);
        setBorder(txtRoom);
        setBorder(txtDate);
    }

    private void setBorder(JFormattedTextField txt) {
        txt.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.darkGray));
    }

    public void addPreset() {
        //take sample of initial selection data
        ArrayList<Point> selections = roomDisplay.currentSelection;

        boolean isOriginal = true;
        for (int i = 0; i < roomDisplay.presets.length; i++) {
            if (roomDisplay.presets[i].text.trim().equals(txtPreset.getText())) {
                isOriginal = false;
                break;
            }
        }

        if(isOriginal) {

            Preset[] newPres = new Preset[roomDisplay.presets.length + 1];

            for (int i = 0; i < roomDisplay.presets.length; i++)
                newPres[i] = roomDisplay.presets[i];

            newPres[newPres.length - 1] = new Preset(txtPreset.getText(),
                    sldrRed.getValue(),
                    sldrGreen.getValue(),
                    sldrBlue.getValue(),
                    chkMoveable.isSelected());

            roomDisplay.presets = newPres;

            presetDropdown();
        } else {
            JOptionPane.showMessageDialog(null,  "Preset name already exists!", "Cannot add preset!", JOptionPane.WARNING_MESSAGE);
        }
        roomDisplay.currentSelection = selections;
        roomDisplay.repaint();
    }

    public void savePreset() {
        int ind = cbxPresets.getSelectedIndex();
        if (ind > -1 && txtPreset.getText().equals(roomDisplay.presets[ind].text)) {

            roomDisplay.presets[ind].rgb =
                    new int[]{sldrRed.getValue(),
                            sldrGreen.getValue(),
                            sldrBlue.getValue()};
            roomDisplay.presets[ind].isMoveable = chkMoveable.isSelected();

            presetDropdown();

            roomDisplay.repaint();
        }
    }
}
