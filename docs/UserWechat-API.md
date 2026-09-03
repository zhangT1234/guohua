# 用户微信绑定接口文档（UserWechatController）

> 模块：`用户微信绑定`  
> 基础路径：`/yuanzang/UserWechat`  
> 在线文档：启动服务后访问 Knife4j / Swagger（如 `/yuanzang/doc.html`）

---

## 1. 根据微信授权 code 获取绑定记录

| 项 | 说明 |
| --- | --- |
| 接口名称 | 根据微信授权code获取绑定记录 |
| 请求方式 | `GET` |
| 请求路径 | `/yuanzang/UserWechat/getByCode` |
| Content-Type | — |

### 请求参数

| 参数名 | 位置 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- | --- |
| code | Query | String | 是 | 微信授权 code，服务端换取 openid 后查询绑定表 |

### 请求示例

```http
GET /yuanzang/UserWechat/getByCode?code=081xxxxx HTTP/1.1
Host: {host}:{port}
```

### 成功响应示例

```json
{
  "code": "0",
  "IsOk": true,
  "ErrorCode": "S",
  "Message": "查询成功",
  "data": {
    "id": 1,
    "username": "zhangsan",
    "openid": "oXXXX-xxxxxxxx",
    "insertDt": "2026-08-21T10:00:00.000+00:00",
    "updateDt": "2026-08-21T10:00:00.000+00:00"
  },
  "phid": ""
}
```

### 失败响应示例

```json
{
  "code": "1",
  "IsOk": false,
  "ErrorCode": "E",
  "Message": "未查询到绑定记录",
  "data": ""
}
```

### 业务说明

1. 使用微信 `code` 调用微信接口换取 `openid`
2. 按 `openid` 查询表 `user_wechat`
3. 无记录或换取失败时返回错误信息

---

## 2. 根据 openid 和 username 保存绑定记录

| 项 | 说明 |
| --- | --- |
| 接口名称 | 根据openid和username保存绑定记录 |
| 请求方式 | `POST` |
| 请求路径 | `/yuanzang/UserWechat/save` |
| Content-Type | `application/x-www-form-urlencoded`（Query / Form 参数） |

### 请求参数

| 参数名 | 位置 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- | --- |
| openid | Query | String | 是 | 微信用户 openid |
| username | Query | String | 是 | 系统用户账号（对应 `userno`） |

### 请求示例

```http
POST /yuanzang/UserWechat/save?openid=oXXXX-xxxxxxxx&username=zhangsan HTTP/1.1
Host: {host}:{port}
```

或表单：

```http
POST /yuanzang/UserWechat/save HTTP/1.1
Host: {host}:{port}
Content-Type: application/x-www-form-urlencoded

openid=oXXXX-xxxxxxxx&username=zhangsan
```

### 成功响应示例

```json
{
  "code": "0",
  "IsOk": true,
  "ErrorCode": "S",
  "Message": "保存成功",
  "data": {
    "id": 2,
    "username": "zhangsan",
    "openid": "oXXXX-xxxxxxxx",
    "insertDt": "2026-08-21T10:05:00.000+00:00",
    "updateDt": "2026-08-21T10:05:00.000+00:00"
  },
  "phid": ""
}
```

### 失败响应示例

```json
{
  "code": "1",
  "IsOk": false,
  "ErrorCode": "E",
  "Message": "保存失败",
  "data": ""
}
```

### 业务说明

1. 若本地已存在相同 `openid` 或相同 `username` 的记录，先删除
2. 再插入新的 `username` ↔ `openid` 绑定关系
3. `openid` 或 `username` 为空时保存失败

---

## 3. 获取微信临时二维码

| 项 | 说明 |
| --- | --- |
| 接口名称 | 获取微信临时二维码 |
| 请求方式 | `GET` |
| 请求路径 | `/yuanzang/UserWechat/getQrCode` |
| Content-Type | — |

### 请求参数

无

### 请求示例

```http
GET /yuanzang/UserWechat/getQrCode HTTP/1.1
Host: {host}:{port}
```

### 成功响应示例

```json
{
  "code": "0",
  "IsOk": true,
  "ErrorCode": "S",
  "Message": "获取成功",
  "data": {
    "qrUrl": "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=gQH47jo%2FAAAAAAA...",
    "sceneStr": "a1b2c3d4e5f6789012345678abcdef12"
  },
  "phid": ""
}
```

### 失败响应示例

```json
{
  "code": "1",
  "IsOk": false,
  "ErrorCode": "E",
  "Message": "获取二维码失败",
  "data": ""
}
```

### 业务说明

1. 调用微信接口创建临时字符串场景二维码（`QR_STR_SCENE`），`expire_seconds=600`
2. `sceneStr` 为 32 位随机串，写入本地缓存且不重复，有效期 600 秒（后续扫码事件可用）
3. `qrUrl` 为展示地址：`https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=TICKET`，其中 `TICKET` 已做 UrlEncode
4. 前端可将 `qrUrl` 直接作为图片地址展示，并用 `sceneStr` 轮询后续登录/绑定状态

---

## 4. 微信服务号消息与事件推送

| 项 | 说明 |
| --- | --- |
| 接口名称 | 微信服务号消息与事件推送 |
| 请求方式 | `GET`（URL 校验） / `POST`（事件接收） |
| 请求路径 | `/yuanzang/UserWechat/callback` |
| Content-Type | `text/xml`（POST body） |
| 微信后台配置 URL | `http(s)://{host}:{port}/yuanzang/UserWechat/callback` |
| Token | 与配置项 `wechat.token` 一致（默认 `yuanzang`） |
| 加解密方式 | 明文模式 |

### GET：验证服务器

| 参数名 | 位置 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- | --- |
| signature | Query | String | 是 | 微信加密签名 |
| timestamp | Query | String | 是 | 时间戳 |
| nonce | Query | String | 是 | 随机数 |
| echostr | Query | String | 是 | 随机字符串 |

校验通过时原样返回 `echostr` 纯文本；失败返回 `error`。

### POST：接收事件

监听扫描带参数二维码事件：

1. 已关注用户扫码：`Event=SCAN`，`EventKey` 为 `sceneStr`
2. 未关注用户扫码关注：`Event=subscribe`，`EventKey` 为 `qrscene_{sceneStr}`

若 `sceneStr` 存在于本地二维码缓存（`wechat:qr:scene:{sceneStr}`），则将 `sceneStr` 与用户 `openid`（`FromUserName`）写入本地缓存：

- key：`wechat:qr:scan:{sceneStr}`
- value：openid
- 有效期：60 秒

处理完成后返回纯文本 `success`。

### 业务说明

1. 微信服务器 5 秒内需收到响应，本接口快速返回 `success`
2. 当前按明文模式解析 XML；若后台改为安全模式，需另行接入加解密
3. 后续可用 `sceneStr` 从缓存 `wechat:qr:scan:{sceneStr}` 读取扫码用户 openid

---

## 5. 微信扫码登录

| 项 | 说明 |
| --- | --- |
| 接口名称 | 微信扫码登录 |
| 请求方式 | `GET` |
| 请求路径 | `/yuanzang/UserWechat/loginBySceneStr` |
| Content-Type | — |

### 请求参数

| 参数名 | 位置 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- | --- |
| sceneStr | Query | String | 是 | 获取二维码时返回的场景值 |

### 请求示例

```http
GET /yuanzang/UserWechat/loginBySceneStr?sceneStr=a1b2c3d4e5f6789012345678abcdef12 HTTP/1.1
Host: {host}:{port}
```

### 成功响应示例

```json
{
  "code": "0",
  "IsOk": true,
  "ErrorCode": "S",
  "Message": "登录成功",
  "data": {
    "status": "Success",
    "accesstoken": "ngTokenKey$...",
    "authorization": "ngTokenKey$...",
    "refreshtoken": "ngRefreshTokenKey$...",
    "userCode": "yangzq",
    "userId": "7700000000000002",
    "oname": "广东省第十一批援藏工作队",
    "ocode": "001"
  },
  "phid": ""
}
```

响应会同步设置与 `/rest/api/kernelsession` 相同的 Cookie（如 `LoginIdentity`、`i86.5.2tokenkey`、`loginTime`、`JCusSessionId`）。

### 失败响应示例

```json
{
  "code": "1",
  "IsOk": false,
  "ErrorCode": "E",
  "Message": "登录失败，请重新扫码",
  "data": ""
}
```

### 业务说明

1. 用 `sceneStr` 读取扫码缓存 `wechat:qr:scan:{sceneStr}` 得到 `openid`（有效期 60 秒）
2. 按 `openid` 查 `user_wechat` 得到 `username`
3. 调用 `/rest/api/kernelsession`，参数：
   - `esn`：配置项 `i8.esn`（如 `760833`）
   - `loginid`：username 经 DES（密钥 `d(3D0;Ia`，ECB/PKCS7）加密后的 hex
   - `ucode`：账套号 `i8.database` 同样 DES 加密后的 hex
   - `isapp`：固定 `0`
4. 将返回的 `Set-Cookie` 回写给前端
5. 登录相关根地址配置项：`i8.loginUrl`（生产示例：`https://gdyz.gd.gov.cn`）

---

## 6. 测试写入 sceneStr 缓存

| 项 | 说明 |
| --- | --- |
| 接口名称 | 测试写入sceneStr缓存 |
| 请求方式 | `GET` |
| 请求路径 | `/yuanzang/UserWechat/mockSceneCache` |
| Content-Type | — |

### 请求参数

| 参数名 | 位置 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- | --- |
| sceneStr | Query | String | 是 | 场景值 |
| openid | Query | String | 是 | 模拟扫码用户的 openid（需已在 user_wechat 绑定） |

### 请求示例

```http
GET /yuanzang/UserWechat/mockSceneCache?sceneStr=testscene001&openid=oXXXX-xxxxxxxx HTTP/1.1
Host: {host}:{port}
```

### 业务说明

1. 写入二维码场景缓存 `wechat:qr:scene:{sceneStr}`，有效期 600 秒
2. 写入扫码缓存 `wechat:qr:scan:{sceneStr} → openid`，有效期 60 秒
3. 随后可调用 `loginBySceneStr` 进行登录联调（无需真实扫码）

---

## 通用返回结构

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| code | String | `0` 成功，`1` 失败 |
| IsOk | Boolean | 是否成功 |
| ErrorCode | String | `S` 成功 / `E` 失败 |
| Message | String | 提示信息 |
| data | Object | 业务数据，成功时为 `UserWechat` 或 `{ qrUrl, sceneStr }` |
| phid | String | 预留字段 |

### UserWechat 字段

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | Long | 主键 |
| username | String | 用户账号 |
| openid | String | 微信 openid |
| insertDt | Date | 创建时间 |
| updateDt | Date | 更新时间 |
