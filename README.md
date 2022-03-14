# 本地数据API网关 local-data-api-gateway
## Introduction
Configure and connect to your various databases, quickly build API services by querying SQL statements, and conduct special data conversion through custom data processor, so that your local data can easily, quickly and safely provide controllable HTTP restful JSON API data services.
## 网关介绍
通过配置连接到您的各种数据库，通过查询SQL语句即可快速构建API服务，通过自定义数据处理器进行数据特殊转换，让您的本地数据方便快捷安全的提供可控http restful json api数据服务。
## 功能列表
1. 数据源管理：支持JDBC驱动自定义上传和配置
2. API接口：一个API接口支持多个数据项（可采用不同数据源）、多个入参处理器、多个结果处理器的配置。处理器：可以入参数据或结果数据进行JAVA处理，比如自定义的加密和解密。
3. 服务迁移说明：API网关文件列表、API网关备份、API网关恢复和API网关升级。
4. 网关配置：登录密码修改、互联网访问的域名/IP配置、签名安全码配置、时间戳验证配置。
## 直接下载使用
完整版约170MB，含常用JDBC驱动和JDK11运行环境，支持windows、linux、macos的64位操作系统的运行。
下载地址：进入[网站](https://www.511ds.com)：https://www.511ds.com，点击顶部菜单“功能清单”，找到“本地数据API网关”功能描述，点击“下载链接（点击下载）”即可直接下载使用。
## 代码构建命令（LINUX或MACOS SHELL示例）
ui-local-apigateway：网关前端代码
local-apigateway：网关后台代码
git clone https://github.com/yiyiwk/local-data-api-gateway
cd local-data-api-gateway
cd ui-local-apigateway
npm install
npm run build
cd ..
rm -rf ./local-apigateway/src/main/resources/static/sharkapiweb
mkdir ./local-apigateway/src/main/resources/static/sharkapiweb
cp -rf ./ui-local-apigateway/dist/* ./local-apigateway/src/main/resources/static/sharkapiweb/
cd ./local-apigateway
mvn clean package -Dmaven.test.skip=true
## 运行本地数据API网关程序包
上面mvn构建成功后包位置：./target/apiserver-0.0.1-SNAPSHOT.jar
安装和配置好JDK11运行环境
export JAVA_HOME
java -Xms128m -Xmx2048m -Dfile.encoding=utf-8 -jar ./target/apiserver-0.0.1-SNAPSHOT.jar --server.port=56888
成功启动后即可访问（初始密码为空）：http://127.0.0.1:56888
# 关于我们
欢迎访问[迪赛智慧数可视化互动平台](https://www.511ds.com)：https://www.511ds.com，免费使用第三代大数据可视化代表产品。平台不但拥有市场上主流数据大屏功能，还拥有第三代特有的智能分析、多屏互动、指标预警和分享评论等功能。
如果疑问或建议，请访问[迪赛智慧数可视化互动平台](https://www.511ds.com)联系客服，谢谢。