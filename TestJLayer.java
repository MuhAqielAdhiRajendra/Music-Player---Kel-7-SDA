import javazoom.jl.player.advanced.*; 
import java.io.FileInputStream; 
import java.io.File;

public class TestJLayer { 
    public static void main(String[] args) throws Exception { 
        // Find an mp3 file to test with
        File assetsDir = new File("src/assets");
        File[] files = assetsDir.listFiles((d, name) -> name.endsWith(".mp3"));
        if (files == null || files.length == 0) {
            System.out.println("No mp3 found");
            return;
        }
        
        AdvancedPlayer p = new AdvancedPlayer(new FileInputStream(files[0])); 
        p.setPlayBackListener(new PlaybackListener() { 
            public void playbackFinished(PlaybackEvent e) { 
                System.out.println("Frame returned by getFrame(): " + e.getFrame()); 
            } 
        }); 
        
        System.out.println("Playing from 100 to 200...");
        p.play(100, 200); 
    } 
}
