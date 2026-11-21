# Hulk Mod - NeoForge 1.21.1

Minecraft için Hulk dönüşüm modu. İksir içerek 5 dakika boyunca yeşil kambur dev Hulk'a dönüşün!

## Özellikler

### Dönüşüm
- **Hulk İksiri** içerek aktif edilir
- **5 dakika (6000 tick)** sürer
- Yeşil cilt, kambur duruş, 2.5x boyut artışı
- GeckoLib ile tam animasyonlu model

### Yetenekler (Sadece Hulk formunda)
| Tuş | Yetenek | Açıklama |
|-----|---------|----------|
| **Çift Space** | Süper Zıplama | 2.5 blok yüksekliğine zıplar |
| **Shift + Sağ Tık** | Yer Çarpma | 5 blok yarıçapında 20 hasar + geri itme |
| **Çift W** | Hızlanma (Dash) | Öne doğru 2 blok anlık ileri atlar |
| **R** | Kükreme | 10 blok yarıçapında mob'ları korkutur/iter |

### İstatistik Değişiklikleri
- **Saldırı Hasarı**: +40 (tek vuruşla çoğu mob ölür)
- **Maksimum Sağlık**: +100 (50 kalp)
- **Geri İtilme Direnci**: %100 (hiç itilmez)
- **Hareket Hızı**: +%30
- **Kazma Hızı**: +10 (el ile blok kırma)

### Crafting Recepti (Standart)
```
G N G
N B N
G N G
```
- G = Emerald (Zümrüt)
- N = Nether Star (Nether Yıldızı)
- B = Beacon (Işınlanma)

### Crafting Recepti (Kolay)
```
E D E
D G D
E D E
```
- E = Emerald Block (Zümrüt Bloku)
- D = Diamond Block (Elmas Bloku)
- G = Golden Apple (Altın Elma)

## Kurulum & Derleme

### Gereksinimler
- **Java 21** (JDK 21)
- **Gradle** (wrapper dahil)

### Derleme Adımları

```bash
# Proje klasörüne gir
cd C:\Users\ASUS\Desktop\HulkMod

# Gradle wrapper izin ver (Linux/Mac)
chmod +x gradlew

# Derle (Windows)
.\gradlew.bat build

# Derle (Linux/Mac)
./gradlew build
```

### Çıktı
Derleme başarılı olursa `build/libs/` klasöründe şu dosya oluşur:
```
HulkMod-1.0.0.jar
```

Bu dosyayı Minecraft `mods` klasörüne atın.

## Geliştirme Ortamı Kurulumu

### IntelliJ IDEA
1. `File > Open` → `build.gradle.kts` seçin
2. "Open as Project" deyin
3. Gradle sync bekleyin
4. `Run > Edit Configurations` → `+` → `Gradle` → `runClient` seçin

### VS Code
1. Extension Pack for Java kurun
2. Gradle for Java kurun
3. Klasörü açın, `Terminal > Run Task > gradle: runClient`

## Yapılandırma

`config/hulkmod-common.toml` dosyası ile ayarları değiştirebilirsiniz:

```toml
[general]
transformationDuration = 6000      # Dönüşüm süresi (tick)
hulkScale = 2.5                    # Model ölçeği
attackDamageBonus = 40.0           # Ekstra hasar
maxHealthBonus = 100.0             # Ekstra sağlık
knockbackResistance = 1.0          # Geri itilme direnci (0-1)
movementSpeedBonus = 0.3           # Hız bonusu
miningSpeedBonus = 10.0            # Kazma hızı bonusu
enableParticles = true             # Parçacık efektleri
enableSounds = true                # Ses efektleri
```

## Texture & Model Değiştirme

Mod şu dosyaları kullanır (kendi sanatlarınızla değiştirin):
- `assets/hulkmod/textures/item/hulk_potion.png` - İksir ikonu
- `assets/hulkmod/textures/entity/hulk/hulk.png` - Hulk model dokusu (64x64 veya 128x128)
- `assets/hulkmod/textures/entity/hulk/hulk_eyes.png` - Göz ışığı doku
- `assets/hulkmod/geo/hulk.geo.json` - GeckoLib model tanımı
- `assets/hulkmod/animations/hulk.animation.json` - Animasyonlar

## Lisans

MIT License - Serbest kullanım, değiştirme, dağıtım.

## Katkıda Bulunma

1. Fork yapın
2. Feature branch oluşturun (`git checkout -b feature/yeni-yetenek`)
3. Commit atın (`git commit -am 'Yeni yetenek eklendi'`)
4. Pushlayın (`git push origin feature/yeni-yetenek`)
5. Pull Request açın

## Sorun Bildirimi

GitHub Issues üzerinden hata raporlayın. Log dosyalarını (`logs/latest.log`) eklemeyi unutmayın.

---

**Not**: Bu mod GeckoLib kütüphanesini kullanır. Modpaketlerinize GeckoLib'i de eklemeyi unutmayın.