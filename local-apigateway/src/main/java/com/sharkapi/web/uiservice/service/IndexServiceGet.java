package com.sharkapi.web.uiservice.service;

import com.sharkapi.web.uiservice.pojo.ResultException;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IndexServiceGet {

    public void service(String method, String paramData, MultipartFile file, HttpServletRequest request, HttpServletResponse response) throws ResultException;
}
