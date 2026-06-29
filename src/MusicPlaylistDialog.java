import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class MusicPlaylistDialog extends JDialog {
    private MusicPlayerGUI musicPlayerGUI;

    // Store all of the paths to be written to a txt file (when we load a playlist)
    private ArrayList<String> songPaths;

    public MusicPlaylistDialog(MusicPlayerGUI musicPlayerGUI) {
        this.musicPlayerGUI = musicPlayerGUI;
        songPaths = new ArrayList<>();

        // configure dialog
        setTitle("Create PlayList");
        setSize(400, 400);
        setResizable(false);
        getContentPane().setBackground(MusicPlayerGUI.FRAME_COLOR);
        setLayout(null);
        setModal(true);
        setLocationRelativeTo(musicPlayerGUI);

        addDialogComponents();
    }

    private void addDialogComponents() {
        // Container to hold all of the added songs
        JPanel songContainer = new JPanel();
        songContainer.setLayout(new BoxLayout(songContainer, BoxLayout.Y_AXIS));
        songContainer.setBounds((int) (getWidth() * 0.025), 10, (int) (getWidth() * 0.90), (int) (getHeight() * 0.75));
        songContainer.setBackground(MusicPlayerGUI.FRAME_COLOR); // match frame color
        add(songContainer);

        // "Add" button
        JButton addSongButton = new JButton("add");
        addSongButton.setBounds(60, (int) (getHeight() * 0.80), 100, 25);
        addSongButton.setFont(new Font("Dialog", Font.BOLD, 14));
        addSongButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // open file explorer
                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.setFileFilter(new FileNameExtensionFilter("MP3", "mp3"));
                jFileChooser.setCurrentDirectory(new File("src/assets"));
                int result = jFileChooser.showOpenDialog(MusicPlaylistDialog.this);
                File selectedFile = jFileChooser.getSelectedFile();

                if (result == JFileChooser.APPROVE_OPTION && selectedFile != null) {
                    // Create label displaying the file name
                    JLabel filePathLabel = new JLabel(selectedFile.getName());
                    filePathLabel.setFont(new Font("Dialog", Font.BOLD, 12));
                    filePathLabel.setForeground(MusicPlayerGUI.TEXT_COLOR);

                    // add to the list of paths
                    songPaths.add(selectedFile.getPath());

                    // add label to container to show the user
                    songContainer.add(filePathLabel);

                    // refreshes dialog to show newly added JLabel
                    songContainer.revalidate();
                    songContainer.repaint();
                }
            }
        });
        add(addSongButton);

        // "Save" button
        JButton savePlaylistButton = new JButton("save");
        savePlaylistButton.setBounds(215, (int) (getHeight() * 0.80), 100, 25);
        savePlaylistButton.setFont(new Font("Dialog", Font.BOLD, 14));
        savePlaylistButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // If playlist is empty, don't save
                if (songPaths.isEmpty()) {
                    JOptionPane.showMessageDialog(MusicPlaylistDialog.this, "Playlist cannot be empty!");
                    return;
                }

                try {
                    JFileChooser jFileChooser = new JFileChooser();
                    jFileChooser.setCurrentDirectory(new File("src/assets"));
                    int result = jFileChooser.showSaveDialog(MusicPlaylistDialog.this);

                    if (result == JFileChooser.APPROVE_OPTION) {
                        File selectedFile = jFileChooser.getSelectedFile();

                        // Append .txt if not present
                        if (!selectedFile.getName().toLowerCase().endsWith(".txt")) {
                            selectedFile = new File(selectedFile.getAbsolutePath() + ".txt");
                        }

                        // Write song paths to file
                        BufferedWriter writer = new BufferedWriter(new FileWriter(selectedFile));
                        for (String path : songPaths) {
                            writer.write(path + "\n");
                        }
                        writer.close();

                        JOptionPane.showMessageDialog(MusicPlaylistDialog.this, "Successfully Created Playlist!");

                        // Close dialog
                        dispose();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        add(savePlaylistButton);
    }
}
