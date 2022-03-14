package com.sharkapi.web.uiservice.service.jdbc;

import cn.hutool.core.convert.ConverterRegistry;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sharkapi.web.uiservice.pojo.JdbcCatalog;
import com.sharkapi.web.uiservice.pojo.ResultException;
import com.sharkapi.web.uiservice.util.ConnectionUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.*;
import java.util.*;

@Service
public class JdbcQueryService {

    @Resource
    private JdbcCatalog jdbcCatalog;

    public Object service(String paramData) throws ResultException {
        //组装数据
        JSONObject connectionInfo = JSONObject.parseObject(paramData);
        //paramData,组装链接信息
        Map<String, String> driverInfo = new HashMap<>();
        driverInfo.put("file",connectionInfo.getString("file"));
        driverInfo.put("class",connectionInfo.getString("class"));
        driverInfo.put("url",connectionInfo.getString("url"));

        if (driverInfo == null) throw new ResultException(701, "没有数据库驱动信息，请检查。");
        JSONArray params = connectionInfo.getJSONArray("params");

        //获取连接
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = ConnectionUtil.create(driverInfo, connectionInfo);
            conn.setReadOnly(true);
        } catch (Exception e) {
            throw new ResultException(703, e.getMessage());
        }
        if (conn == null)
            throw new ResultException(703, "数据源链接请求失败，请检查url、用户名和密码。");
        try {
            Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
            String querysql = connectionInfo.getString("sql");
            if (StringUtils.isEmpty(querysql)) {
                //没有查询，直接返回数据库信息
                try{
                    DatabaseMetaData metaData = conn.getMetaData();
                    resultMap.put("productName", metaData.getDatabaseProductName());
                    resultMap.put("productVersion", metaData.getDatabaseProductVersion());
                    resultMap.put("userName", metaData.getUserName());
                    resultMap.put("readOnly", metaData.isReadOnly() ? "on" : "off");
                    resultMap.put("driverName", metaData.getDriverName());
                    resultMap.put("driverVersion", metaData.getDriverVersion());
                    return resultMap;
                }catch(Exception e){
                    e.printStackTrace();
                    throw new ResultException(703, e.getMessage());
                }

            } else {
                //查询，如select * from userdata where b=? and A1=?
                querysql = querysql.replaceAll("\r\n", " ").replaceAll("\n", " ");//解决回车后面缺少空格问题

                ps = conn.prepareStatement(querysql);
                ConverterRegistry converterRegistry = ConverterRegistry.getInstance();
                //参数，如 ["2",{type:"java.lang.String",value:"1"}]
                for (int n = 0; params != null && n < params.size(); n++) {
                    //循环设置参数
                    Object param = params.get(n);
                    if (param instanceof JSONObject) {
                        JSONObject paramObject = (JSONObject) param;
                        //转换类型
                        Object pdata = converterRegistry.convert(
                                Class.forName(paramObject.getString("type")),
                                paramObject.get("value"));
                        ps.setObject(n + 1, pdata);
                    } else if (param instanceof String) {
                        ps.setString(n + 1, (String) param);
                    } else {
                        throw new ResultException(702, "sql参数设置错误，请稍后重试。");
                    }
                }
                rs = ps.executeQuery();

                //得到列表
                List<String> columnNames = new ArrayList<String>();
                ResultSetMetaData metaData = rs.getMetaData();
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    columnNames.add(metaData.getColumnName(i));
                }
                resultMap.put("column", columnNames);
                if (columnNames.size() < 1) throw new ResultException(704, "查询结果集组装错误，请稍后重试。");
                List<Object> resultList = new ArrayList<Object>();
                while (rs.next()) {
                    List<Object> lineData = new ArrayList<Object>();
                    for (String name : columnNames) {
                        lineData.add(rs.getObject(name));
                    }
                    if (lineData.size() > 0) {
                        resultList.add(lineData.toArray(new Object[lineData.size()]));
                    }
                }
                resultMap.put("data", resultList);
                return resultMap;
            }
        } catch (ResultException ex) {
            throw ex;
        } catch (Exception ex) {
            //ex.printStackTrace();
            throw new ResultException(703, StringUtils.isEmpty(ex.getMessage()) ? "数据源链接请求失败，请检查url、用户名和密码。" : ex.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
            } catch (Exception ex) {
            }
            try {
                if (ps != null) ps.close();
            } catch (Exception ex) {
            }
            ConnectionUtil.close(conn);
        }
    }
}
