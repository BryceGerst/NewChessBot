package gui;

import java.awt.EventQueue;
import javax.swing.JFrame;

public class Application extends JFrame {
    
	private static final long serialVersionUID = 1L;

	public Application() {

        initUI();
    }

    private void initUI() {

        add(new Board());

        pack();

        setTitle("Chess");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {

        EventQueue.invokeLater(() -> {
            Application ex = new Application();
            ex.setVisible(true);
        });
        
    }
}
