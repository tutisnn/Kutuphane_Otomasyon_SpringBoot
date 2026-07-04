package org.example.kutuphaneotomasyon.ResponseMessage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class GenericResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private T data;
    private String message;

    public static <T> GenericResponse<T> error(String message) { //findBy, delete, update için
        return GenericResponse.<T>builder()
                .message(message)
                .build();
    }

    public static <T> GenericResponse<T> success(T data) {
        return GenericResponse.<T>builder()
                .message("İşlem başarılı")
                .data(data)
                .build();
    }

}
