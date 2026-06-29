import java.util.ArrayList;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 * Struktur data katalog musik yang bersifat hierarkis (bertingkat).
 * Menggunakan N-ary Tree dengan tingkatan: Root -> Genre -> Artist -> Song
 * 
 * Setiap node (CatalogNode) dapat memiliki banyak child node (children),
 * sehingga membentuk struktur pohon bertingkat.
 */
public class MusicCatalogTree {

    /**
     * Enum yang mendefinisikan tipe-tipe node dalam pohon katalog.
     */
    public enum NodeType {
        ROOT, // Akar pohon (Katalog Musik)
        GENRE, // Tingkat Genre (misal: Pop, Rock, Jazz)
        ARTIST, // Tingkat Artis (misal: Taylor Swift, Queen)
        SONG // Tingkat Lagu (leaf node / daun, berisi data Song)
    }

    /**
     * Inner class CatalogNode merepresentasikan satu simpul (node) dalam pohon.
     * Setiap node menyimpan:
     * - name: nama tampilan (genre, artist, atau judul lagu)
     * - nodeType: tipe node (ROOT, GENRE, ARTIST, SONG)
     * - children: daftar anak-anak node (ArrayList) -> membentuk N-ary tree
     * - song: referensi ke objek Song (hanya untuk leaf node bertipe SONG)
     */
    public static class CatalogNode {
        private String name;
        private NodeType nodeType;
        private ArrayList<CatalogNode> children; // N-ary tree: setiap node bisa punya banyak anak
        private Song song; // hanya diisi jika nodeType == SONG

        public CatalogNode(String name, NodeType nodeType) {
            this.name = name;
            this.nodeType = nodeType;
            this.children = new ArrayList<>();
            this.song = null;
        }

        public CatalogNode(String name, NodeType nodeType, Song song) {
            this(name, nodeType);
            this.song = song;
        }

        public String getName() {
            return name;
        }

        public NodeType getNodeType() {
            return nodeType;
        }

        public ArrayList<CatalogNode> getChildren() {
            return children;
        }

        public Song getSong() {
            return song;
        }

        /**
         * Menambahkan child node ke node ini.
         */
        public void addChild(CatalogNode child) {
            children.add(child);
        }

        /**
         * Menghapus child node dari node ini.
         */
        public void removeChild(CatalogNode child) {
            children.remove(child);
        }

        /**
         * Mencari child node berdasarkan nama (case-insensitive).
         * Digunakan untuk menemukan node Genre atau Artist yang sudah ada.
         */
        public CatalogNode findChildByName(String name) {
            for (CatalogNode child : children) {
                if (child.getName().equalsIgnoreCase(name)) {
                    return child;
                }
            }
            return null;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    // Root node dari pohon katalog
    private CatalogNode root;

    /**
     * Konstruktor: membuat pohon katalog kosong dengan root node.
     */
    public MusicCatalogTree() {
        root = new CatalogNode("Katalog Musik", NodeType.ROOT);
    }

    /**
     * Menambahkan lagu ke dalam pohon katalog.
     * Secara otomatis membuat node Genre dan Artist jika belum ada.
     * 
     * Alur: Root -> Genre (dari metadata) -> Artist (dari metadata) -> Song
     */
    public void addSong(Song song) {
        String genre = song.getSongGenre();
        String artist = song.getSongArtist();
        String title = song.getSongTitle();

        // Cari atau buat node Genre
        CatalogNode genreNode = root.findChildByName(genre);
        if (genreNode == null) {
            genreNode = new CatalogNode(genre, NodeType.GENRE);
            root.addChild(genreNode);
        }

        // Cari atau buat node Artist di bawah Genre
        CatalogNode artistNode = genreNode.findChildByName(artist);
        if (artistNode == null) {
            artistNode = new CatalogNode(artist, NodeType.ARTIST);
            genreNode.addChild(artistNode);
        }

        // Cek duplikat: jangan tambah lagu yang sudah ada
        for (CatalogNode child : artistNode.getChildren()) {
            if (child.getSong() != null && child.getSong().getFilePath().equals(song.getFilePath())) {
                return; // Lagu sudah ada, tidak perlu ditambahkan lagi
            }
        }

        // Tambahkan node Song sebagai leaf (daun) di bawah Artist
        CatalogNode songNode = new CatalogNode(title, NodeType.SONG, song);
        artistNode.addChild(songNode);
    }

    /**
     * Menghapus lagu dari pohon katalog berdasarkan file path.
     * Juga membersihkan node Artist dan Genre yang kosong setelah penghapusan.
     */
    public boolean removeSong(String filePath) {
        for (CatalogNode genreNode : new ArrayList<>(root.getChildren())) {
            for (CatalogNode artistNode : new ArrayList<>(genreNode.getChildren())) {
                for (CatalogNode songNode : new ArrayList<>(artistNode.getChildren())) {
                    if (songNode.getSong() != null && songNode.getSong().getFilePath().equals(filePath)) {
                        artistNode.removeChild(songNode);
                        // Hapus artist node jika tidak punya anak lagi
                        if (artistNode.getChildren().isEmpty()) {
                            genreNode.removeChild(artistNode);
                        }
                        // Hapus genre node jika tidak punya anak lagi
                        if (genreNode.getChildren().isEmpty()) {
                            root.removeChild(genreNode);
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Mengambil semua lagu dari pohon katalog (traversal DFS).
     * Melakukan penelusuran mendalam (depth-first) ke semua leaf node.
     */
    public List<Song> getAllSongs() {
        List<Song> songs = new ArrayList<>();
        collectSongs(root, songs);
        return songs;
    }

    /**
     * Helper method rekursif untuk mengumpulkan semua Song dari sub-tree.
     */
    private void collectSongs(CatalogNode node, List<Song> songs) {
        if (node.getNodeType() == NodeType.SONG && node.getSong() != null) {
            songs.add(node.getSong());
        }
        for (CatalogNode child : node.getChildren()) {
            collectSongs(child, songs);
        }
    }

    /**
     * Mengambil semua lagu berdasarkan genre.
     */
    public List<Song> getSongsByGenre(String genre) {
        List<Song> songs = new ArrayList<>();
        CatalogNode genreNode = root.findChildByName(genre);
        if (genreNode != null) {
            collectSongs(genreNode, songs);
        }
        return songs;
    }

    /**
     * Mengambil semua lagu berdasarkan artis.
     */
    public List<Song> getSongsByArtist(String artist) {
        List<Song> songs = new ArrayList<>();
        for (CatalogNode genreNode : root.getChildren()) {
            CatalogNode artistNode = genreNode.findChildByName(artist);
            if (artistNode != null) {
                collectSongs(artistNode, songs);
            }
        }
        return songs;
    }

    /**
     * Mengkonversi pohon katalog menjadi DefaultTreeModel untuk ditampilkan di
     * JTree (Swing).
     * Ini adalah jembatan antara struktur data internal dan komponen GUI.
     */
    public DefaultTreeModel toJTreeModel() {
        DefaultMutableTreeNode jtreeRoot = convertToJTreeNode(root);
        return new DefaultTreeModel(jtreeRoot);
    }

    /**
     * Helper rekursif: mengkonversi CatalogNode menjadi DefaultMutableTreeNode.
     */
    private DefaultMutableTreeNode convertToJTreeNode(CatalogNode node) {
        DefaultMutableTreeNode jtreeNode = new DefaultMutableTreeNode(node);
        for (CatalogNode child : node.getChildren()) {
            jtreeNode.add(convertToJTreeNode(child));
        }
        return jtreeNode;
    }

    /**
     * Getter untuk root node.
     */
    public CatalogNode getRoot() {
        return root;
    }

    /**
     * Mengecek apakah katalog kosong (tidak ada lagu).
     */
    public boolean isEmpty() {
        return root.getChildren().isEmpty();
    }

    /**
     * Menghitung total jumlah lagu dalam katalog.
     */
    public int getSongCount() {
        return getAllSongs().size();
    }
}
