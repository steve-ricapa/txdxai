package com.example.txdxai;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableEncryptableProperties
public class TxdxaiApplication {

    public static void main(String[] args) {
        SpringApplication.run(TxdxaiApplication.class, args);
    }

}
