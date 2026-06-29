# MP3 Music Player - Kelompok 7 SDA

Aplikasi **MP3 Music Player** ini dibuat menggunakan Java (GUI dengan Java Swing) yang memungkinkan pengguna untuk memutar lagu-lagu berformat MP3, membuat *playlist*, dan mengontrol *playback* musik. Aplikasi ini mengimplementasikan konsep Struktur Data dan Algoritma (SDA) untuk menghadirkan fitur manajemen katalog, antrean, dan riwayat putar musik yang canggih.

## Nama Anggota Kelompok
Tema Proyek : A3 Music Player
Kelompok : Kelompok 7

Nama Anggota 1 : [Akmal Mustofa W] NIM : [L0125094] 
Kontribusi : [set UP library dan Pembuatan UI]

Nama Anggota 2 : [Ayu atika azhar] NIM : [L0125127] 
Kontribusi : [Pembuatan UI dan function untuk music]

Nama Anggota 3 : [Muh Aqiel Adhi R] NIM : [L0125107] 
Kontribusi : [Finnishing, pembenaran bug]

## Fitur-Fitur Utama Program

1. **Playback Control**: Kontrol dasar pemutar musik (Play, Pause, Next, Previous).
2. **Dynamic Time Slider**: Menggeser *slider* untuk maju/mundur pada lagu yang sedang diputar beserta informasi waktu (*elapsed time*).
3. **Music Catalog (Hierarchical Tree)**: Melihat koleksi seluruh lagu yang dimuat ke dalam aplikasi dalam bentuk struktur pohon (dikategorikan berdasarkan *Genre* -> *Artist* -> *Song*). Pengguna dapat menambah, menghapus, memutar, dan menambahkan lagu ke antrean dari katalog ini.
4. **View Queue (FIFO)**: Menampilkan antrean putar lagu selanjutnya. Lagu dapat ditambahkan ke antrean dari katalog dan dapat dihapus dari daftar antrean.
5. **View History (LIFO)**: Menampilkan riwayat lagu yang telah diputar dari yang terbaru hingga yang terlama. Dilengkapi tombol "Undo/Back" untuk kembali ke lagu sebelumnya.
6. **Playlist Management**: Membuat *playlist* baru serta memuat (load) *playlist* dari file `.txt`.

## Struktur Data dan Algoritma yang Digunakan

1. **N-ary Tree (Pohon Bertingkat Banyak)**
   - **Kegunaan**: Menyimpan data Katalog Musik (`MusicCatalogTree.java`).
   - **Alasan Pemilihan**: *Tree* sangat cocok dan efisien untuk merepresentasikan data yang memiliki tingkatan secara logis. Katalog direpresentasikan dengan struktur: Root -> Genre -> Artist -> Song (Leaf), sehingga memudahkan pengelompokan lagu.
2. **Queue (Antrean - LinkedList)**
   - **Kegunaan**: Menyimpan daftar putar lagu selanjutnya (*Next in Queue*).
   - **Alasan Pemilihan**: Menggunakan prinsip **FIFO** (*First In, First Out*). Secara logis, lagu yang pertama kali dimasukkan oleh pengguna ke dalam antrean haruslah lagu yang pertama kali dimainkan ketika lagu saat ini selesai. Implementasi LinkedList digunakan karena sangat efisien dalam menempatkan elemen ke akhir antrean (enqueue) dan mengambil elemen dari awal antrean (dequeue).
3. **Stack (Tumpukan)**
   - **Kegunaan**: Menyimpan riwayat putar lagu (*History*) dan fitur mundur (*Undo/Previous*).
   - **Alasan Pemilihan**: Menggunakan prinsip **LIFO** (*Last In, First Out*). Saat pengguna menekan "Previous", ia ingin kembali ke lagu *terakhir* yang baru saja didengarkannya. Stack adalah struktur data mutlak dan paling efisien untuk mengimplementasikan fitur riwayat atau *undo* semacam ini.
4. **Depth-First Search (DFS)**
   - **Kegunaan**: Traversal (penelusuran) pada N-ary Tree untuk mengumpulkan daftar semua lagu.
   - **Alasan Pemilihan**: DFS efisien dalam menelusuri setiap cabang kategori secara mendalam sampai ke ujung (*leaf* / lagunya) sebelum berpindah ke kategori (genre/artist) sebelahnya.

## Panduan Instalasi dan Menjalankan Program

### Prasyarat
- Telah menginstal **Java Development Kit (JDK)** di sistem (Versi 8 atau di atasnya).
- *Clone* repositori proyek ini atau unduh *source code* secara lengkap.

### Cara Menjalankan Aplikasi
Bagi pengguna **Windows**, Anda cukup mengklik ganda (*double-click*) pada file **`run.bat`** yang ada di direktori utama proyek.

Jika ingin menjalankan secara manual via **Command Prompt** atau **Terminal**:
1. Buka Terminal/CMD dan arahkan *directory* ke folder utama proyek ini.
2. Kompilasi program dan *library*:
   ```bash
   javac -cp "lib/*" -d bin src/*.java
   ```
   *(Gunakan karakter `:` sebagai pemisah di Linux/Mac OSX, contoh: `lib/*` menjadi `"lib/*"` biasanya sama, namun perhatikan OS-nya)*
3. Jalankan aplikasi:
   ```bash
   java -cp "bin;lib/*" App
   ```
   *(Gunakan karakter `:` sebagai pengganti `;` untuk sistem operasi Linux/Mac)*

## Library Eksternal yang Digunakan

Proyek ini tidak membaca sinyal biner MP3 secara manual, melainkan memanfaatkan pustaka eksternal yang ada pada direktori `lib/`.

1. **JLayer**
   - **Versi**: 1.0.1 (`jlayer-1.0.1.jar`)
   - **Fungsi/Kegunaan**: Digunakan sebagai *engine* utama untuk memproses proses dekode (*decoding*) dan memutar file biner MP3 ke perangkat *speaker* komputer.
   - **Instalasi/Konfigurasi**: File jar diletakkan di dalam folder `lib/`. Untuk menggunakannya, path ini disertakan ke dalam *classpath* Java saat kompilasi (`javac -cp "lib/*" ...`) dan eksekusi (`java -cp "bin;lib/*" ...`).
   
2. **Mp3agic**
   - **Versi**: 0.9.0 (`mp3agic-0.9.0.jar`)
   - **Fungsi/Kegunaan**: Digunakan secara paralel untuk menganalisis durasi lagu dan jumlah *frame rate* per milidetik. Ini sangat vital untuk membuat visualisasi *slider* durasi lagu yang akurat.
   - **Instalasi/Konfigurasi**: Sama seperti library lainnya, letakkan di `lib/` lalu hubungkan *classpath* pada parameter `-cp` di CMD/Terminal.
   
3. **JAudioTagger**
   - **Versi**: 3.0.1 (`jaudiotagger-3.0.1.jar`)
   - **Fungsi/Kegunaan**: Secara khusus digunakan untuk mengekstrak informasi detail/metadata (ID3 Tag) dari dalam file `.mp3` secara otomatis, seperti nama **Judul Lagu (Title)**, **Nama Artis (Artist)**, dan **Genre** dari lagu tersebut (yang menjadi dasar pembentukan struktur *Tree* katalog musik).
   - **Instalasi/Konfigurasi**: Disertakan dalam folder `lib/` dan dirujuk menggunakan *classpath* saat aplikasi dibangun dan berjalan.
