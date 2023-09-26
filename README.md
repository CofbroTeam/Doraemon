# Doraemon
学习通签到助手

# 开发规范：
* 使用kt，命名统一使用驼峰命名法
* 所有kt文件需要以 `xxxActivity/xxxFragment` 命名，`xxx` 需要包含业务名称，比如登录页：`LoginActivity`。其中的 `xml` 文件里的 `id` 需要符合 `控件缩写_业务名称` 要求，比如 `TextView` 的 `id` : `tv_login`
* 保持代码可阅读性的前提下，尽量使代码行缩进减小
* 开发时，自己拉一条分支出来，一般命名为 `features/业务名_你的昵称`
* 提交代码，先提交到自己的分支，然后再提 `PR/Pull Request`
