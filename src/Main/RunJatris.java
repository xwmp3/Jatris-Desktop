package Main;

import View.UI;

import javax.swing.*;

public class RunJatris {
    public static void main(String[] args) {
        UI ui = new UI();
        JFrame frame = new JFrame();
        frame.add(ui);
        frame.setSize(650, 670);
        frame.setTitle("Jatris");
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        ui.run();
    }
}
