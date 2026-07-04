# Kutuphane Otomasyon Spring Boot

Spring Boot ile gelistirilmis kutuphane otomasyon API projesidir. Projede kitap, yazar, yayinci, kategori, odunc alma ve kullanici islemleri icin REST endpointleri bulunur. PostgreSQL veritabani, Redis cache, JWT tabanli kimlik dogrulama ve Swagger UI destegi kullanilir.

## Kullanilan Teknolojiler

- Java 17
- Spring Boot 4.0.6
- Spring Web MVC
- Spring Data JPA
- Spring Security
- JWT
- PostgreSQL
- Redis
- Spring Cache
- Lombok
- MapStruct
- ModelMapper
- Springdoc OpenAPI / Swagger UI
- Docker Compose

## Gereksinimler

- Java 17
- Maven veya proje icindeki Maven Wrapper
- Docker Desktop

## Docker Servisleri

Projedeki `docker-compose.yml` dosyasi PostgreSQL, pgAdmin ve Redis servislerini calistirir.

```bash
docker compose up -d
```

Servisler:

| Servis | Port | Bilgi |
| --- | --- | --- |
| PostgreSQL | `5432` | Kullanici: `postgres`, sifre: `gizlisifrem` |
| pgAdmin | `5050` | Email: `admin@admin.com`, sifre: `admin` |
| Redis | `6379` | Cache servisi |

pgAdmin adresi:

```text
http://localhost:5050
```

## Uygulama Ayarlari

Temel ayarlar `src/main/resources/application.properties` dosyasindadir.

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=gizlisifrem

spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.cache.type=redis
```

Hibernate varsayilan schema olarak `kutuphaneotomasyon` kullanir ve `ddl-auto=update` ile tablolar otomatik guncellenir.

## Projeyi Calistirma

Once Docker servislerini baslatin:

```bash
docker compose up -d
```

Ardindan uygulamayi calistirin:

```bash
./mvnw spring-boot:run
```

Windows icin:

```bash
mvnw.cmd spring-boot:run
```

Uygulama varsayilan olarak su adreste calisir:

```text
http://localhost:8080
```

## Swagger UI

API dokumantasyonuna su adresten ulasilabilir:

```text
http://localhost:8080/swagger-ui/index.html
```

## Redis Cache

Projede Redis cache icin `RedisConfig` sinifi bulunur. Cache mekanizmasi main uygulama sinifindaki `@EnableCaching` ile aktif edilir.

Kullanilan cache anotasyonlari:

- `@Cacheable`: Veri okunurken once Redis kontrol edilir. Veri varsa database calismadan cache'den doner.
- `@CachePut`: Veri guncellendiginde database islemi sonrasi Redis'teki cache kaydini da gunceller.
- `@CacheEvict`: Veri silindiginde Redis'teki cache kaydini da siler.

Kitap cache'i `books` cache adi ile tutulur.

Redis cache temizleme:

```bash
docker exec -it redis-cache redis-cli FLUSHALL
```

Redis CLI'a baglanma:

```bash
docker exec -it redis-cache redis-cli
```

## Kimlik Dogrulama Endpointleri

| Method | Endpoint | Aciklama |
| --- | --- | --- |
| POST | `/register` | Yeni kullanici kaydi |
| POST | `/authenticate` | Giris yapma, access token ve refresh token alma |
| POST | `/refreshToken` | Refresh token ile yeni access token alma |

## Kitap Endpointleri

Base path:

```text
/rest/api/Book
```

| Method | Endpoint | Aciklama |
| --- | --- | --- |
| POST | `/save` | Kitap ekleme |
| PUT | `/update/{id}` | Kitap guncelleme |
| DELETE | `/delete/{id}` | Kitap silme |
| GET | `/listAll` | Tum kitaplari listeleme |
| GET | `/list/{id}` | ID ile kitap getirme |
| GET | `/search?keyword=...` | Kitap adina gore arama |
| GET | `/system/status` | Sistem kitap durum ozeti |

Kitap ekleme ve guncelleme endpointleri JSON body ile `DtoBookIU` alanlarini bekler.

## Yazar Endpointleri

Base path:

```text
/rest/api/Author
```

| Method | Endpoint | Aciklama |
| --- | --- | --- |
| POST | `/save` | Yazar ekleme |
| GET | `/listAllAuthors` | Tum yazarlari listeleme |
| GET | `/list/{id}` | ID ile yazar getirme |
| PUT | `/update/{id}` | Yazar guncelleme |
| DELETE | `/delete/{id}` | Yazar silme |
| GET | `/{authorId}/books` | Yazara ait kitaplari listeleme |

## Kategori Endpointleri

Base path:

```text
/rest/api/Category
```

| Method | Endpoint | Aciklama |
| --- | --- | --- |
| POST | `/save` | Kategori ekleme |
| GET | `/listAllCategories` | Tum kategorileri listeleme |
| GET | `/list/{id}` | ID ile kategori getirme |
| PUT | `/update/{id}` | Kategori guncelleme |
| DELETE | `/delete/{id}` | Kategori silme |
| GET | `/{categoryId}/books` | Kategoriye ait kitaplari listeleme |

## Yayinci Endpointleri

Base path:

```text
/rest/api/Publisher
```

| Method | Endpoint | Aciklama |
| --- | --- | --- |
| POST | `/save` | Yayinci ekleme |
| GET | `/listAllPublishers` | Tum yayincilari listeleme |
| GET | `/list/{id}` | ID ile yayinci getirme |
| PUT | `/update/{id}` | Yayinci guncelleme |
| DELETE | `/delete/{id}` | Yayinci silme |
| GET | `/{publisherId}/books` | Yayinciya ait kitaplari listeleme |

## Odunc Endpointleri

Base path:

```text
/api/loans
```

| Method | Endpoint | Aciklama |
| --- | --- | --- |
| POST | `/save` | Odunc kaydi olusturma |
| GET | `/getAll` | Tum odunc kayitlarini listeleme |
| GET | `/getLoan/{id}` | ID ile odunc kaydi getirme |
| PUT | `/update/{id}` | Odunc kaydi guncelleme |
| DELETE | `/delete/{id}` | Odunc kaydi silme |

## Kullanici Endpointleri

Base path:

```text
/users
```

| Method | Endpoint | Aciklama |
| --- | --- | --- |
| GET | `/me` | Giris yapan kullaniciyi getirir |
| GET | `/` | Tum kullanicilari listeler |
| GET | `/find-By-Id?id=...` | ID ile kullanici getirir |
| PUT | `/update/{id}` | Kullanici gunceller |
| DELETE | `/delete/{id}` | Kullanici siler |

## Yetkilendirme

Bazi endpointlerde `@PreAuthorize` kullanilir. Kitap ekleme, kitap guncelleme, kitap silme ve odunc kaydi olusturma islemleri `ADMIN` veya `LIBRARIAN` rolune sahip kullanicilar icindir.

JWT token alindiktan sonra isteklerde header olarak gonderilir:

```text
Authorization: Bearer <token>
```

## Notlar

- Redis cache kayitlari varsayilan olarak 10 dakika yasam suresine sahiptir.
- Eski cache verileri serialize/deserialize hatasina neden olursa Redis cache temizlenmelidir.
- Mail ayarlari `application.properties` icinde bulunur.
