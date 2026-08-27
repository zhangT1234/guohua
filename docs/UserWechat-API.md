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

## 通用返回结构

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| code | String | `0` 成功，`1` 失败 |
| IsOk | Boolean | 是否成功 |
| ErrorCode | String | `S` 成功 / `E` 失败 |
| Message | String | 提示信息 |
| data | Object | 业务数据，成功时为 `UserWechat` |
| phid | String | 预留字段 |

### UserWechat 字段

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | Long | 主键 |
| username | String | 用户账号 |
| openid | String | 微信 openid |
| insertDt | Date | 创建时间 |
| updateDt | Date | 更新时间 |
