package soundboard;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

/**
 * 
 *  @author mohammed
 *  @author David
 * 
 */
public class SoundPanel extends JPanel implements ActionListener {

    private final JButton soundAssignButton;
    private final JButton soundPlayButton;
    private final SoundBoard soundBoard;
    
    
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
        
    }
}
