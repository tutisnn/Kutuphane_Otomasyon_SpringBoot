package org.example.kutuphaneotomasyon.Dto;

import org.example.kutuphaneotomasyon.Entity.Durum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Kitap bilgilerini içeren DTO")
public class DtoBookIU {

    @Schema(description = "Kitap ISBN numarası", example = "9789750802942")
    private String isbn;

    @Schema(description = "Kitap adı", example = "Suç ve Ceza")
    private String ad;

    @Schema(description = "Kitap baskı yılı", example = "2023")
    private int baskiYili;

    @Schema(description = "Kitap durumu", example = "MUSAIT", allowableValues = {"MUSAIT", "MEVCUT", "KAYIP", "ODUNC_VERILDI"})
    private Durum durum; // Durum enum tipi için de Swagger açıklaması eklenmeli



    @Schema(description = "Kitap dili", example = "Türkçe")
    private String dil;

    @Schema(description = "Yazar adı", example = "Fyodor")
    private String authorAd;

    @Schema(description = "Yazar soyadı", example = "Dostoyevski")
    private String authorSoyad;

    @Schema(description = "Yayınevi adı", example = "Can Yayınları")
    private String publisherAd;

    @Schema(description = "Kitap kategorisi", example = "Roman")
    private String categoryAd;
}
