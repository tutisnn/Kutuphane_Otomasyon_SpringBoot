package org.example.kutuphaneotomasyon.exception;

import lombok.Getter;

@Getter
public enum MessageType {
    NO_RECORD_EXISTS("1001", "kayıt bulunamadı"),
    EMPTY_ID("1002", "No registration found with the same id"),
    EMPTY_USER("1003", "No registration found with the same user id"),
    EMPTY_BOOK("1004", "No registration found with the same book id"),
    FOUND_ID("1005", "Book found with the same id"),
    EMPTY_LIST("1006", "No registration found"),
    BOOK_NOT_AVAILABLE("1007", "Kitap şu an uygun değil"),
    GENERAL_EXCEPTION("9999", "genel bir hata oluştu");

    private final String code;
    private final String message;

    MessageType(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
