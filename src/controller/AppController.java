package controller;

import db.Database;
import ui.MainWindow;

public class AppController {
    private Database db;
    private MainWindow window;

    public void startApp() {
        try {
            db = new Database();
            db.connect();
            System.out.println("Povezano z bazo");

            window = new MainWindow();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
