import javax.swing.*;

public class RunTetris {
    public static void main(String[] args) {
        UI ui = new UI();
        JFrame frame = new JFrame();
        frame.add(ui);
        frame.setSize(650, 670);
        frame.setTitle("Tetris");
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        ui.run();
    }
}
