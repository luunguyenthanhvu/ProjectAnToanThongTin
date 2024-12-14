package Main;

import Controller.SignatureScreen_Controller;
import MyException.GlobalExceptionHandler;
import View.SignatureScreen_View;
import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import java.awt.*;

public class App extends JFrame {
    private SignatureScreen_View SignatureScreenView;
    private JPanel AppContentPane;
    public static App instance;

    public App() {
        if (instance == null){
            instance = this;
        }
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Signature tool");
        this.setVisible(true);

        initialComponents();
        initialLayout();
    }

    private void initialLayout() {
        this.setContentPane(AppContentPane);
        AppContentPane.setLayout(new BorderLayout());
        AppContentPane.add(SignatureScreenView, BorderLayout.CENTER);

        this.pack();
    }

    private void initialComponents() {
        this.SignatureScreenView = new SignatureScreen_View();
        {
            new SignatureScreen_Controller(SignatureScreenView);
        }
        this.AppContentPane = new JPanel();
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();
        new App();
        Thread.setDefaultUncaughtExceptionHandler((thread, ex) -> {
            GlobalExceptionHandler.handleAppException((Exception) ex);
        });
    }

}
