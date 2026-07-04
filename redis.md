1. Bellek İçi (In-Memory) Önbelleklemenin Problemi 
Spring Boot varsayılan olarak önbellek verilerini uygulamanın kendi belleğinde (bir ConcurrentHashMap içerisinde) tutar. Ancak uygulamanızı yatayda ölçeklendirip birden fazla sunucuda (node) çalıştırdığınızda, her sunucunun kendi bağımsız belleği olur. Bir sunucudaki veri güncellendiğinde diğer sunucular bu durumdan haberdar olamaz ve kullanıcılara eski/hatalı (stale) veriyi sunmaya devam eder.

2. Çözüm: Redis ile Dağıtık Önbellekleme 
Bu tutarsızlık sorununu çözmek için uygulamanın dışında çalışan tek ve merkezi bir önbellek sunucusuna ihtiyaç vardır. Redis; verileri geleneksel veritabanları gibi diskte değil, doğrudan RAM üzerinde tutan (in-memory data store) oldukça popüler ve hızlı bir dağıtık önbellekleme çözümüdür. Tüm uygulama sunucuları bu ortak Redis noktasına bağlanarak veri tutarlılığını sağlar.

Ne Tür Veriler Önbelleğe Alınmalı? Ürün katalogları, kategoriler, OTP kodları gibi görece statik veriler önbelleğe alınmalıdır. Hesap bakiyesi veya stok miktarı gibi anlık değişen kritik veriler için önerilmez.

Redis, verileri diske yazmak yerine bellekte (RAM) saklayan, saniyede milyonlarca isteği işleyebilen Dağıtık (Distributed) Key-Value Store teknolojisidir.

```bash
docker exec -it demo_redis redis-cli
SET product:1 "Macbook Pro"
GET product:1
SET otp:user123 "492345" EX 30
GET otp:user123
TTL otp:user123
TTL otp:user123
```

mekanizması devreye alınıyor.

RedisCacheManager

@Cacheable (Veri Çekme - GET): * Ürün getirme metodunun üzerine ekleniyor.

Nasıl çalışıyor: Gelen istek önce Redis'e sorulur; veri Redis'te varsa anında dönülür (DB çalıştırılmaz). Eğer yoksa, DB'den veri çekilir, Redis'e yazılır ve ardından döner. (Buna Cache-Aside Pattern denir).

@CachePut (Veri Güncelleme - PUT/POST):

Bir ürünü güncelleyen metodun üzerine ekleniyor.

Nasıl çalışıyor: Veritabanı güncellendiğinde, kullanıcılara eski veri gitmemesi için arka planda o ürünün Redis'teki kaydı da ezilerek güncellenir.


## Redis Cache

Projede Redis cache icin `RedisConfig` sinifi bulunur. Cache mekanizmasi main uygulama sinifindaki `@EnableCaching` ile aktif edilir.

