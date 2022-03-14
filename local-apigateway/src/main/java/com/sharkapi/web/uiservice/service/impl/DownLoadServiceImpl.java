package com.sharkapi.web.uiservice.service.impl;

import com.sharkapi.web.uiservice.pojo.ResultException;
import com.sharkapi.web.uiservice.service.IndexServiceGet;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

/*demo工程下载类
* */
@Service("DownLoadService")
public class DownLoadServiceImpl implements IndexServiceGet {

    @Override
    public void service(String method, String paramData, MultipartFile file,
                          HttpServletRequest request, HttpServletResponse response) throws ResultException {

        String path = System.getProperty("user.dir")+"/jar/demo/handler-jar.jar";   //当前demo工程的路径
        File f = new File(path);
        if (!f.exists()) {
            throw new ResultException(255, "下载的demo工程不存在。");
        }
        try {
            BufferedInputStream br = new BufferedInputStream(new FileInputStream(f));
            byte[] buf = new byte[1024];
            int len = 0;
            response.reset();
            response.setContentType("application/x-msdownload");
            response.setHeader("Content-Disposition",
                    "attachment; filename=" + new String(f.getName().getBytes("GBK"), "iso-8859-1"));
            OutputStream out = response.getOutputStream();
            while ((len = br.read(buf)) > 0)
                out.write(buf, 0, len);
            br.close();
            out.close();
        } catch (Exception e) {
            throw new ResultException(256, "下载demo工程异常，请稍后再试。");
        }
    }
}
