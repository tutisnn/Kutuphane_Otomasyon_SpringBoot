# Cascade Types

## Yaygin cascade tipleri

### CascadeType.PERSIST

Parent ilk kez kaydedildiginde child da kaydedilir.

Ornek: kategori olustururken yaninda yeni kitaplar da geldiyse, kategori save edilince kitaplar da save olur.

### CascadeType.MERGE

Parent guncellendiginde child tarafindaki degisiklikler de merge edilir.

Ornek: kategori guncellenirken bagli kitap listesindeki nesneler de guncellensin istiyorsan kullanilir.

### CascadeType.REMOVE

Parent silinirse child da silinir.

Bu en riskli olanlardan biri. Mesela kategori silindiginde kitaplarin da silinmesini istemiyorsan kullanilmamali.

### CascadeType.REFRESH

Parent veritabanindan refresh edilirse child da refresh edilir.

Daha az kullanilir.

### CascadeType.DETACH

Parent persistence context'ten ayrilirsa child da ayrilir.

Bu da genelde daha ileri JPA kullanimlarinda gorulur.

### CascadeType.ALL

Hepsini kapsar:

```text
PERSIST + MERGE + REMOVE + REFRESH + DETACH
```

Yani parent'a yapilan neredeyse tum persistence islemleri child'a da yansir.

## Bizim projede su an kullanilan

Projede `CascadeType` su dosyada kullaniliyor:

```text
src/main/java/org/example/kutuphaneotomasyon/Entity/Category.java
```

Kullanim:

```java
@OneToMany(cascade = CascadeType.ALL)
private List<Book> bookList;
```

Burada parent entity `Category`, child taraf ise `Book` listesidir. `CascadeType.ALL` kullanildigi icin `Category` uzerinden yapilan persist, merge, remove, refresh ve detach islemleri iliskili `Book` kayitlarina da yansiyabilir.

Ozellikle `REMOVE` de dahil oldugu icin dikkatli kullanilmalidir. Kategori silindiginde iliskili kitaplarin da silinmesi istenmiyorsa `CascadeType.ALL` yerine daha dar bir cascade tipi tercih edilmelidir.
