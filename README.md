===========================================================
LEMBAR PENILAIAN KONTRIBUSI ANGGOTA KELOMPOK
===========================================================
Tema Proyek  : A3 Music Player
Kelompok     : Kelompok 7

1. Nama Anggota 1 : [Akmal Mustofa W]
   NIM            : [L0125094]
   Kontribusi     : [set UP library dan Pembuatan UI]

2. Nama Anggota 2 : [Ayu atika azhar]
   NIM            : [L0125127]
   Kontribusi     : [Pembuatan UI dan function untuk music]

3. Nama Anggota 3 : [Muh Aqiel Adhi R]
   NIM            : [L0125107]
   Kontribusi     : [Finnishing, pembenaran bug]
===========================================================

# MP3 Music Player - Kelompok 7 SDA

Aplikasi **MP3 Music Player** ini dibuat menggunakan Java (GUI dengan Java Swing) yang memungkinkan pengguna untuk memutar lagu-lagu berformat MP3, membuat *playlist*, dan mengontrol *playback* (Play, Pause, Next, Previous).

---

## Penjelasan Cara Kerja Program

Program ini dirancang menggunakan konsep pemrograman berorientasi objek (OOP) di Java dengan antarmuka grafis (GUI) menggunakan pustaka **Swing**. Berikut adalah penjelasan singkat mengenai cara kerjanya:

### 1. Antarmuka Pengguna (GUI)
- **`MusicPlayerGUI.java`**: Ini adalah kelas utama yang merender tampilan (*frame*) aplikasi. Di sini kita membuat berbagai tombol kontrol seperti *Play*, *Pause*, *Next*, *Previous*, serta sebuah *slider* untuk melacak durasi lagu yang sedang dimainkan. Kelas ini juga menyediakan menu **Catalog** yang berisi akses ke Katalog Musik (Tree), View Queue (FIFO), dan View History (LIFO).
- **`MusicPlaylistDialog.java`**: Ini adalah kelas yang menampilkan dialog *pop-up* bagi pengguna untuk membuat dan menyimpan *playlist*.
- **`MusicCatalogDialog.java`**: Dialog GUI yang menampilkan katalog musik dalam bentuk **JTree** (tampilan pohon hierarkis). Pengguna dapat menambah/menghapus lagu, memutar lagu terpilih, atau menambahkan lagu ke antrean putar langsung dari katalog.

### 2. Logika Pemutar Musik (Core Logic)
- **`MusicPlayer.java`**: Ini adalah "otak" di balik pemutar musik. Kelas ini memanggil fungsi-fungsi dari *library* eksternal untuk melakukan *decoding* dan memutar file biner MP3 ke *speaker* komputer. Kelas ini juga yang mengatur perhitungan *frame* saat ini sehingga pergeseran lagu melalui *slider* (maju/mundur) dapat dilakukan dengan mulus. Kelas ini mengelola **antrean putar (FIFO Queue)** dan **riwayat putar (LIFO Stack)**.
- **`Song.java`**: Kelas ini merepresentasikan struktur data tunggal untuk lagu. Kelas ini bertanggung jawab membaca *file* MP3, dan menggunakan pustaka pembaca *metadata* untuk mengekstrak informasi seperti Judul Lagu (*Title*), Nama Artis (*Artist*), Genre, dan Panjang/Durasi Lagu.

### 3. Struktur Data Katalog Musik Hierarkis (Tree)
- **`MusicCatalogTree.java`**: Kelas ini mengimplementasikan **struktur data N-ary Tree** untuk menyimpan katalog musik secara hierarkis (bertingkat). Strukturnya adalah:
  ```
  Root (Katalog Musik)
  ├── Genre: Pop
  │   ├── Artist: Taylor Swift
  │   │   ├── Song: Love Story.mp3
  │   │   └── Song: Shake It Off.mp3
  │   └── Artist: Ed Sheeran
  │       └── Song: Shape of You.mp3
  ├── Genre: Rock
  │   └── Artist: Queen
  │       └── Song: Bohemian Rhapsody.mp3
  └── Genre: Unknown
      └── Artist: Unknown
          └── Song: mysong.mp3
  ```
  Setiap **CatalogNode** menyimpan daftar anak (*children*) menggunakan `ArrayList`, sehingga membentuk pohon N-ary. Node bertipe SONG adalah *leaf node* (daun) yang menyimpan referensi ke objek `Song`.

### 4. Antrean Putar — FIFO Queue (Next in Queue)
- Menggunakan `Deque<Song> nextQueue` (implementasi `LinkedList`) dengan prinsip **First In, First Out (FIFO)**.
- Lagu yang pertama dimasukkan ke antrean akan diputar pertama kali.
- Operasi: `add()` untuk menambah ke akhir antrean, `poll()` untuk mengambil dari awal antrean.
- Pengguna dapat melihat isi antrean melalui menu **Catalog > View Queue (FIFO)** dan menghapus lagu dari antrean.

### 5. Riwayat Putar dan Undo/Back — LIFO Stack (History)
- Menggunakan `Stack<Song> historyStack` dengan prinsip **Last In, First Out (LIFO)**.
- Lagu yang terakhir diputar berada di puncak stack.
- Operasi: `push()` untuk menambah ke puncak stack saat berpindah lagu, `pop()` untuk mengambil dari puncak saat Undo/Back.
- Tombol **Previous** berfungsi sebagai operasi **Undo/Back** yang mengambil lagu dari puncak stack (LIFO pop).
- Pengguna dapat melihat riwayat melalui menu **Catalog > View History (LIFO)** dan melakukan Undo/Back dari dialog tersebut.

### 6. Pustaka Eksternal (Libraries)
Program ini tidak membaca *file* MP3 dari nol secara manual melainkan menggunakan beberapa *library* eksternal (yang terletak di folder `lib`):
- **JLayer (`jlayer-1.0.1.jar`)**: Digunakan untuk *decoding* dan memainkan file berformat MP3.
- **Mp3agic (`mp3agic-0.9.0.jar`) & JAudioTagger (`jaudiotagger-3.0.1.jar`)**: Digunakan untuk membaca metadata/ID3 Tag dari dalam file MP3 secara otomatis sehingga aplikasi tahu apa judul lagu, siapa penyanyinya, dan genre-nya.

### Cara Menjalankan Program (Kompilasi & Run)
Apabila tidak dijalankan melalui IDE (seperti VS Code atau IntelliJ), program dapat dijalankan dari *Command Prompt* / *Terminal* dengan memastikan agar *library* (.jar) dimuat dengan benar:
```bash
# Pindah ke direktori src
cd src

# Kompilasi seluruh file Java dan sambungkan dengan folder library
javac -cp ".;../lib/*" *.java

# Jalankan aplikasi
java -cp ".;../lib/*" App
```
*(Catatan: Karakter `;` pada `classpath` digunakan untuk sistem operasi Windows. Untuk Linux/Mac, gunakan `:`)*
