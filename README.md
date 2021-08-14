## 一、概念定义
1. console后台
> 爱码开源管理后台
2. 业务后台
> 业务系统管理后台

### console后台与业务的分工：
1. console后台与业务后台分离部署；
2. console后台负责权限、账号、菜单的管理；
3. 通过在console管理后台配置菜单，业务后台集成到console管理后台中，使之看上去像一个完整的系统；
4. 业务后台通过filter读取cookie信息并解析使用；
> cookie信息共享console后台的登录账户信息，其中包括登录帐户管理的所有组织机构编号集合
5. 业务后台通过filter控制菜单功能权限；
> 业务后台通过<#canVisit/>标签控制页面按钮、链接的显示/隐藏。

## 二、console后台与业务后台的相同点
均依赖于icode-common基础类库

## 三、业务后台开发步骤

由于console管理后台与业务后台在页面端存在跨域问题，在开发过程中，应采用以下步骤：

1. 将console后台部署于测试环境，配置域名指向，使用域名访问，例如：
> http://console.icode.js.cn/
2. 在开发环境开发业务后台，修改application.properties，设置console.root参数，例如：
```
# console管理后台的访问根地址
console.root=http://console.icode.js.cn/
```
3. 在开发环境设置设置系统host文件、应用端口号，使之访问地址与测试环境的console后台在相同根域名下，例如：
> http://my.icode.js.cn/
4. console后台配置开发环境业务系统的功能菜单，以http://my.icode.js.cn/开头；
5. 在console后台访问开发环境业务功能。