# 🧠 Core Intelligence & Global Development Standards

**Mission Statement:**
Aplikasi ini dibangun dengan tujuan menjadi produk berskala global yang bermanfaat bagi pengguna di seluruh dunia. Setiap baris kode yang ditulis harus memenuhi standar Enterprise dan memastikan pengalaman pengguna (UX) yang mulus, aman, dan responsif.

**Development Rules (Always Active):**

1. **Enterprise-Grade Architecture:** 
   - Selalu gunakan **Clean Architecture** dan pola **MVVM (Model-View-ViewModel)**.
   - Pisahkan logika bisnis (Domain/Data) dari antarmuka pengguna (UI/Presentation).

2. **Modern UI & UX (Jetpack Compose):**
   - Gunakan **Jetpack Compose** untuk seluruh antarmuka. 
   - Pastikan desain bersifat **Adaptive & Responsive** (mendukung berbagai ukuran layar mulai dari ponsel kecil hingga tablet).
   - Terapkan standar **Material Design 3 (M3)** dengan perpaduan warna dan tipografi yang elegan.

3. **Maximized Performance & State Management:**
   - Gunakan `StateFlow` dan `collectAsStateWithLifecycle()` untuk manajemen *state* UI.
   - Cegah *memory leak* secara ketat.
   - Gunakan fungsi `remember` dan `derivedStateOf` untuk meminimalkan *recomposition* (render ulang) yang tidak perlu di Jetpack Compose.

4. **Robust Error Handling & Resilience:**
   - Aplikasi tidak boleh mengalami *force close*.
   - Setiap operasi asinkron (Network/Database) harus menggunakan `Kotlin Coroutines` dengan penanganan blok `try-catch` yang aman, dan memberikan status (Loading, Success, Error) ke UI.

5. **Global Usability:**
   - Siapkan fondasi aplikasi yang mudah diskalakan.
   - Tulis kode modular agar fitur-fitur kompleks (seperti Lokasi/Nearby, Chat Real-time, Feed, dan Profil) dapat diintegrasikan dengan mulus.
