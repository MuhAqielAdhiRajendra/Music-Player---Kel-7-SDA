import com.mpatric.mp3agic.Mp3File;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import java.io.File;

// class used to describe a song
public class Song {
    private String songTitle;
    private String songArtist;
    private String songGenre;
    private String songLength;
    private String filePath;
    private Mp3File mp3File;
    private double frameRatePerMilliseconds;

    public Song(String filePath) {
        this.filePath = filePath;
        try {
            mp3File = new Mp3File(filePath);
            frameRatePerMilliseconds = (double) mp3File.getFrameCount() / mp3File.getLengthInMilliseconds();
            songLength = convertToSongLengthFormat();

            // Creates an AudioFile object to read the selected MP3 file.
            AudioFile audioFile = AudioFileIO.read(new File(filePath));

            // read through the meta data of the audio file
            Tag tag = audioFile.getTag();
            if (tag != null) {
                songTitle = tag.getFirst(FieldKey.TITLE);
                songArtist = tag.getFirst(FieldKey.ARTIST);
                songGenre = tag.getFirst(FieldKey.GENRE);
                // set defaults for empty metadata
                if (songTitle == null || songTitle.isEmpty())
                    songTitle = "N/A";
                if (songArtist == null || songArtist.isEmpty())
                    songArtist = "N/A";
                if (songGenre == null || songGenre.isEmpty())
                    songGenre = "Unknown";
            } else {
                // could not read through mp3 file's meta data
                songTitle = "N/A";
                songArtist = "N/A";
                songGenre = "Unknown";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String convertToSongLengthFormat() {
        long minutes = mp3File.getLengthInSeconds() / 60;
        long seconds = mp3File.getLengthInSeconds() % 60;
        String formattedTime = String.format("%02d:%02d", minutes, seconds);

        return formattedTime;
    }

    // getters
    public String getSongTitle() {
        return songTitle;
    }

    public String getSongArtist() {
        return songArtist;
    }

    public String getSongGenre() {
        return songGenre;
    }

    public String getSongLength() {
        return songLength;
    }

    public String getFilePath() {
        return filePath;
    }

    public Mp3File getMp3File() {
        return mp3File;
    }

    public double getFrameRatePerMilliseconds() {
        return frameRatePerMilliseconds;
    }
}