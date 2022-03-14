package com.sharkapi.web.uiservice.util;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import com.sharkapi.web.uiservice.pojo.ResultException;
import org.apache.commons.lang3.StringUtils;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 数据库连接工具类
 */
@Component
public class ConnectionUtil {

    private static final String LOCK = new String("LOCK");

    /** 驱动包存放根目录绝对路径 */
    public static String driverJarRootDirAbsolutePath ;

    @Value("${sharkconfig.jdbc.driverJarPath}")
    public void setJarPath(String path){

        ConnectionUtil.driverJarRootDirAbsolutePath = System.getProperty("user.dir")+ path;
    }


    /** 驱动对应类加载器集合 key: 驱动包路径:驱动类名称 value: urlClassLoader实例 */
    private static Map<String, URLClassLoader> urlClassLoaderMap = new ConcurrentHashMap<>();

    /**
     *  创建数据库连接
     *  driverInfoPO 驱动信息
     *  connectionInfoPO 连接信息
     *  数据库连接
     *  Exception 获取DB连接失败
     */
    public static Connection create(Map<String,String> driverInfo, JSONObject connectionInfo) throws Exception {
        String[] fileList = driverInfo.get( "file").split( "###");
    	for( String name: fileList) {
	    	String jarFile = driverJarRootDirAbsolutePath + File.separator + name;
            File testJarFile = new File(jarFile);
	        if( !testJarFile.exists() || !testJarFile.isFile())
                throw new ResultException(251, "未发现"+name+"数据库驱动jar包，请检查jdbc文件夹，添加此数据库驱动。");
    	}
    	
        String key = String.format("%s:%s",driverInfo.get( "file"), driverInfo.get( "class"));
        URLClassLoader loader = null;
        synchronized (LOCK) {
            loader = urlClassLoaderMap.get(key);
            if(loader == null){
                URL[] urls = new URL[fileList.length];
            	for( int n=0; n<fileList.length;n++) {
            		urls[n] = new URL("jar:file:" + driverJarRootDirAbsolutePath + File.separator + fileList[n] + "!/");
            	}
            	loader = new URLClassLoader( urls);
                urlClassLoaderMap.put(key,loader);
            }
        }
        // 记录原始ClassLoader并设置当前线程的ClassLoader
        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(loader);

        // 获取数据库连接
        Class<?> classz = loader.loadClass( driverInfo.get( "class"));
        @SuppressWarnings("deprecation")
		Driver driver = (Driver)classz.newInstance();
        Properties connInfo = new Properties();
        if( StringUtils.isNoneEmpty( connectionInfo.getString( "username")))
        	connInfo.put("user", connectionInfo.getString( "username"));
        if( StringUtils.isNoneEmpty( connectionInfo.getString( "password")))
        	connInfo.put("password", connectionInfo.getString( "password"));
        //String type = driverInfo.get( "type");
        Connection conn = driver.connect(connectionInfo.getString( "url"), connInfo);
        // 还原原始ClassLoader
        Thread.currentThread().setContextClassLoader(oldClassLoader);
        return conn;
    }

    /**
     * 关闭数据库连接
     */
    public static void close(Connection conn){
        try {
            if(conn != null)  conn.close();
        } catch (Exception throwables) {
        }
    }

}
