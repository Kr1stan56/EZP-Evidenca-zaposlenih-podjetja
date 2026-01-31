import controller.AppController;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {

        // Swing mora teči na Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            new AppController().startApp();
        });

    }
}
