package soundboard;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

/**
 * @author mohammed
 * @author David
 */
public class MainMenu extends JPanel implements ActionListener {

    private static final int DEFAULT_WIDTH = 800;                   //default application window width
    private static final int DEFAULT_HEIGHT = 600;                  //default application window height

    private final JFrame frame;                                     //application window
    private final JButton playButton;                               //play game
    private final JButton InformationButton;                        //display Information about the game

    //display Image for soundboard log
    @Override
    public void paintComponent(Graphics g) {
        Image img;
        try {
            img = ImageIO.read(Class.class.getResourceAsStream("/res/BeatPad.jpg"));
            g.drawImage(img, 200, 50, this);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public MainMenu(JFrame frame) {

        //set top-level panel layout behaviour and dimensions
        super(new BorderLayout());
        setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        this.frame = frame;

        //create Meun control panel for play and Information
        TitledBorder MeundBorder = BorderFactory.createTitledBorder("Meun");
        JPanel MeunPanel = new JPanel(new GridLayout(2, 2));
        MeunPanel.setBorder(MeundBorder);

        playButton = new JButton("Play");
        playButton.addActionListener(this);
        MeunPanel.add(playButton);

        InformationButton = new JButton("Information");
        InformationButton.addActionListener(this);
        MeunPanel.add(InformationButton);

        add(MeunPanel, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //add play Button event
        if (e.getSource() == playButton) {
            SoundBoard soundBoard = new SoundBoard(frame);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(soundBoard);
            frame.pack();
            setVisible(true);
            //add Information Button event
        } else if (e.getSource() == InformationButton) {
            InfoWindow i = new InfoWindow();
            i.setVisible(true);

        }
    }

    public static void main(String[] args) {
        //ensures GUI updates occur through the event dispatch thread 
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                //create application window
                JFrame frame = new JFrame("Sound Board");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                //set main content pan to the application window frame
                MainMenu mainMenu = new MainMenu(frame);
                frame.setContentPane(mainMenu);
                frame.pack();
                //center application window in the middle of the screen
                Toolkit toolkit = Toolkit.getDefaultToolkit();
                Dimension screenDimension = toolkit.getScreenSize();
                Dimension frameDimension = frame.getSize();
                frame.setLocation((screenDimension.width - frameDimension.width) / 2,
                        (screenDimension.height - frameDimension.height) / 2);
                //display application window
                frame.setVisible(true);
            }
        });
    }

}
