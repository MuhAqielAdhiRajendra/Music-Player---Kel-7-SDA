
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSlider;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

public class MusicPlayerGUI extends JFrame {
    // color configurations
    public static final Color FRAME_COLOR = Color.BLACK;
    public static final Color TEXT_COLOR = Color.WHITE;

    public MusicPlayerGUI() {
        // calls JFrame constructor to configure gui and set title header to "Music
        // Player"
        super("MP3 Music Player");

        // set width and height
        setSize(400, 600);

        // center gui on screen
        setLocationRelativeTo(null);

        // Prevent resizing
        setResizable(false);

        // Exit application when window is closed
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // set layout to null which allows us to control the (x,y) coordinates of our
        // components
        // and also set height and width
        setLayout(null);

        // change frame background color
        getContentPane().setBackground(FRAME_COLOR);

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
        JLabel songTitle = new JLabel("Song Title");
        songTitle.setBounds(0, 285, getWidth() - 10, 30);
        songTitle.setFont(new Font("Dialog", Font.BOLD, 24));
        songTitle.setForeground(TEXT_COLOR);
        songTitle.setHorizontalAlignment(SwingConstants.CENTER);
        add(songTitle);

        // song artist
        JLabel songArtist = new JLabel("Artist");
        songArtist.setBounds(0, 315, getWidth() - 10, 30);
        songArtist.setFont(new Font("Dialog", Font.PLAIN, 24));
        songArtist.setForeground(TEXT_COLOR);
        songArtist.setHorizontalAlignment(SwingConstants.CENTER);
        add(songArtist);

        // playback slider
        JSlider playbackSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
        playbackSlider.setBounds(getWidth() / 2 - 300 / 2, 365, 300, 40);
        playbackSlider.setBackground(null);
        add(playbackSlider);

        // playback buttons

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
        songMenu.add(loadSong);

        // add Play list menu
        JMenu playListMenu = new JMenu("Play List");
        menuBar.add(playListMenu);

        // add "Create Playlist" option to the play list menu
        JMenuItem createPlaylist = new JMenuItem("Create Playlist");
        playListMenu.add(createPlaylist);

        // add "Load Playlist" option to the play list menu
        JMenuItem loadPlaylist = new JMenuItem("Load Playlist");
        playListMenu.add(loadPlaylist);

        add(toolBar);
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
}
