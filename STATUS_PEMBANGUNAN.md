# Status GeoExplorer Foundation 0.3.1

## Keputusan pengembangan
GeoExplorer dikembangkan sebagai aplikasi GIS Android native, menggunakan fondasi Kotlin yang sudah ada. Tidak dibuat ulang dari nol.

## Perbaikan konfigurasi, kode, & unit test yang telah diterapkan
- Root `build.gradle.kts` menambahkan deklarasi plugin `com.android.library` dan `org.jetbrains.kotlin.jvm` (`apply false`) untuk mendukung pemecahan modul GIS di tahap berikutnya.
- `settings.gradle.kts` mengaktifkan `TYPESAFE_PROJECT_ACCESSORS`.
- `gradle.properties` menambahkan `org.gradle.parallel=true`, `org.gradle.caching=true`, dan `android.nonTransitiveRClass=true`.
- `gradle/wrapper/gradle-wrapper.properties` menetapkan distribusi Gradle `8.9`.
- `app/build.gradle.kts` diperbarui dari `kotlinOptions` ke `kotlin.compilerOptions` (`JvmTarget.JVM_17`), menambahkan `buildTypes`, `buildFeatures` (`viewBinding`, `buildConfig`), aturan `packaging`, dependensi AndroidX (`core-ktx`, `appcompat`, `activity-ktx`, `lifecycle-runtime-ktx`), `androidTestImplementation`, serta dependensi MapLibre Native Android SDK `11.13.0`.
- `AndroidManifest.xml` menambahkan deklarasi XML, izin `INTERNET` dan `ACCESS_NETWORK_STATE` untuk MapLibre, serta `ACCESS_COARSE_LOCATION` / `ACCESS_FINE_LOCATION` untuk persiapan modul GPS & Survey.
- `styles.xml` memperbaiki `parent="android:style/Theme.Material.Light.NoActionBar"` menjadi `parent="Theme.AppCompat.Light.NoActionBar"` agar kompatibel dengan `AppCompatActivity` dan resource linking AAPT2.
- `MainActivity.kt` diperbarui ke `AppCompatActivity`, menambahkan penanganan `WindowInsetsCompat` untuk Android 15 (`targetSdk 35`), tombol Back via `OnBackPressedCallback`, pemulihan state layar, format koordinat `Locale.US`, serta integrasi `MapView` MapLibre melalui modul `core/map/` (`MapLibreConfig.kt` & `GeoMapController.kt`) dengan tetap mempertahankan seluruh modul `IndonesiaUtmZones` dan `ProjectDefinition`.
- Unit test (`IndonesiaUtmZonesTest.kt`, `ProjectDefinitionTest.kt`, dan `MapLibreConfigTest.kt`) dilengkapi kasus batas UTM, validasi metadata penuh, dan validasi konfigurasi awal MapLibre.
- `.gitignore` dilengkapi pola `.kotlin/`, `*.apk`, `*.aab`, `*.dex`, dan artefak build lainnya.

## Gerbang sebelum fitur GIS lanjutan
Pekerjaan fitur peta lanjutan belum dianggap terverifikasi sampai tiga pemeriksaan berikut berhasil pada mesin dengan JDK 17 + Android SDK 35 + Gradle 8.9:
1. Gradle Sync.
2. `:app:testDebugUnitTest`.
3. `:app:assembleDebug` dan aplikasi dapat dibuka pada emulator/perangkat.

## Hasil yang belum diverifikasi
- Build Android penuh, eksekusi `:app:testDebugUnitTest`, `:app:assembleDebug`, dan pengujian pada emulator belum dijalankan karena lingkungan audit ini tidak menyediakan JDK dan Android SDK.
- Berkas `gradlew`, `gradlew.bat`, dan `gradle-wrapper.jar` masih perlu dibuat secara lokal dengan `gradle wrapper --gradle-version 8.9`.
