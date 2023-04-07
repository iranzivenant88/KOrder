package com.claivenant.orderService.config;

import com.claivenant.orderService.external.decoder.CustomErrorDecoder;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {
    ErrorDecoder errorDecoder(){
        return new CustomErrorDecoder();
    }

}
