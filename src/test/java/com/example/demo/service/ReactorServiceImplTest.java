package com.example.demo.service;

import com.example.demo.dto.RequestTarget;
import com.example.demo.dto.Response;
import com.example.demo.dto.ResponseTarget;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Repeat;
import org.springframework.test.context.ContextConfiguration;
import reactor.core.Disposable;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {ReactorServiceImpl.class, ExternalServiceImpl.class})
@SpringBootTest
@ExtendWith(MockitoExtension.class)
class ReactorServiceImplTest {

    @MockBean
    private ExternalServiceImpl externalService;

    @Autowired
    private ReactorService reactorService;


    @Test
    //вот это чтобы повторить тест N раз
    @RepeatedTest(100)
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void process() {
        when(externalService.getSomeInfo())
                .thenReturn(Response.builder()
                        .repeat(true)
                        .someInformation(Arrays.asList("1 INFO", "2 INFO", "3 INFO"))
                        .build())
                .thenReturn(Response.builder()
                        .repeat(false)
                        .someInformation(Arrays.asList("4 INFO", "5 INFO"))
                        .build());
        when(externalService.placeSomeInfo(any(RequestTarget.class)))
                .thenReturn(ResponseTarget.builder().build())
                .thenReturn(ResponseTarget.builder().build())
                .thenReturn(ResponseTarget.builder().build())
                .thenReturn(ResponseTarget.builder().build())
                .thenReturn(ResponseTarget.builder().build());
        reactorService.process();

        verify(externalService, times(2)).getSomeInfo();
        verify(externalService, times(5)).placeSomeInfo(any(RequestTarget.class));
    }
}