package soundboard;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
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
    private final JButton soundLoopButton;      //enable/disable audio clip loop play back
    private final SoundBoard soundBoard;        //reference to parent panel
    private File file;                          //assigned audio clip file
    private Clip clip;                          //loaded audio clip object on memory
    private AudioInputStream audioInputStream;
    private boolean isLooping;
    private int playBackSpeed;

    public SoundPanel(SoundBoard soundBoard, int playBackSpeed) {
        //creates a single audio clip sound board item
        super(new GridLayout(1, 2));
        this.soundBoard = soundBoard;
        this.playBackSpeed = playBackSpeed;
        TitledBorder soundBoardTitledBorder = BorderFactory.createTitledBorder("Empty");
        setBorder(soundBoardTitledBorder);
        soundAssignButton = new JButton("Assign");
        soundAssignButton.addActionListener(this);
        add(soundAssignButton);
        soundPlayButton = new JButton("Play");
        soundPlayButton.setEnabled(false);
        soundPlayButton.addActionListener(this);
        add(soundPlayButton);
        soundLoopButton = new JButton("Loop On");
        soundLoopButton.setEnabled(false);
        soundLoopButton.addActionListener(this);
        add(soundLoopButton);
        isLooping = false;
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
                soundLoopButton.setEnabled(true);
            }
            try {
                if (audioInputStream != null) {
                    audioInputStream.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(SoundPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (e.getSource() == soundPlayButton) {
            playClip();
        } else if (e.getSource() == soundLoopButton) {
            if (!isLooping) {
                isLooping = true;
                playClip();
            } else {
                isLooping = false;
                stopLoop();
            }
        }
    }

    public void playClip() {
        if (file != null) {
            try {
                AudioInputStream tempAudioInputStream = AudioSystem.getAudioInputStream(file);
                AudioFormat audioFormat = tempAudioInputStream.getFormat();
                int frameSize = audioFormat.getFrameSize();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] bytes = new byte[2 ^ 16];
                int read = 1;
                while (read > -1) {
                    read = tempAudioInputStream.read(bytes);
                    if (read > 0) {
                        byteArrayOutputStream.write(bytes, 0, read);
                    }
                }
                byte[] bytes1 = byteArrayOutputStream.toByteArray();
                byte[] bytes2 = new byte[bytes1.length / playBackSpeed];
                for (int i = 0; i < bytes2.length / frameSize; i++) {
                    for (int j = 0; j < frameSize; j++) {
                        bytes2[(i * frameSize) + j] = bytes1[(i * frameSize * playBackSpeed) + j];
                    }
                }
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes2);
                //load audio clip data from assigned file to
                //audio clip memory object via Audio System,
                //then commence playback from uploaded memory clip object
                audioInputStream = new AudioInputStream(byteArrayInputStream, audioFormat, bytes2.length);
                clip = AudioSystem.getClip();
                clip.open(audioInputStream);
                if (isLooping) {
                    soundPlayButton.setEnabled(false);
                    clip.loop(Clip.LOOP_CONTINUOUSLY);
                    soundLoopButton.setText("Loop Off");
                }
                //audio clip playback event
                clip.start();
            } catch (LineUnavailableException | IOException | UnsupportedAudioFileException ex) {
                Logger.getLogger(SoundPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void setPlayBackSpeed(int playBackSpeed) {
        this.playBackSpeed = playBackSpeed;
    }

    public void stopLoop() {
        if (clip != null && clip.isRunning()) {
            clip.loop(0);
            soundPlayButton.setEnabled(true);
            soundLoopButton.setText("Loop On");
        }
    }

    public void closeAudioStream() {
        try {
            if (audioInputStream != null) {
                audioInputStream.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(SoundPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
