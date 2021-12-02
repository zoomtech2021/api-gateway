# 致用网关平台

### 目录
* <a href="#1">平台背景</a>
* <a href="#2">平台介绍</a>
* <a href="#3">平台特性</a>
* <a href="#4">平台技术</a> 
* <a href="#5">核心组件</a> 
* <a href="#6">项目结构</a>
* <a href="#7">系统截图</a>
* <a href="#8">环境要求</a>
* <a href="#9">运行说明</a>


## <a name="1">平台背景</a>

目前互联网系统通常采用前后端分离的方式，也就是通常所说的C端和S端。其中C端主要是负责展示层的页面渲染，而B端主要负责数据的处理和组装。
C端：主要是前端代码应用，通过HTTP或HTTPS的方式访问NG，然后有NG转发请求给后端应用。  
S端：主要是服务端代码应用，可能会是多个垂直业务领域的微服务同时对C端提供服务，可能是HTTP的WEB应用也可能是DUBBO类型的RPC服务应用，C端的请求通常会路由到S端的WEB应用中。  
按照这种架构方式，逻辑还算清楚，但对于服务端开发来说，通常需要多做两个操作：①封装WEB服务；②接入登录鉴权。只要对C端提供服务，通常都需要做这两件事。  
为了避免此类问题：提供一个统一的API网关应用，统一提供WEB服务，实现标准化请求、接口协议转换、多版本管理、登录鉴权、流控、超时控制、调用监控、服务治理、接口测试工具等功能，减少服务端同学的重复开发工作，完成API的统一管理。

## <a name="2">平台介绍</a>

致用网关平台是一个简易的API网关平台。旨在降低后端服务开发与前端调用的耦合性，实现接口开发标准化、统一化。

<div>如果看不见图片，需要在hosts文件中添加：199.232.68.133 raw.githubusercontent.com   199.232.68.133 githubusercontent.com</div>
<div><img src="https://github.com/zoomtech2021/api-gateway/raw/master/api-gateway-server/src/main/resources/static/images/gateway-design.png" width="800" height="600"/></div>

## <a name="3">平台特性</a>
*  目前网关仅支持服务端的Dubbo服务接入（HTTP服务暂不支持）
*  支持界面化的接口配置管理
*  安全机制(登录鉴权、接口授权、签名检查)
*  流控机制(sentinel单机限流)
*  处理流程、可插拔机制(组件化配置)
*  接口mock（暂不支持）
*  支持接口的快速录入测试功能
*  提供了用户管理、角色管理（支持按接口类目授权，暂不支持APPKEY授权）
*  跨环境发布（暂不支持，后续会提供接口导入、导出功能）
*  平台支持分布式部署，可按需任意扩展节点部署；同时支持了二级缓存配置访问特性，以满足高并发要求
*  平台技术架构图

<div><img src="https://github.com/zoomtech2021/api-gateway/raw/master/api-gateway-server/src/main/resources/static/images/gateway-technical.jpg"/></div>


## <a name="4">平台技术</a>
* 服务端技术：springboot2.2.6、mysql5+、fastjson、druid、mybatis、logback。
* 前端技术：jquery easyui
* 中间件:apache-dubbo2.7.8、redis、alibaba sentinel、elastic-job、zoomkeeper




## <a name="5">核心组件</a>

*  GatewayApiController     网关API入口
*  RequestParseProcessor    请求报文解析
*  ApiConfigLoadProcessor   API配置加载
*  ApiParamParseProcessor   API参数解析
*  SignCheckProcessor       请求签名延签
*  FlowControlProcessor     API流控
*  TenantContextProcessor   SAAS租户上下文处理
*  LoginCheckProcessor      登录鉴权处理
*  AuthCheckProcessor       接口权限处理
*  ServiceInvokeProcessor   远程服务调用
*  ResultConverterProcessor 响应结果处理


<br>

## <a name="6">项目结构</a>

#### 1、maven项目结构

    api-gateway

        / — api-gateway-biz         【业务层:缓存操作、定时任务、redis消息监听、API配置service、远程服务调用代理】

        / — api-gateway-common      【公共层：常量、枚举、异常、DTO、工具类定义】

        / — api-gateway-dal         【数据访问层：mysql操作、redis操作】
        
        / — api-gateway-facade      【接口提供层：主要用于对外保留dubbo服务接口、接口参数定义】
        
        / — api-gateway-integration 【接口依赖层：用于外部接口依赖的定义和管理，用于依赖接口参数转内部DTO，起到防腐层作用】
        
        / — api-gateway-model       【实体定义层：主要定义各种网关系统内部传输的对象,非标准领域模型层】
        
        / — api-gateway-server      【WEB层：Application类、API入口、后台管理入口】
            
            / — com.zhiyong.gateway.admin      【管理端：API配置、参数类型配置、用户管理、权限管理、监控】
            
            / — com.zhiyong.gateway.server     【服务端：API请求入口、请求路由】

#### 2、WEB层目录结构

api-gateway-server

	/ — com.zhiyong.gateway.admin

		/ — common                            【管理端WEB公共代码】

		/ — config                            【管理端配置定义】
		
		    / — RedisSessionConfig            【管理端登录session】

		/ — controller                        【管理端页面接口定义】

		/ — filter                            【管理端LoginFilter】

	/ — com.zhiyong.gateway.server
    
        / — context                           【API请求上下文定义】

        / — controller                        
        
            / — GatewayApiController          【API调用入口】

        / — processor                         【API核心处理组件处理器定义】

            / — ApiProcessorChain             【API调用处理器链式管理器】	

    / — src.main.resources                    【管理端静态资源】
        
        / — static                            【管理端前端代码】
        
            / — css                           【页面样式】
            
            / — js                            【第三方公共js】
            
            / — pagejs                        【网关自定义js】
            
        / — templates                         【管理端前端页面代码】
        
            / — api                           【API配置管理】
            
            / — app                           【APPKEY配置管理】
            
            / — group                         【API类目配置管理】	
            
            / — monitor                       【API监控页面】	
            
            / — struct                        【API自定义参数类型结构配置管理】	
            
            / — user                          【管理端用户权限配置管理】	
            
            / — doc.html                      【网关首页文档说明:前后端接入网关帮助手册】	
            
            / — index.html                    【网关首页】	
            
            / — login.html                    【网关登录页面】	
	
## <a name="7">系统截图</a>

<div><img src="https://github.com/zoomtech2021/api-gateway/raw/master/api-gateway-server/src/main/resources/static/images/网关截图1.png"/></div>
<div><img src="https://github.com/zoomtech2021/api-gateway/raw/master/api-gateway-server/src/main/resources/static/images/网关截图2.png"/></div>
<div><img src="https://github.com/zoomtech2021/api-gateway/raw/master/api-gateway-server/src/main/resources/static/images/网关截图3.png"/></div>
<div><img src="https://github.com/zoomtech2021/api-gateway/raw/master/api-gateway-server/src/main/resources/static/images/网关截图4.png"/></div>
<div><img src="https://github.com/zoomtech2021/api-gateway/raw/master/api-gateway-server/src/main/resources/static/images/网关截图5.png"/></div>
<div><img src="https://github.com/zoomtech2021/api-gateway/raw/master/api-gateway-server/src/main/resources/static/images/网关截图6.png"/></div>

## <a name="8">环境要求</a>
1. JDK1.8
2. zk、redis
3. Mysql数据库


## <a name="9">运行说明</a>

* 下载项目
* 处理以下几个核心配置

	* 初始化数据库脚本执行							
	  api-gateway-dal/src/main/resources/sql/db-init.sql
	* 修改初始化系统配置（mysql、redis、zk连接配置）					
	  api-gateway-server/src/main/resources/application.yml
	
* 运行api-gateway-server模块下的com.zhiyong.gateway.server.Application类启动项目
* 浏览器地址栏输入:  http://localhost:19002 进入网关管理后台 账号：admin/admin123

## <a name="10">平台作者</a>

### 毛军锐 VX：ybyh8899



