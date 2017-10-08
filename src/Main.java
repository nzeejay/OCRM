import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        ProjectManager pm = new ProjectManager();

        //set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Throwable e) {

        }
    }
}
