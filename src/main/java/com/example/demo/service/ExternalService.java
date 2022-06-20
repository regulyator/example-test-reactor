package com.example.demo.service;

import com.example.demo.dto.RequestTarget;
import com.example.demo.dto.Response;
import com.example.demo.dto.ResponseTarget;

public interface ExternalService {
    Response getSomeInfo();

    ResponseTarget placeSomeInfo(RequestTarget requestTarget);
}
