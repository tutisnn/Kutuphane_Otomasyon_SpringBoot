package org.example.kutuphaneotomasyon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class KutuphaneOtomasyonApplication {

    public static void main(String[] args) {
        SpringApplication.run(KutuphaneOtomasyonApplication.class, args);
    }

}
