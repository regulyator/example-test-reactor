package com.example.demo.service;

import com.example.demo.dto.RequestTarget;
import com.example.demo.dto.Response;
import com.example.demo.dto.ResponseTarget;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ExternalServiceImpl implements ExternalService {

    @Override
    public Response getSomeInfo() {
        return Response.builder()
                .build();
    }

    @Override
    public ResponseTarget placeSomeInfo(RequestTarget requestTarget) {
        log.info("Receive target {} ", requestTarget.getInfo());
        return ResponseTarget.builder().build();
    }
}
