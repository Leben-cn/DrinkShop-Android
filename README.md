# 🥤 DrinkShop - Android Client (MVP + Multi-Module)

> 一个基于 Android (Java) 开发的组件化饮品店点餐 App。
> 采用 **多模块 (Multi-Module)** + **MVP** 架构设计，高度封装基类与通用控件。
>
> 🔗 **后端仓库地址**: [DrinkShop-Backend](https://github.com/Leben-cn/DrinkShop-Backend)

## 📱 项目简介

DrinkShop 是一个模拟真实饮品店点餐流程的安卓应用。它演示了一个完整的电商/点餐业务闭环，包括商品浏览、双列表联动菜单、规格选择、购物车管理、订单流程以及个人中心等功能。

本项目重点在于**架构设计**与**基类封装**，通过组件化开发模式解耦业务，适合作为 Android 进阶、架构设计参考或课程设计项目。

## 🛠️ 技术架构 (Architecture)

本项目采用 **多模块组件化** 开发模式，项目结构清晰：

### 1. 模块划分 (Modules)
* **`app`**: 壳工程，负责集成各业务模块，应用入口。
* **`base`**: **核心基础库**。包含所有基类封装、网络框架、通用控件、工具类。不依赖任何业务模块。
* **`common`**: 公共业务层。包含跨模块通用的实体类 (Bean)、常量、路由服务等。
* **`user`**: 用户业务模块（登录、注册、个人中心、地址管理）。
* **`shop`**: 点餐业务模块（菜单列表、商品详情、购物车、订单提交）。
* **`merchant`**: 商家端功能模块。

### 2. 核心封装 (Base Layer Highlights) ✨

`base` 模块实现了高度的抽象和复用，极大地提高了开发效率：

#### 🏛️ MVP 架构支持 (`BaseActivity` / `BaseFragment`)
* **自动注入 Presenter**: 使用 `@InjectPresenter` 注解，无需手动 new Presenter。
* **生命周期管理 (`LifecycleManage`)**: 自动分发生命周期给 Controller 和 Presenter，有效防止内存泄漏。
* **View 注入**: 虽然 `@BindView` 已废弃，但架构保留了扩展性，支持自动注入 View。

#### 📄 列表页面自动化 (`BaseRecyclerActivity`)
* **自动初始化**: 内置 RecyclerView 初始化逻辑，减少重复代码。
* **Adapter 管理**: 自动管理 Adapter 的创建与绑定。
* **缺省页管理 (`StateController`)**: 自动切换“加载中”、“空数据”、“网络错误”等状态页面，提升用户体验。

#### 🔍 搜索通用基类 (`BaseSearchActivity`)
* **模板方法模式**: 严格遵守 BaseActivity 的模板方法模式。
* **规范流程**: 统一管理搜索历史、搜索结果展示与清除逻辑。

#### 📑 标签页通用基类 (`BaseTabActivity`)
* **快速搭建**: 通用的 TabLayout + ViewPager 页面基类，支持快速构建顶部/底部导航栏。

#### 🧩 通用适配器 (`BaseRecyclerAdapter` & `BaseViewHolder`)
* **链式调用**: 封装了 `BaseViewHolder`，支持链式调用设置数据。
* **简化开发**: 避免每次编写 Adapter 时都要写内部类 ViewHolder，大幅减少样板代码。

### 3. 自定义控件 (Custom Widgets) 🎨

#### 🔗 双列表联动控件 (`LinkageView`)
* 实现了类似“美团/饿了么”点餐页面的效果。
* 左侧分类列表与右侧商品列表双向滑动联动，交互流畅。

#### 💬 通用弹窗 (`BaseDialog` / `BaseBottomSheetDialog`)
* **通用中心弹窗**: 处理了屏幕宽度适配、背景透明度等通用逻辑。
* **通用底部弹窗**: 支持从底部滑出，用于规格选择、购物车详情等场景。

## 🚀 快速开始 (Getting Started)

1.  **环境准备**
    * Android Studio Ladybug 或更高版本
    * JDK 17+
    * Gradle 8.0+

2.  **克隆项目**
    ```bash
    git clone [https://github.com/Leben-cn/DrinkShop-Android.git](https://github.com/Leben-cn/DrinkShop-Android.git)
    ```

3.  **配置后端**
    * 本项目依赖后端服务，请先启动 [DrinkShop-Backend](https://github.com/Leben-cn/DrinkShop-Backend)。
    * 修改 `common` 模块或 `app` 模块中的 `BASE_URL` 配置，指向你的本地后端 IP 地址。

4.  **运行**
    * Sync Project with Gradle Files.
    * 选择 `app` 模块运行即可。

## 👤 作者 (Author)

* **Developer**: [Leben-cn](https://github.com/Leben-cn)
* **Created by**: youjiahui
* **Date**: 2026

---

**如果觉得这个项目对你有帮助，欢迎点个 Star ⭐️ !**
