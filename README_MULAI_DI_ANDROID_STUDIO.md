# GeoExplorer Android — Foundation 0.3.1

Paket ini melanjutkan fondasi proyek GeoExplorer 0.3.0. Ini adalah aplikasi Android native berbasis Kotlin untuk dikembangkan bertahap menuju aplikasi GIS lapangan yang lebih lengkap. Paket ini **belum** setara QGIS dan belum memuat mesin analisis GIS penuh.

## Konfigurasi proyek

- Android application module: `app`
- Namespace/application ID: `id.geoexplorer.app`
- Android Gradle Plugin: `8.7.3`
- Kotlin Android plugin: `2.0.21`
- Gradle distribution yang ditetapkan: `8.9`
- Java/Kotlin target: `17` (`compilerOptions.jvmTarget = JvmTarget.JVM_17`)
- `minSdk 26`, `compileSdk 35`, `targetSdk 35`
- `versionCode 4`, `versionName 0.3.1-foundation`
- AndroidX: `core-ktx 1.15.0`, `appcompat 1.7.0`, `activity-ktx 1.9.3`, `lifecycle-runtime-ktx 2.8.7`
- Persiapan mesin peta utama: `org.maplibre.gl:android-sdk:11.13.0` (tersedia pula opsi `android-sdk-opengl:11.13.0` bila emulator mengalami kendala Vulkan)

## Yang sudah ada

- Aktivitas utama Android dengan menu modul awal.
- Halaman pratinjau peta (disiapkan untuk integrasi MapLibre).
- Pemilih zona UTM Indonesia 46–54.
- Pemilihan EPSG WGS 84 / UTM utara dan selatan.
- Validasi rentang lintang UTM standar 80°LS–84°LU.
- Model metadata proyek dengan validasi dasar.
- Unit test JUnit untuk zona UTM dan metadata proyek.

## Cara membuka di Android Studio

1. Pastikan seluruh berkas pada `DAFTAR_FILE.txt` (termasuk folder `app/src/` dan `gradle/wrapper/gradle-wrapper.properties`) berada dalam satu folder proyek `GeoExplorer_Android_Foundation_0_3_1`.
2. Android Studio → **Open** → pilih folder proyek tersebut.
3. Gunakan JDK 17.
4. Pastikan Android SDK Platform 35 terpasang melalui SDK Manager.
5. Tunggu Gradle Sync. Jika diminta, izinkan Android Studio mengunduh Gradle 8.9 dan dependensi dari repositori resmi (`google()` dan `mavenCentral()`).
6. Pilih konfigurasi `app`, lalu jalankan pada emulator atau perangkat Android API 26+.

## Catatan Gradle Wrapper yang penting

Paket ini memiliki `gradle/wrapper/gradle-wrapper.properties`, tetapi belum memiliki `gradlew`, `gradlew.bat`, dan `gradle-wrapper.jar`. Artinya, perintah `./gradlew` belum dapat digunakan langsung. Jangan menjalankan wrapper sebelum berkas tersebut dibuat.

Jika Gradle 8.9 sudah tersedia di komputer, buka terminal pada folder proyek dan jalankan:

```bash
gradle wrapper --gradle-version 8.9
```

Perintah tersebut akan membuat launcher dan wrapper JAR. Setelah itu, gunakan:

```bash
./gradlew :app:testDebugUnitTest
./gradlew :app:assembleDebug
```

Pada Windows gunakan `gradlew.bat` sebagai pengganti `./gradlew`.

## Batasan saat ini

- Kode UI peta (`MainActivity.kt` / komponen MapView) dan `AndroidManifest.xml` (izin `INTERNET` / `ACCESS_FINE_LOCATION`) masih perlu diselaraskan begitu berkas sumber di `app/src/` diaudit.
- Belum ada izin dan layanan GPS/GNSS.
- Belum ada impor/ekspor SHP, KML/KMZ, GPX, GeoJSON, GeoPackage, GeoTIFF, atau ECW.
- Belum ada transformasi CRS PROJ. Fungsi UTM hanya memilih kode EPSG, bukan mentransformasi koordinat.
- Belum ada digitasi, analisis spasial, database proyek persisten, atau ekspor layout PDF.

## Urutan pekerjaan

1. Buktikan Gradle Sync, unit test, dan debug APK berhasil di Android Studio / mesin lokal dengan JDK 17 & Android SDK 35.
2. Uji aplikasi pada emulator/perangkat Android.
3. Aktifkan `MapView` MapLibre pada layar peta dan tambahkan izin jaringan di `AndroidManifest.xml`.
4. Tambahkan layer manager dan penyimpanan proyek.
5. Tambahkan data vektor, transformasi CRS, GPS, dan dukungan raster secara bertahap.

## Contoh zona UTM

- `(-5.1, 119.0)` → EPSG:32750.
- `(-4.0, 135.0)` → EPSG:32753.
- `(1.0, 103.0)` → EPSG:32648.

Pemilihan zona di dalam starter dibatasi pada longitude 90°–144° dan tidak menggantikan pemeriksaan CRS sumber data. Data SRGI 2013, TM-3, koordinat lintas zona, dan kebutuhan transformasi datum/epoch memerlukan penanganan CRS yang benar.
