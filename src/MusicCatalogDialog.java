import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/**
 * Dialog GUI untuk menampilkan katalog musik dalam bentuk JTree (pohon
 * hierarkis).
 * Pengguna dapat:
 * - Melihat semua lagu yang diorganisir berdasarkan Genre -> Artist -> Song
 * - Menambah lagu baru ke katalog
 * - Menghapus lagu dari katalog
 * - Memutar lagu yang dipilih
 * - Menambah lagu ke antrean putar (Queue)
 */
public class MusicCatalogDialog extends JDialog {
    private MusicPlayerGUI musicPlayerGUI;
    private MusicCatalogTree catalogTree;
    private MusicPlayer musicPlayer;
    private JTree jTree;

    /**
     * Inner class untuk menyimpan data node katalog (untuk ditampilkan di JTree).
     */
    static class CatalogNodeData {
        private MusicCatalogTree.CatalogNode catalogNode;

        public CatalogNodeData(MusicCatalogTree.CatalogNode catalogNode) {
            this.catalogNode = catalogNode;
        }

        public MusicCatalogTree.CatalogNode getCatalogNode() {
            return catalogNode;
        }

        @Override
        public String toString() {
            String prefix = "";
            switch (catalogNode.getNodeType()) {
                case ROOT:
                    prefix = "\uD83C\uDFB5 "; // 🎵
                    break;
                case GENRE:
                    prefix = "\uD83D\uDCC2 "; // 📂
                    break;
                case ARTIST:
                    prefix = "\uD83C\uDFA4 "; // 🎤
                    break;
                case SONG:
                    prefix = "\u266A "; // ♪
                    break;
            }
            return prefix + catalogNode.getName();
        }
    }

    public MusicCatalogDialog(MusicPlayerGUI musicPlayerGUI, MusicCatalogTree catalogTree, MusicPlayer musicPlayer) {
        this.musicPlayerGUI = musicPlayerGUI;
        this.catalogTree = catalogTree;
        this.musicPlayer = musicPlayer;

        // Configure dialog
        setTitle("Katalog Musik (Hierarchical Tree)");
        setSize(450, 500);
        setResizable(false);
        getContentPane().setBackground(MusicPlayerGUI.FRAME_COLOR);
        setLayout(new BorderLayout());
        setModal(true);
        setLocationRelativeTo(musicPlayerGUI);

        addDialogComponents();
    }

    private void addDialogComponents() {
        // === Header Label ===
        JLabel headerLabel = new JLabel("Katalog Musik - Struktur Hierarkis (Tree)");
        headerLabel.setFont(new Font("Dialog", Font.BOLD, 14));
        headerLabel.setForeground(MusicPlayerGUI.TEXT_COLOR);
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        headerLabel.setOpaque(true);
        headerLabel.setBackground(MusicPlayerGUI.FRAME_COLOR);
        add(headerLabel, BorderLayout.NORTH);

        // === JTree Panel ===
        refreshTree();
        JScrollPane scrollPane = new JScrollPane(jTree);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        scrollPane.getViewport().setBackground(Color.DARK_GRAY);
        add(scrollPane, BorderLayout.CENTER);

        // === Button Panel ===
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(MusicPlayerGUI.FRAME_COLOR);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 8, 8));

        // Tombol Tambah Lagu
        JButton addButton = new JButton("Tambah Lagu");
        addButton.setFont(new Font("Dialog", Font.BOLD, 12));
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.setFileFilter(new FileNameExtensionFilter("MP3", "mp3"));
                jFileChooser.setCurrentDirectory(new File("src/assets"));
                jFileChooser.setMultiSelectionEnabled(true);
                int result = jFileChooser.showOpenDialog(MusicCatalogDialog.this);

                if (result == JFileChooser.APPROVE_OPTION) {
                    File[] selectedFiles = jFileChooser.getSelectedFiles();
                    for (File file : selectedFiles) {
                        Song song = new Song(file.getPath());
                        catalogTree.addSong(song);
                    }
                    refreshTree();
                    revalidate();
                    repaint();
                }
            }
        });
        buttonPanel.add(addButton);

        // Tombol Hapus Lagu
        JButton removeButton = new JButton("Hapus");
        removeButton.setFont(new Font("Dialog", Font.BOLD, 12));
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TreePath selectedPath = jTree.getSelectionPath();
                if (selectedPath == null) {
                    JOptionPane.showMessageDialog(MusicCatalogDialog.this, "Pilih lagu yang ingin dihapus!");
                    return;
                }
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();
                Object userObj = selectedNode.getUserObject();
                if (userObj instanceof CatalogNodeData) {
                    MusicCatalogTree.CatalogNode catNode = ((CatalogNodeData) userObj).getCatalogNode();
                    if (catNode.getNodeType() == MusicCatalogTree.NodeType.SONG && catNode.getSong() != null) {
                        catalogTree.removeSong(catNode.getSong().getFilePath());
                        refreshTree();
                        revalidate();
                        repaint();
                    } else {
                        JOptionPane.showMessageDialog(MusicCatalogDialog.this,
                                "Hanya lagu (leaf node) yang dapat dihapus!");
                    }
                }
            }
        });
        buttonPanel.add(removeButton);

        // Tombol Play
        JButton playButton = new JButton("Play");
        playButton.setFont(new Font("Dialog", Font.BOLD, 12));
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Song selectedSong = getSelectedSong();
                if (selectedSong == null) {
                    JOptionPane.showMessageDialog(MusicCatalogDialog.this, "Pilih lagu yang ingin dimainkan!");
                    return;
                }
                musicPlayer.loadSong(selectedSong);
                musicPlayerGUI.updateSongTitleAndArtist(selectedSong);
                musicPlayerGUI.updatePlaybackSlider(selectedSong);
                musicPlayerGUI.enablePauseButtonDisablePlayButton();
                dispose();
            }
        });
        buttonPanel.add(playButton);

        // Tombol Add to Queue
        JButton queueButton = new JButton("Add to Queue");
        queueButton.setFont(new Font("Dialog", Font.BOLD, 12));
        queueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Song selectedSong = getSelectedSong();
                if (selectedSong == null) {
                    JOptionPane.showMessageDialog(MusicCatalogDialog.this,
                            "Pilih lagu yang ingin ditambahkan ke antrean!");
                    return;
                }
                musicPlayer.addToQueue(selectedSong);
                JOptionPane.showMessageDialog(MusicCatalogDialog.this,
                        "\"" + selectedSong.getSongTitle() + "\" ditambahkan ke antrean putar.");
            }
        });
        buttonPanel.add(queueButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Mengambil lagu yang dipilih dari JTree (hanya jika node bertipe SONG).
     */
    private Song getSelectedSong() {
        TreePath selectedPath = jTree.getSelectionPath();
        if (selectedPath == null)
            return null;
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();
        Object userObj = selectedNode.getUserObject();
        if (userObj instanceof CatalogNodeData) {
            MusicCatalogTree.CatalogNode catNode = ((CatalogNodeData) userObj).getCatalogNode();
            if (catNode.getNodeType() == MusicCatalogTree.NodeType.SONG) {
                return catNode.getSong();
            }
        }
        return null;
    }

    /**
     * Memperbarui tampilan JTree dari data MusicCatalogTree.
     * Mengkonversi CatalogNode tree ke DefaultMutableTreeNode tree untuk JTree
     * Swing.
     */
    private void refreshTree() {
        DefaultMutableTreeNode jtreeRoot = buildJTreeNode(catalogTree.getRoot());
        if (jTree == null) {
            jTree = new JTree(new DefaultTreeModel(jtreeRoot));
            jTree.setBackground(Color.DARK_GRAY);
            jTree.setForeground(MusicPlayerGUI.TEXT_COLOR);
            jTree.setFont(new Font("Dialog", Font.PLAIN, 14));
        } else {
            jTree.setModel(new DefaultTreeModel(jtreeRoot));
        }
        // Expand all nodes
        for (int i = 0; i < jTree.getRowCount(); i++) {
            jTree.expandRow(i);
        }
    }

    /**
     * Helper rekursif: konversi CatalogNode ke DefaultMutableTreeNode
     * dengan membungkus data menggunakan CatalogNodeData.
     */
    private DefaultMutableTreeNode buildJTreeNode(MusicCatalogTree.CatalogNode catalogNode) {
        DefaultMutableTreeNode jtreeNode = new DefaultMutableTreeNode(new CatalogNodeData(catalogNode));
        for (MusicCatalogTree.CatalogNode child : catalogNode.getChildren()) {
            jtreeNode.add(buildJTreeNode(child));
        }
        return jtreeNode;
    }
}
