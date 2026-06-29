import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Hashtable;
import java.util.List;

public class MusicPlayerGUI extends JFrame {
    // color configurations
    public static final Color FRAME_COLOR = Color.BLACK;
    public static final Color TEXT_COLOR = Color.WHITE;

    private MusicPlayer musicPlayer;

    // Katalog musik hierarkis (Tree) - digunakan oleh seluruh aplikasi
    private MusicCatalogTree musicCatalogTree;

    // allow us to use file explorer in our app
    private JFileChooser jFileChooser;

    private JLabel songTitle, songArtist;
    private JPanel playbackBtns;
    private JSlider playbackSlider;
    private JLabel timeElapsedLabel;

    public MusicPlayerGUI() {
        // calls JFrame constructor to configure gui and set title header to
        // "MusicPlayer"
        super("MP3 Music Player");

        // set width and height
        setSize(400, 600);

        // end process when app is closed
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // center gui on screen
        setLocationRelativeTo(null);

        // Prevent resizing
        setResizable(false);

        // set layout to null which allows us to control the (x,y) coordinates of our
        // components
        // and also set height and width
        setLayout(null);

        // change frame background color
        getContentPane().setBackground(FRAME_COLOR);

        musicPlayer = new MusicPlayer(this);
        musicCatalogTree = new MusicCatalogTree();
        jFileChooser = new JFileChooser();

        // set a default path for file explorer
        jFileChooser.setCurrentDirectory(new File("src/assets"));

        // filter file chooser to only see .mp3 files
        jFileChooser.setFileFilter(new FileNameExtensionFilter("MP3", "mp3"));

        addGuiComponents();
    }

    private void addGuiComponents() {
        // add toolbar
        addToolbar();

        // load record image
        JLabel songImage = new JLabel(loadImage("/assets/record.png"));
        songImage.setBounds(0, 50, getWidth() - 20, 225);
        add(songImage);

        // song title
        songTitle = new JLabel("Song Title");
        songTitle.setBounds(0, 285, getWidth() - 10, 30);
        songTitle.setFont(new Font("Dialog", Font.BOLD, 24));
        songTitle.setForeground(TEXT_COLOR);
        songTitle.setHorizontalAlignment(SwingConstants.CENTER);
        add(songTitle);

        // song artist
        songArtist = new JLabel("Artist");
        songArtist.setBounds(0, 315, getWidth() - 10, 30);
        songArtist.setFont(new Font("Dialog", Font.PLAIN, 24));
        songArtist.setForeground(TEXT_COLOR);
        songArtist.setHorizontalAlignment(SwingConstants.CENTER);
        add(songArtist);

        // playback slider
        playbackSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
        playbackSlider.setBounds(getWidth() / 2 - 300 / 2, 365, 300, 40);
        playbackSlider.setBackground(null);
        playbackSlider.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // when the user is holding the tick we want to the pause the song
                musicPlayer.pauseSong();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // when the user drops the tick
                JSlider source = (JSlider) e.getSource();

                // get the frame value from where the user wants to playback to
                int frame = source.getValue();

                // update the current frame in the music player to this frame
                musicPlayer.setCurrentFrame(frame);

                // check if a song is actually loaded to prevent NullPointerException
                if (musicPlayer.getCurrentSong() == null) {
                    return;
                }

                // update current time in milli as well
                musicPlayer.setCurrentTimeInMilli(
                        (int) (frame / musicPlayer.getCurrentSong().getFrameRatePerMilliseconds()));

                // resume the song
                musicPlayer.playCurrentSong();

                // toggle on pause button and toggle off play button
                enablePauseButtonDisablePlayButton();
            }
        });
        add(playbackSlider);

        // playback buttons (i.e. previous, play, next)
        addPlaybackBtns();
    }

    private void addToolbar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setBounds(0, 0, getWidth(), 20);

        // prevent toolbar from being moved around
        toolBar.setFloatable(false);

        // add drop down menu
        JMenuBar menuBar = new JMenuBar();
        toolBar.add(menuBar);

        // Song menu option to place song loading option
        JMenu songMenu = new JMenu("Song");
        menuBar.add(songMenu);

        // add "Load Song" option to the song menu
        JMenuItem loadSong = new JMenuItem("Load Song");
        loadSong.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // an integer is returned to us to let us know what the user did
                int result = jFileChooser.showOpenDialog(MusicPlayerGUI.this);
                File selectedFile = jFileChooser.getSelectedFile();

                // Verify that a file was selected successfully.
                if (result == JFileChooser.APPROVE_OPTION && selectedFile != null) {
                    // create a song obj based on selected file
                    Song song = new Song(selectedFile.getPath());

                    // load song in music player
                    musicPlayer.loadSong(song);

                    // tambahkan ke tree catalog agar menu catalog terupdate
                    musicCatalogTree.addSong(song);

                    // update song title and artist
                    updateSongTitleAndArtist(song);

                    // update playback slider
                    updatePlaybackSlider(song);

                    // toggle on pause button and toggle off play button
                    enablePauseButtonDisablePlayButton();
                }
            }
        });
        songMenu.add(loadSong);

        // add Play list menu
        JMenu playListMenu = new JMenu("Play List");
        menuBar.add(playListMenu);

        JMenuItem createPlaylist = new JMenuItem("Create Playlist");
        createPlaylist.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new MusicPlaylistDialog(MusicPlayerGUI.this).setVisible(true);
            }
        });
        playListMenu.add(createPlaylist);

        // add "Load Playlist" option to the play list menu
        JMenuItem loadPlaylist = new JMenuItem("Load Playlist");
        loadPlaylist.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.setFileFilter(new FileNameExtensionFilter("Playlist", "txt"));
                jFileChooser.setCurrentDirectory(new File("src/assets"));

                int result = jFileChooser.showOpenDialog(MusicPlayerGUI.this);
                File selectedFile = jFileChooser.getSelectedFile();

                if (result == JFileChooser.APPROVE_OPTION && selectedFile != null) {
                    // stop the music
                    musicPlayer.stopSong();

                    // load playlist
                    musicPlayer.loadPlaylist(selectedFile);

                    // tambahkan lagu-lagu dari playlist ke catalog tree
                    if (musicPlayer.getCurrentSong() != null) {
                        musicCatalogTree.addSong(musicPlayer.getCurrentSong());
                    }
                    for (Song s : musicPlayer.getNextQueue()) {
                        musicCatalogTree.addSong(s);
                    }
                }
            }
        });
        playListMenu.add(loadPlaylist);

        // === Menu Katalog ===
        JMenu catalogMenu = new JMenu("Catalog");
        menuBar.add(catalogMenu);

        // Item: Buka Katalog Musik (Hierarchical Tree)
        JMenuItem openCatalog = new JMenuItem("Music Catalog (Tree)");
        openCatalog.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new MusicCatalogDialog(MusicPlayerGUI.this, musicCatalogTree, musicPlayer).setVisible(true);
            }
        });
        catalogMenu.add(openCatalog);

        // Item: Lihat Antrean Putar (FIFO Queue)
        JMenuItem viewQueue = new JMenuItem("View Queue (FIFO)");
        viewQueue.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showQueueDialog();
            }
        });
        catalogMenu.add(viewQueue);

        // Item: Lihat Riwayat Putar (LIFO History)
        JMenuItem viewHistory = new JMenuItem("View History (LIFO)");
        viewHistory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showHistoryDialog();
            }
        });
        catalogMenu.add(viewHistory);

        add(toolBar);
    }

    private void addPlaybackBtns() {
        playbackBtns = new JPanel();
        playbackBtns.setBounds(0, 435, getWidth() - 10, 80);
        playbackBtns.setBackground(null);

        // previous button
        JButton prevButton = new JButton(loadImage("src/assets/previous.png"));
        prevButton.setBorderPainted(false);
        prevButton.setBackground(null);
        prevButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // go to the previous song
                musicPlayer.prevSong();
            }
        });
        playbackBtns.add(prevButton);

        // play button
        JButton playButton = new JButton(loadImage("src/assets/play.png"));
        playButton.setBorderPainted(false);
        playButton.setBackground(null);
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // toggle off play button and toggle on pause button
                enablePauseButtonDisablePlayButton();

                // play or resume song
                musicPlayer.playCurrentSong();
            }
        });
        playbackBtns.add(playButton);

        // pause button
        JButton pauseButton = new JButton(loadImage("src/assets/pause.png"));
        pauseButton.setBorderPainted(false);
        pauseButton.setBackground(null);
        pauseButton.setVisible(false);
        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // toggle off pause button and toggle on play button
                enablePlayButtonDisablePauseButton();

                // pause the song
                musicPlayer.pauseSong();
            }
        });
        playbackBtns.add(pauseButton);

        // next button
        JButton nextButton = new JButton(loadImage("src/assets/next.png"));
        nextButton.setBorderPainted(false);
        nextButton.setBackground(null);
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // go to the next song
                musicPlayer.nextSong();
            }
        });
        playbackBtns.add(nextButton);

        add(playbackBtns);
    }

    // this will be used to update our slider from the music player class
    public void setPlaybackSliderValue(int frame) {
        playbackSlider.setValue(frame);
        if (timeElapsedLabel != null && musicPlayer.getCurrentSong() != null) {
            int currentTimeInMilli = (int) (frame
                    / musicPlayer.getCurrentSong().getFrameRatePerMilliseconds());
            long minutes = (currentTimeInMilli / 1000) / 60;
            long seconds = (currentTimeInMilli / 1000) % 60;
            String formattedTime = String.format("%02d:%02d", minutes, seconds);
            timeElapsedLabel.setText(formattedTime);
            playbackSlider.repaint();
        }
    }

    public void updateSongTitleAndArtist(Song song) {
        songTitle.setText(song.getSongTitle());
        songArtist.setText(song.getSongArtist());
    }

    public void updatePlaybackSlider(Song song) {
        // update max count for slider
        playbackSlider.setMaximum(song.getMp3File().getFrameCount());

        // create the song length label
        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();

        // beginning will be 00:00
        timeElapsedLabel = new JLabel("00:00");
        timeElapsedLabel.setFont(new Font("Dialog", Font.BOLD, 18));
        timeElapsedLabel.setForeground(TEXT_COLOR);

        // end will vary depending on the song
        JLabel labelEnd = new JLabel(song.getSongLength());
        labelEnd.setFont(new Font("Dialog", Font.BOLD, 18));
        labelEnd.setForeground(TEXT_COLOR);

        labelTable.put(0, timeElapsedLabel);
        labelTable.put(song.getMp3File().getFrameCount(), labelEnd);

        playbackSlider.setLabelTable(labelTable);
        playbackSlider.setPaintLabels(true);
    }

    public void enablePauseButtonDisablePlayButton() {
        // retrieve reference to play button from playbackBtns panel
        JButton playButton = (JButton) playbackBtns.getComponent(1);
        JButton pauseButton = (JButton) playbackBtns.getComponent(2);

        // turn off play button
        playButton.setVisible(false);
        playButton.setEnabled(false);

        // turn on pause button
        pauseButton.setVisible(true);
        pauseButton.setEnabled(true);
    }

    public void enablePlayButtonDisablePauseButton() {
        // retrieve reference to play button from playbackBtns panel
        JButton playButton = (JButton) playbackBtns.getComponent(1);
        JButton pauseButton = (JButton) playbackBtns.getComponent(2);

        // turn on play button
        playButton.setVisible(true);
        playButton.setEnabled(true);

        // turn off pause button
        pauseButton.setVisible(false);
        pauseButton.setEnabled(false);
    }

    private ImageIcon loadImage(String imagePath) {
        try {
            // Try loading as a classpath resource first
            java.net.URL imageUrl = MusicPlayerGUI.class.getResource(imageUrlPath(imagePath));
            if (imageUrl != null) {
                return new ImageIcon(ImageIO.read(imageUrl));
            }

            // Fallback to loading from the file system
            File file = new File(imagePath);
            if (file.exists()) {
                return new ImageIcon(ImageIO.read(file));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // could not find resource
        return null;
    }

    private String imageUrlPath(String path) {
        if (path.startsWith("src/")) {
            path = path.substring(3);
        }
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        return path;
    }

    /**
     * Menampilkan dialog Antrean Putar (FIFO Queue).
     * Lagu ditampilkan dalam urutan FIFO: lagu pertama di list akan diputar
     * pertama.
     * Pengguna bisa menghapus lagu dari antrean.
     */
    private void showQueueDialog() {
        JDialog dialog = new JDialog(this, "Antrean Putar - Next in Queue (FIFO)", true);
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(FRAME_COLOR);
        dialog.setLayout(new BorderLayout());

        // Header
        JLabel header = new JLabel("Antrean Putar (FIFO - First In, First Out)");
        header.setFont(new Font("Dialog", Font.BOLD, 14));
        header.setForeground(TEXT_COLOR);
        header.setHorizontalAlignment(SwingConstants.CENTER);
        header.setOpaque(true);
        header.setBackground(FRAME_COLOR);
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        dialog.add(header, BorderLayout.NORTH);

        // List model
        DefaultListModel<String> listModel = new DefaultListModel<>();
        List<Song> queue = musicPlayer.getNextQueue();
        for (int i = 0; i < queue.size(); i++) {
            Song s = queue.get(i);
            listModel.addElement((i + 1) + ". " + s.getSongTitle() + " - " + s.getSongArtist());
        }
        if (queue.isEmpty()) {
            listModel.addElement("(Antrean kosong)");
        }

        JList<String> jList = new JList<>(listModel);
        jList.setBackground(Color.DARK_GRAY);
        jList.setForeground(TEXT_COLOR);
        jList.setFont(new Font("Dialog", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(jList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        dialog.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(FRAME_COLOR);
        JButton removeBtn = new JButton("Hapus dari Antrean");
        removeBtn.setFont(new Font("Dialog", Font.BOLD, 12));
        removeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = jList.getSelectedIndex();
                if (index >= 0 && !musicPlayer.getNextQueue().isEmpty()) {
                    musicPlayer.removeFromQueue(index);
                    // Refresh list
                    listModel.clear();
                    List<Song> updatedQueue = musicPlayer.getNextQueue();
                    for (int i = 0; i < updatedQueue.size(); i++) {
                        Song s = updatedQueue.get(i);
                        listModel.addElement((i + 1) + ". " + s.getSongTitle() + " - " + s.getSongArtist());
                    }
                    if (updatedQueue.isEmpty()) {
                        listModel.addElement("(Antrean kosong)");
                    }
                }
            }
        });
        btnPanel.add(removeBtn);

        JButton closeBtn = new JButton("Tutup");
        closeBtn.setFont(new Font("Dialog", Font.BOLD, 12));
        closeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        btnPanel.add(closeBtn);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    /**
     * Menampilkan dialog Riwayat Putar (LIFO Stack / History).
     * Lagu ditampilkan dari yang terakhir diputar (atas stack) ke yang pertama.
     * Tombol "Undo/Back" memungkinkan pengguna kembali ke lagu sebelumnya (LIFO
     * pop).
     */
    private void showHistoryDialog() {
        JDialog dialog = new JDialog(this, "Riwayat Putar - History (LIFO)", true);
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(FRAME_COLOR);
        dialog.setLayout(new BorderLayout());

        // Header
        JLabel header = new JLabel("Riwayat Putar (LIFO - Last In, First Out)");
        header.setFont(new Font("Dialog", Font.BOLD, 14));
        header.setForeground(TEXT_COLOR);
        header.setHorizontalAlignment(SwingConstants.CENTER);
        header.setOpaque(true);
        header.setBackground(FRAME_COLOR);
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        dialog.add(header, BorderLayout.NORTH);

        // List model — tampilkan dari atas stack (terakhir diputar) ke bawah
        DefaultListModel<String> listModel = new DefaultListModel<>();
        List<Song> history = musicPlayer.getHistoryStack();
        if (history.isEmpty()) {
            listModel.addElement("(Belum ada riwayat)");
        } else {
            // Tampilkan dari terakhir (top of stack) ke pertama
            for (int i = history.size() - 1; i >= 0; i--) {
                Song s = history.get(i);
                listModel.addElement((history.size() - i) + ". " + s.getSongTitle() + " - " + s.getSongArtist());
            }
        }

        JList<String> jList = new JList<>(listModel);
        jList.setBackground(Color.DARK_GRAY);
        jList.setForeground(TEXT_COLOR);
        jList.setFont(new Font("Dialog", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(jList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        dialog.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(FRAME_COLOR);

        JButton undoBtn = new JButton("Undo/Back (Previous)");
        undoBtn.setFont(new Font("Dialog", Font.BOLD, 12));
        undoBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (musicPlayer.getHistoryStack().size() > 1) {
                    musicPlayer.prevSong();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Tidak ada lagu sebelumnya di riwayat untuk di-undo!");
                }
            }
        });
        btnPanel.add(undoBtn);

        JButton closeBtn = new JButton("Tutup");
        closeBtn.setFont(new Font("Dialog", Font.BOLD, 12));
        closeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        btnPanel.add(closeBtn);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
}
