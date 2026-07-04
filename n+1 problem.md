# N+1 Problem Loglari

## N+1 problemi nedir?

N+1 problemi, ORM aracinin once ana listeyi tek sorguyla cekip, sonra listedeki her kayit icin iliskili verileri ayri ayri sorgulamasidir.

Ornek olarak once tum kitaplar cekilir:

```sql
select * from book;
```

Daha sonra her kitap icin yazar bilgisi ayri sorguyla cekilirse:

```sql
select * from author where id = ?;
select * from author where id = ?;
select * from author where id = ?;
```

Bu durumda 1 ana sorgu + N tane ek sorgu olusur. Bu yuzden adi N+1 problemidir.

Kucuk veride fark edilmeyebilir, ama veri sayisi arttikca performans sorununa donusur. Mesela 100 kitap varsa 1 kitap sorgusu ve her kitap icin ek author/publisher/category sorgulari calisabilir.

Bu problem genelde `LAZY` veya `EAGER` iliskilerin listeleme sirasinda kontrolsuz sekilde tetiklenmesiyle gorulur. Cozum icin ihtiyac olan iliskiler `join fetch`, `@EntityGraph` veya DTO query ile tek sorguda bilincli sekilde cekilebilir.

Endpoint:

```text
GET http://localhost:8080/rest/api/Book/listAll
```

Olusan Hibernate loglari:

```sql
Hibernate: select b1_0.id,b1_0.ad,b1_0.yazar_id,b1_0.baski_yili,b1_0.kategori_id,b1_0.dil,b1_0.durum,b1_0.isbn,b1_0.kapak_fotosu_url,b1_0.yayinci_id from kutuphaneotomasyon.book b1_0
Hibernate: select a1_0.id,a1_0.ad,a1_0.soyad from kutuphaneotomasyon.author a1_0 where a1_0.id=?
Hibernate: select c1_0.id,c1_0.ad,c1_0.deleted from kutuphaneotomasyon.category c1_0 where c1_0.id=? and (c1_0.deleted=false)
Hibernate: select p1_0.id,p1_0.ad from kutuphaneotomasyon.publisher p1_0 where p1_0.id=?
Hibernate: select c1_0.id,c1_0.ad,c1_0.deleted from kutuphaneotomasyon.category c1_0 where c1_0.id=? and (c1_0.deleted=false)
Hibernate: select a1_0.id,a1_0.ad,a1_0.soyad from kutuphaneotomasyon.author a1_0 where a1_0.id=?
Hibernate: select p1_0.id,p1_0.ad from kutuphaneotomasyon.publisher p1_0 where p1_0.id=?
Hibernate: select a1_0.id,a1_0.ad,a1_0.soyad from kutuphaneotomasyon.author a1_0 where a1_0.id=?
Hibernate: select c1_0.id,c1_0.ad,c1_0.deleted from kutuphaneotomasyon.category c1_0 where c1_0.id=? and (c1_0.deleted=false)
Hibernate: select p1_0.id,p1_0.ad from kutuphaneotomasyon.publisher p1_0 where p1_0.id=?
```

## Yorum

Bu loglar `getAllBooks()` endpoint'inde N+1 problemi oldugunu gosterir.

Ilk sorgu tum kitaplari getirir:

```sql
select ... from kutuphaneotomasyon.book
```

Daha sonra her kitap icin iliskili entity'ler ayri sorgularla cekilir:

```sql
select ... from kutuphaneotomasyon.author where id=?
select ... from kutuphaneotomasyon.category where id=?
select ... from kutuphaneotomasyon.publisher where id=?
```

Yani akisin ozeti:

```text
1 sorgu  -> book listesini getirir
N sorgu -> her book icin author/category/publisher bilgilerini getirir
```

Bu problem su kod akisi nedeniyle ortaya cikar:

```java
List<Book> books = bookRepository.findAll();
```

Sonra mapper icinde iliskili alanlara erisilir:

```java
book.getAuthor().getAd();
book.getPublisher().getAd();
book.getCategory().getAd();
```

Bu nedenle `Book -> Author`, `Book -> Category` ve `Book -> Publisher` iliskilerinde ek select sorgulari olusur.

## Fetch join sonrasi log

`getAllBooks()` methodu `FETCH JOIN` kullanacak sekilde guncellendikten sonra log tek sorguya dusmustur:

```text
getAllBooks called...
Hibernate: select b1_0.id,b1_0.ad,a1_0.id,a1_0.ad,a1_0.soyad,b1_0.baski_yili,c1_0.id,c1_0.ad,c1_0.deleted,b1_0.dil,b1_0.durum,b1_0.isbn,b1_0.kapak_fotosu_url,p1_0.id,p1_0.ad from kutuphaneotomasyon.book b1_0 join kutuphaneotomasyon.author a1_0 on a1_0.id=b1_0.yazar_id join kutuphaneotomasyon.publisher p1_0 on p1_0.id=b1_0.yayinci_id join kutuphaneotomasyon.category c1_0 on c1_0.id=b1_0.kategori_id and (c1_0.deleted=false)
```

Bu logda kitaplar, yazar, yayinci ve kategori bilgileri tek SQL sorgusunda `join` ile birlikte cekilmektedir. Boylece onceki loglarda gorulen ek `author`, `publisher` ve `category` select sorgulari ortadan kalkmistir.

## EntityGraph ile cozum

N+1 problemi `FETCH JOIN` yerine `@EntityGraph` ile de cozulebilir.

Bu projede `BookRepository` icinde `findAllWithRelations()` methodu icin su kullanim yapilabilir:

```java
@EntityGraph(attributePaths = {"author", "publisher", "category"})
@Query("select b from Book b")
List<Book> findAllWithRelations();
```

Burada `attributePaths` icine `Book` entity'sinde beraber yuklenmesini istedigimiz iliskiler yazilir:

```text
author
publisher
category
```

Boylece `Book` listesi cekilirken yazar, yayinci ve kategori bilgileri de ayni fetch plani icinde yuklenir.

Service tarafinda kullanim degismez:

```java
List<Book> books = bookRepository.findAllWithRelations();
```

Mapper icinde su alanlara erisildiginde ekstra select sorgulari olusmamasi hedeflenir:

```java
book.getAuthor().getAd();
book.getPublisher().getAd();
book.getCategory().getAd();
```

`FETCH JOIN` ile `EntityGraph` arasindaki temel fark:

```text
FETCH JOIN  -> JPQL sorgusunun icinde join fetch yazariz.
EntityGraph -> Hangi iliskilerin yuklenecegini ayri bir fetch plani olarak belirtiriz.
```

Bu projede once `FETCH JOIN` kullanildi, sonra ayni amac icin `@EntityGraph` tercih edildi.
