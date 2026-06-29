import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

import java.io.*;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Stack;

public class MusicPlayer extends PlaybackListener {
    private static final Object playSignal = new Object();
    private MusicPlayerGUI musicPlayerGUI;
    private Song currentSong;
    private Deque<Song> nextQueue;
    private Stack<Song> historyStack;
    private AdvancedPlayer advancedPlayer;
    private boolean isPaused;
    private boolean songFinished;
    private boolean pressedNext, pressedPrev;
    private int currentFrame;
    private int currentTimeInMilli;

    public Song getCurrentSong() {
        return currentSong;
    }

    public void setCurrentFrame(int frame) {
        currentFrame = frame;
    }

    public void setCurrentTimeInMilli(int timeInMilli) {
        currentTimeInMilli = timeInMilli;
    }

    public MusicPlayer(MusicPlayerGUI musicPlayerGUI) {
        this.musicPlayerGUI = musicPlayerGUI;
        // Inisialisasi queue (FIFO) dan history stack (LIFO) agar tidak pernah null
        nextQueue = new LinkedList<>();
        historyStack = new Stack<>();
    }

    public void loadSong(Song song) {
        currentSong = song;
        // Tidak mereset nextQueue dan historyStack agar data antrean dan riwayat tetap
        // ada

        if (!songFinished)
            stopSong();

        if (currentSong != null) {
            currentFrame = 0;
            currentTimeInMilli = 0;
            musicPlayerGUI.setPlaybackSliderValue(0);
            playCurrentSong();
        }
    }

    public void loadPlaylist(File playlistFile) {
        // Reinisialisasi queue dan history saat memuat playlist baru
        nextQueue = new LinkedList<>();
        historyStack = new Stack<>();

        try {
            FileReader fileReader = new FileReader(playlistFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String songPath;
            while ((songPath = bufferedReader.readLine()) != null) {
                Song song = new Song(songPath);
                nextQueue.add(song);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!nextQueue.isEmpty()) {
            musicPlayerGUI.setPlaybackSliderValue(0);
            currentTimeInMilli = 0;
            currentSong = nextQueue.poll();
            currentFrame = 0;
            musicPlayerGUI.enablePauseButtonDisablePlayButton();
            musicPlayerGUI.updateSongTitleAndArtist(currentSong);
            musicPlayerGUI.updatePlaybackSlider(currentSong);
            playCurrentSong();
        }
    }

    public void pauseSong() {
        if (advancedPlayer != null) {
            isPaused = true;
            stopSong();
        }
    }

    // === Getter dan method tambahan untuk Queue (FIFO) dan History (LIFO) ===

    /**
     * Menambahkan lagu ke akhir antrean putar (FIFO: First-In, First-Out).
     * Lagu yang ditambahkan pertama akan diputar pertama.
     */
    public void addToQueue(Song song) {
        nextQueue.add(song); // add() menambah ke akhir (tail) Deque -> FIFO
    }

    /**
     * Menghapus lagu dari antrean putar berdasarkan indeks.
     */
    public void removeFromQueue(int index) {
        if (index >= 0 && index < nextQueue.size()) {
            // Konversi Deque ke List untuk akses berdasarkan indeks
            java.util.List<Song> list = new java.util.ArrayList<>(nextQueue);
            list.remove(index);
            nextQueue = new LinkedList<>(list);
        }
    }

    /**
     * Getter untuk antrean putar (FIFO Queue).
     * Mengembalikan copy agar data internal tidak dimodifikasi langsung.
     */
    public java.util.List<Song> getNextQueue() {
        return new java.util.ArrayList<>(nextQueue);
    }

    /**
     * Getter untuk riwayat putar (LIFO Stack).
     * Mengembalikan copy agar data internal tidak dimodifikasi langsung.
     */
    public java.util.List<Song> getHistoryStack() {
        return new java.util.ArrayList<>(historyStack);
    }

    public void stopSong() {
        if (advancedPlayer != null) {
            advancedPlayer.stop();
            advancedPlayer.close();
            advancedPlayer = null;
        }
    }

    public void nextSong() {
        if (nextQueue.isEmpty())
            return;
        pressedNext = true;

        if (!songFinished)
            stopSong();

        if (currentSong != null) {
            historyStack.push(currentSong);
        }
        currentSong = nextQueue.poll();
        currentFrame = 0;
        currentTimeInMilli = 0;
        musicPlayerGUI.enablePauseButtonDisablePlayButton();
        musicPlayerGUI.updateSongTitleAndArtist(currentSong);
        musicPlayerGUI.updatePlaybackSlider(currentSong);
        playCurrentSong();
    }

    /**
     * Kembali ke lagu sebelumnya (Undo/Back).
     * Menggunakan prinsip LIFO: lagu terakhir yang dimasukkan ke historyStack
     * akan menjadi lagu yang diputar kembali (pop dari stack).
     */
    public void prevSong() {
        if (historyStack.isEmpty())
            return;
        pressedPrev = true;

        if (!songFinished)
            stopSong();

        if (currentSong != null) {
            nextQueue.addFirst(currentSong);
        }
        currentSong = historyStack.pop();
        currentFrame = 0;
        currentTimeInMilli = 0;
        musicPlayerGUI.enablePauseButtonDisablePlayButton();
        musicPlayerGUI.updateSongTitleAndArtist(currentSong);
        musicPlayerGUI.updatePlaybackSlider(currentSong);
        playCurrentSong();
    }

    public void playCurrentSong() {
        if (currentSong == null)
            return;
        try {
            FileInputStream fileInputStream = new FileInputStream(currentSong.getFilePath());
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            advancedPlayer = new AdvancedPlayer(bufferedInputStream);
            advancedPlayer.setPlayBackListener(this);
            startMusicThread();
            startPlaybackSliderThread();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startMusicThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (isPaused) {
                        synchronized (playSignal) {
                            isPaused = false;
                            playSignal.notify();
                        }
                        advancedPlayer.play(currentFrame, Integer.MAX_VALUE);
                    } else {
                        advancedPlayer.play();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void startPlaybackSliderThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (isPaused) {
                    try {
                        synchronized (playSignal) {
                            playSignal.wait();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                while (!isPaused && !songFinished && !pressedNext && !pressedPrev) {
                    try {
                        currentTimeInMilli++;
                        int calculatedFrame = (int) ((double) currentTimeInMilli * 2.08
                                * currentSong.getFrameRatePerMilliseconds());
                        musicPlayerGUI.setPlaybackSliderValue(calculatedFrame);
                        Thread.sleep(1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public void playbackStarted(PlaybackEvent evt) {
        System.out.println("Playback Started");
        songFinished = false;
        pressedNext = false;
        pressedPrev = false;
    }

    @Override
    public void playbackFinished(PlaybackEvent evt) {
        System.out.println("Playback Finished");
        if (isPaused) {
            currentFrame += (int) ((double) evt.getFrame() * currentSong.getFrameRatePerMilliseconds());
        } else {
            if (pressedNext || pressedPrev)
                return;
            songFinished = true;
            if (nextQueue.isEmpty()) {
                musicPlayerGUI.enablePlayButtonDisablePauseButton();
            } else {
                nextSong();
            }
        }
    }
}