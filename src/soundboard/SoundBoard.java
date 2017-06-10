package soundboard;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioSystem;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.Timer;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author mohammed
 * @author David
 *
 */
public class SoundBoard extends JPanel implements ActionListener, ListSelectionListener, ChangeListener {

    private static final int DEFAULT_WIDTH = 800;                   //default application window width
    private static final int DEFAULT_HEIGHT = 600;                  //default application window height
    private static final int DEFAULT_PATH_LIST_ITEM_DISPLAY = 8;    //default list item display

    private final JFrame frame;                                     //application window

    private final JMenuItem addFilePathMenuItem;                    //add file path file menu item
    private final JMenuItem removeFilePathMenuItem;                 //remove file path file menu item
    private final JMenuItem exitMenuItem;                           //exit application file menu item

    //audio clip directory path display list and model
    private final DefaultListModel pathListModel;
    private final JList pathList;

    //audio clip file display list and model
    private final DefaultListModel fileListModel;
    private final JList fileList;

    private final JButton soundLoopButton;                          //enable/disable audio clip loop play back
    private final JButton soundStopButton;                          //stop audio clip play back
    private final JSpinner soundPlaySpeedSpinner;                   //audio clip playback speed adjustment

    private File[] files;                                           //current displayed audio clip files
    private File file;                                              //current selected audio clip file from current displayed audio clip files

    public SoundBoard(JFrame frame) {
        //set top-level panel layout behaviour and dimensions
        super(new BorderLayout());
        setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        this.frame = frame;

        //create application menu bar and file menu
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        menuBar.add(fileMenu);

        //create add file path file menu item
        addFilePathMenuItem = new JMenuItem("Add Path");
        addFilePathMenuItem.setMnemonic(KeyEvent.VK_A);
        addFilePathMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_A, ActionEvent.SHIFT_MASK));
        addFilePathMenuItem.addActionListener(this);
        fileMenu.add(addFilePathMenuItem);

        //create remove file path file menu item
        removeFilePathMenuItem = new JMenuItem("Remove Path");
        removeFilePathMenuItem.setMnemonic(KeyEvent.VK_R);
        removeFilePathMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_R, ActionEvent.SHIFT_MASK));
        removeFilePathMenuItem.addActionListener(this);
        fileMenu.add(removeFilePathMenuItem);

        //file menu seperator
        fileMenu.addSeparator();

        //create application exit file menu item
        exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.setMnemonic(KeyEvent.VK_E);
        exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_E, ActionEvent.SHIFT_MASK));
        exitMenuItem.addActionListener(this);
        fileMenu.add(exitMenuItem);

        this.frame.setJMenuBar(menuBar);

        //create panel for audio clip path and file display
        JPanel soundAssignmentPanel = new JPanel(new GridLayout(1, 2));

        //create display list for audio clip paths
        TitledBorder filePathTitledBorder = BorderFactory.createTitledBorder("Sound File Paths");
        JPanel filePathPanel = new JPanel(new GridLayout(1, 1));
        filePathPanel.setBorder(filePathTitledBorder);
        soundAssignmentPanel.add(filePathPanel);

        pathListModel = new DefaultListModel();
        pathList = new JList(pathListModel);
        pathList.setVisibleRowCount(DEFAULT_PATH_LIST_ITEM_DISPLAY);
        pathList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        pathList.addListSelectionListener(this);
        JScrollPane pathListScroller = new JScrollPane(pathList);
        filePathPanel.add(pathListScroller);

        //create display list for audio clip files
        TitledBorder soundsFilesTitledBorder = BorderFactory.createTitledBorder("Sound Files");
        JPanel soundsFilePanel = new JPanel(new GridLayout(1, 1));
        soundsFilePanel.setBorder(soundsFilesTitledBorder);
        soundAssignmentPanel.add(soundsFilePanel);

        fileListModel = new DefaultListModel();
        fileList = new JList(fileListModel);
        fileList.setVisibleRowCount(DEFAULT_PATH_LIST_ITEM_DISPLAY);
        fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fileList.addListSelectionListener(this);
        JScrollPane fileListScroller = new JScrollPane(fileList);
        soundsFilePanel.add(fileListScroller);

        add(soundAssignmentPanel, BorderLayout.NORTH);

        //create 4x4 audio clip sound board assignment and playback panels
        TitledBorder soundBoardTitledBorder = BorderFactory.createTitledBorder("Sound Board");
        JPanel soundBoardPanel = new JPanel(new GridLayout(4, 4));
        for (int i = 0; i < 16; i++) {
            soundBoardPanel.add(new SoundPanel(this));
        }
        soundBoardPanel.setBorder(soundBoardTitledBorder);
        add(soundBoardPanel, BorderLayout.CENTER);

        //create sound control panel for audio clip loop, stop, and speed controls
        TitledBorder soundControlTitledBorder = BorderFactory.createTitledBorder("Sound Control");
        JPanel soundControlPanel = new JPanel(new GridLayout(1, 3));
        soundControlPanel.setBorder(soundControlTitledBorder);
        soundLoopButton = new JButton("Loop");
        soundLoopButton.addActionListener(this);
        soundControlPanel.add(soundLoopButton);
        soundStopButton = new JButton("Stop");
        soundStopButton.addActionListener(this);
        soundControlPanel.add(soundStopButton);
        JPanel soundPlaySpeedPanel = new JPanel(new GridLayout(1, 2));
        JLabel soundPlaySpeedLabel = new JLabel("Playback Speed:");
        soundPlaySpeedPanel.add(soundPlaySpeedLabel);
        SpinnerModel playSpeedSpinnerModel
                = new SpinnerNumberModel(3, //initial value
                        1, //min
                        10, //max
                        1);  //step
        soundPlaySpeedSpinner = new JSpinner(playSpeedSpinnerModel);
        ((DefaultEditor) soundPlaySpeedSpinner.getEditor()).getTextField().setEditable(false);
        soundPlaySpeedSpinner.addChangeListener(this);
        soundPlaySpeedPanel.add(soundPlaySpeedSpinner);
        soundControlPanel.add(soundPlaySpeedPanel);
        add(soundControlPanel, BorderLayout.SOUTH);

        //application state persistence thread (saves application state to file), called when application is shutdown
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {

            }
        });
    }

    //method to get currently selected audio clip file
    File getFile() {
        return file;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        //add audio clips path event
        if (e.getSource() == addFilePathMenuItem) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnVal = fileChooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                pathListModel.addElement(fileChooser.getSelectedFile().getAbsolutePath());
            }
            //remove audio clips path event
        } else if (e.getSource() == removeFilePathMenuItem) {
            int pathIndex = pathList.getSelectedIndex();
            if (pathIndex != -1) {
                pathListModel.remove(pathIndex);
                if (pathListModel.isEmpty()) {
                    fileListModel.removeAllElements();
                }
            }
            //application exit event from file menu
        } else if (e.getSource() == exitMenuItem) {
            frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
            //enbale/disable audio clip loop playback event
        } else if (e.getSource() == soundLoopButton) {

            //stop audio clip playback event
        } else if (e.getSource() == soundStopButton) {

        } else if (e.getSource() == exitMenuItem) {

            frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        // ignore display list value adjusting events to prevent doublling up
        // when responding to value has changed events
        if (!e.getValueIsAdjusting()) {
            // source is audio clips path display list
            if (e.getSource() == pathList) {
                // get all supported audio clip formats form audio system
                AudioFileFormat.Type[] types = AudioSystem.getAudioFileTypes();
                ArrayList<String> extensions = new ArrayList<>();
                for (AudioFileFormat.Type type : types) {
                    extensions.add(type.getExtension());
                }
                // insure a path is selected from the path list
                if (pathList.getSelectedValue() != null) {
                    // filter only audio clip supported formats at location given by path
                    // and place these in an array of files
                    File directory = new File(pathList.getSelectedValue().toString());
                    files = directory.listFiles(new FileFilter() {
                        private final FileNameExtensionFilter filter
                                = new FileNameExtensionFilter("Sound file",
                                        extensions.toArray(new String[extensions.size()]));

                        @Override
                        public boolean accept(File file) {
                            return filter.accept(file);
                        }
                    });
                    // update the audio clip file model with found suppported audio clips
                    fileListModel.removeAllElements();
                    for (File file : files) {
                        if (file.isFile()) {
                            fileListModel.addElement(file.getName());
                        }
                    }
                }
                // source is audio clips file display list
            } else if (e.getSource() == fileList) {
                // ensure audio clip file is selected
                if (fileList.getSelectedIndex() != -1) {
                    // reference to currently selected audio clip file from current audio clip file array
                    file = files[fileList.getSelectedIndex()];
                }
            }
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        // audio clip playback speed event handler

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //ensures GUI updates occur through the event dispatch thread 
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                //create application window
                JFrame frame = new JFrame("Sound Board");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                //set main content pan to the application window frame
                SoundBoard soundBoard = new SoundBoard(frame);
                frame.setContentPane(soundBoard);
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
