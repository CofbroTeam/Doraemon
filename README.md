# Doraemon

<div align="center"> <img src="https://github.com/CofbroTeam/Doraemon/assets/101956946/aa7e4302-59c6-4184-8390-d1022f9b2b2a" width = 300 height = 300 /> </div>

 # 

<div align="center" style="font-weight:bold"><b>学习通智能签到助手 - 还你一个不被签到问题打扰、认真学习的梦</b></div>

## 各级页面
|  页面名称   | 页面图片  |
|  ----  | ----  |
| `登录页`  | <div align="center"> <img src="https://github.com/CofbroTeam/Doraemon/assets/101956946/31bf4fd1-e112-4082-8bf6-1d8fe89dde0c" width = 350 height = 800 /> </div> |
| `课程列表页`  | <div align="center"> <img src="https://github.com/CofbroTeam/Doraemon/assets/101956946/f92fccc3-bdea-4a7f-b3a2-b6ae262ee158" width = 350 height = 800 /> </div><div align="center"> <img src="https://github.com/CofbroTeam/Doraemon/assets/101956946/8574a7c0-5f15-475c-82c3-d747b2e81aa5" width = 350 height = 800 /> </div>|
| `签到列表页`  | <div align="center"> <img src="https://github.com/CofbroTeam/Doraemon/assets/101956946/27c5d615-a85e-4bf7-b1bf-a3b1b0b849e8" width = 350 height = 800 /> </div> |
| `定位签到页`  | <div align="center"> <img src="https://github.com/CofbroTeam/Doraemon/assets/101956946/71e699c8-b6a7-4e4e-9d0b-a2caadbfad85" width = 350 height = 800 /> </div> |

## 特点
👏 `Doraemon` 拥有清晰明了的 `UI` 界面以及易于操作的特点。除此之外，还具备：

☑️ 普通签到  

☑️ 签到码签到

☑️ 手势签到

☑️ 定位签到：修改任意经纬度

☑️  图片签到：相册上传任意图片

☑️  扫码签到：只能老老实实扫码，目前暂时无法越过服务端拿到 `enc`，不过后面支持一人扫码，多人签到的功能

- [ ] 作业通知：push通知，短信通知！
- [ ] 应用挂后台，自动侦测签到！
- [ ] 二维码定位签到！
- [ ] 好友系统！
- [ ] 绑定好友，一键全签。一个宿舍以后上课只去一人！
- [ ] 作业一键同步给所有好友：同步作业给好友成功，经好友同意，好友自动完成作业提交！
- [ ] ...

由于开发人手不足，项目所有功能预计11月底完成开发，欢迎感兴趣的同学一起参与进来。

## 如何贡献你的代码 & 提出宝贵的建议
* 贡献代码：`Fork` -> `Pull Request`，或者直接联系我，将您添加为项目成员。
* 建议：首先提出 `issue`，描述您的问题，如果有 `ui` 或者 `功能` 方面的建议欢迎配上简单明了的图文。

## 使用
目前项目还处于开发阶段，如果您想尝鲜已开发的功能或者迫切需要使用 `签到功能` ，请在 `issue` 中告诉我们。

### `1.链接` 
[尝鲜版请点击这里](https://raw.githubusercontent.com/CofbroTeam/Doraemon/main/app.apk)。由于项目处于开发阶段，并且我们没有 `测试同学`，因此如果大家在使用过程中遇到 `bug`，或者对本项目有更好的 `需求和建议`，烦请在 `issue` 中提出，以便让我们的 `app` 更加完善。

### `2.QR-Code:` 
请使用浏览器或手机自带的二维码扫描器扫描。
<div> <img src="https://github.com/CofbroTeam/Doraemon/assets/101956946/60125d13-97d2-4515-a3e4-6460ac97c9d0" width = 200 height = 200 /> </div>


## 开发规范：
* 使用kt或者java，命名统一使用驼峰命名法
* 所有kt/java文件需要以 `xxxActivity/xxxFragment` 命名，`xxx` 需要包含业务名称，比如登录页：`LoginActivity`。其中的 `xml` 文件里的 `id` 需要符合 `控件缩写_业务名称` 要求，比如 `TextView` 的 `id` : `tv_login`
* 保持代码可阅读性的前提下，尽量使代码行缩进减小
* 开发时，自己拉一条分支出来，一般命名为 `features/业务名_你的昵称`
* 提交代码，先提交到自己的分支，然后再提 `PR/Pull Request`

## 免责声明
本项目仅作为交流学习使用，通过本项目加深网络通信、接口编写、交互设计等方面知识的理解，请勿用作商业用途，任何人或组织使用项目中代码进行的任何违法行为与本人无关。如有触及相关平台规定或者权益，烦请联系我们删除。
