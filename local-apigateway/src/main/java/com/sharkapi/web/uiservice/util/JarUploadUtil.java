package com.sharkapi.web.uiservice.util;

import com.sharkapi.web.uiservice.pojo.ResultException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class JarUploadUtil {

    private static String ROOTPATH;    //绝对路径

    private static String USERPATH;  //相对路径

    @Value("${api.jar.upload.path}")
    public void setPath(String path){

        JarUploadUtil.ROOTPATH = System.getProperty("user.dir") + path;
    }

    @Value("${api.jar.upload.path}")
    public void setUsePath(String path){
        JarUploadUtil.USERPATH =  path;
    }

    public static String uploadFile(MultipartFile file) throws ResultException {

        String returnName = "";
        String time = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String path = ROOTPATH+time+"/";
        //完整路径是 xxxx/jar/upload/20211222121010/1/文件名
        String usepath = USERPATH+time+"/";
        try{
            if(file != null){
                InputStream filesm = file.getInputStream();
                String fileName = file.getOriginalFilename();
                String replaceName = replaceAndRenameFile(fileName);
                File newFile = new File(path+replaceName);  //新文件名

                if(!newFile.getParentFile().exists()){
                    newFile.getParentFile().mkdirs();
                }
                FileOutputStream out = new FileOutputStream(newFile);
                // 每次读取的字节长度
                int n = 0;
                // 存储每次读取的内容
                byte[] bb = new byte[1024];
                while ((n = filesm.read(bb)) != -1) {
                    // 将读取的内容，写入到输出流当中
                    out.write(bb, 0, n);
                }
                // 关闭输入输出流
                out.close();
                filesm.close();
                returnName = usepath+replaceName;
            }
        }catch(Exception e){
            e.printStackTrace();
            throw new ResultException(146,"上传jar包出错，请稍后重试。");
        }
        return returnName;
    }

    public static String uploadJar(MultipartFile file) throws ResultException {

        String returnName = "";
        //完整路径是 /jdbc/文件名
        String usepath = System.getProperty("user.dir")+"/jdbc/";
        try{
            if(file != null){
                InputStream filesm = file.getInputStream();
                String fileName = file.getOriginalFilename();
                String replaceName = replaceAndRenameFile(fileName);
                File newFile = new File(usepath+replaceName);  //新文件名

                if(!newFile.getParentFile().exists()){
                    newFile.getParentFile().mkdirs();
                }
                FileOutputStream out = new FileOutputStream(newFile);
                // 每次读取的字节长度
                int n = 0;
                // 存储每次读取的内容
                byte[] bb = new byte[1024];
                while ((n = filesm.read(bb)) != -1) {
                    // 将读取的内容，写入到输出流当中
                    out.write(bb, 0, n);
                }
                // 关闭输入输出流
                out.close();
                filesm.close();
                returnName = replaceName;
            }
        }catch(Exception e){
            e.printStackTrace();
            throw new ResultException(146,"上传jar包出错，请稍后重试。");
        }
        return returnName;
    }


    /**
     * 字符串是否包含中文
     * @return true 包含中文字符 false 不包含中文字符
     */
    public static boolean isContainChinese(String str) {
        Pattern p = Pattern.compile("[\u4E00-\u9FA5|\\！|\\，|\\。|\\（|\\）|\\《|\\》|\\“|\\”|\\？|\\：|\\；|\\【|\\】]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    public static String replaceAndRenameFile(String file) {
        if( file==null || file.trim().length()==0) return "";
        if( !isContainChinese( file)) return file;
        //有中文
        int pos1 = file.lastIndexOf("/");
        int pos2 = file.lastIndexOf(".");
        String ext = ".unknow";
        if( pos2>0)  ext = file.substring( pos2);
        //扩展名不能有中文
        if( isContainChinese( ext)) ext=".unknow";
        //文件名不能有中文
        String name = file.substring( pos1+1, pos2);
        if(  isContainChinese( name)) name=""+1;

        String newfile = file.substring(0, pos1+1)+name+ext;

        return newfile;
    }
}
