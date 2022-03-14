package com.sharkapi.web.uiservice.service;

import com.sharkapi.web.uiservice.pojo.ResultException;
import org.springframework.web.multipart.MultipartFile;

public interface IndexService {

    Object service(String method,String paramData, MultipartFile file) throws ResultException;
}
