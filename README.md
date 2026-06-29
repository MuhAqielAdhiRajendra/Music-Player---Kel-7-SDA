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
- **`MusicPlayerGUI.java`**: Ini adalah kelas utama yang merender tampilan (*frame*) aplikasi. Di sini kita membuat berbagai tombol kontrol seperti *Play*, *Pause*, *Next*, *Previous*, serta sebuah *slider* untuk melacak durasi lagu yang sedang dimainkan.
- **`MusicPlaylistDialog.java`**: Ini adalah kelas yang menampilkan dialog *pop-up* bagi pengguna untuk membuat dan menyimpan *playlist*.

### 2. Logika Pemutar Musik (Core Logic)
- **`MusicPlayer.java`**: Ini adalah "otak" di balik pemutar musik.cd Kelas ini memanggil fungsi-fungsi dari *library* eksternal untuk melakukan *decoding* dan memutar file biner MP3 ke *speaker* komputer. Kelas ini juga yang mengatur perhitungan *frame* saat ini sehingga pergeseran lagu melalui *slider* (maju/mundur) dapat dilakukan dengan mulus.
- **`Song.java`**: Kelas ini merepresentasikan struktur data tunggal untuk lagu. Kelas ini bertanggung jawab membaca *file* MP3, dan menggunakan pustaka pembaca *metadata* untuk mengekstrak informasi seperti Judul Lagu (*Title*), Nama Artis (*Artist*), dan Panjang/Durasi Lagu.

### 3. Pustaka Eksternal (Libraries)
Program ini tidak membaca *file* MP3 dari nol secara manual melainkan menggunakan beberapa *library* eksternal (yang terletak di folder `lib`):
- **JLayer (`jlayer-1.0.1.jar`)**: Digunakan untuk *decoding* dan memainkan file berformat MP3.
- **Mp3agic (`mp3agic-0.9.0.jar`) & JAudioTagger (`jaudiotagger-3.0.1.jar`)**: Digunakan untuk membaca metadata/ID3 Tag dari dalam file MP3 secara otomatis sehingga aplikasi tahu apa judul lagu dan siapa penyanyinya.

### Cara Menjalankan Program (Kompilasi & Run)
Apabila tidak dijalankan melalui IDE (seperti VS Code atau IntelliJ), program dapat dijalankan dari *Command Prompt* / *Terminal* dengan memastikan agar *library* (.jar) dimuat dengan benar:
```bash
# Pindah ke direktori src
cd src

# Kompilasi seluruh file Java dan sambungkan dengan folder library
javac -cp ".;../lib/*" *.java

```
*(Catatan: Karakter `;` pada `classpath` digunakan untuk sistem operasi Windows. Untuk Linux/Mac, gunakan `:`)*
