import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class ProjectManager {

    public String[] lstData;
    public JPanel mainPanel;
    private Random rnd = new Random();
    private JPanel Recent;
    private JLabel lblRecent;
    private JList lstRecent;
    private JPanel Operations;
    private JButton btnNew;
    private JButton btnOpen;

    JFrame frame;

    public ProjectManager() {
        //opening welcoming screen/file manager
        frame = new JFrame("ProjectManager");
        frame.setContentPane(mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);

        lstData = new String[0];
        btnNew.addActionListener(e -> btnNewAction());
        btnOpen.addActionListener(e -> btnOpen());
    }

    //region listeners
    private void btnNewAction() {
        int[] model = new int[32];

        for (int i = 0; i < model.length; i++)
            model[i] = i;


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
            JFrame rmFrame = new JFrame("Room Manager");
            rmFrame.setContentPane(new RoomManager((int)sizeX.getValue(),(int)sizeY.getValue()).ContentPane);
            rmFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            rmFrame.pack();
            rmFrame.setVisible(true);

            rmFrame.setMinimumSize(new Dimension(894, 625));

            frame.setVisible(false);
        }
    }

    private void btnOpen() {
        FileDialog fd = new FileDialog(frame, "Choose a file", FileDialog.LOAD);
        fd.setDirectory("C:\\");
        fd.setFile("*.csv");
        fd.setVisible(true);
        String filename = fd.getDirectory()+fd.getFile();

        if (filename != null) {
            JFrame rmFrame = new JFrame("Room Manager");
            rmFrame.setContentPane(new RoomManager(filename).ContentPane);
            rmFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            rmFrame.pack();
            rmFrame.setVisible(true);

            rmFrame.setMinimumSize(new Dimension(894, 625));


            frame.setVisible(false);
        }
    }
    //endregion
}
