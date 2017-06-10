package soundboard;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

/**
 *
 * @author mohammed
 * @author David
 *
 */
public class SoundPanel extends JPanel implements ActionListener {

    private final JButton soundAssignButton;    //audio clip assignment button
    private final JButton soundPlayButton;      //audio clip playback button
    private final SoundBoard soundBoard;        //reference to parent panel
    private File file;                          //assigned audio clip file
    private Clip clip;                          //loaded audio clip object on memory

    public SoundPanel(SoundBoard soundBoard) {
        //creates a single audio clip sound board item
        super(new GridLayout(1, 2));
        this.soundBoard = soundBoard;
        TitledBorder soundBoardTitledBorder = BorderFactory.createTitledBorder("Empty");
        setBorder(soundBoardTitledBorder);
        soundAssignButton = new JButton("Assign");
        soundAssignButton.addActionListener(this);
        add(soundAssignButton);
        soundPlayButton = new JButton("Play");
        soundPlayButton.setEnabled(false);
        soundPlayButton.addActionListener(this);
        add(soundPlayButton);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //audio clip file assignment to sound board item event
        if (e.getSource() == soundAssignButton) {
            //obtain file refrence to assigned audio clip
            file = soundBoard.getFile();
            if (file != null) {
                TitledBorder soundBoardTitledBorder = BorderFactory.createTitledBorder(file.getName());
                setBorder(soundBoardTitledBorder);
                soundPlayButton.setEnabled(true);
            }
            //audio clip playback event
        } else if (e.getSource() == soundPlayButton) {
            //load audio clip data from assigned file to
            //audio clip memory object via Audio System,
            //then commence playback from uploaded memory clip object
            AudioInputStream audioInputStream = null;
            try {
                audioInputStream = AudioSystem.getAudioInputStream(file);
                clip = AudioSystem.getClip();
                getClip().open(audioInputStream);
                getClip().start();
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
                Logger.getLogger(SoundPanel.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    if (audioInputStream != null) {
                        audioInputStream.close();
                    }
                } catch (IOException ex) {
                    Logger.getLogger(SoundPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    /**
     * @return the clip
     */
    public Clip getClip() {
        //provides access to audio clip object
        return clip;
    }

}
