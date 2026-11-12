# Win12 TV Desktop

**Windows 12 风格 Android TV 启动器 + 虚拟鼠标**

专为 Android TV / 智能电视盒子设计的桌面启动器。通过内置虚拟光标系统，用普通遥控器也能流畅点击、拖动和操作界面。

[English](#english) | [中文](#中文)

---

## 中文

### ✨ 核心功能

- **Fluent Design 桌面**：动态渐变壁纸、居中任务栏 (Dock)、开始菜单、多主题切换
- **智能虚拟鼠标**：
  - 方向键平滑移动光标（长按加速）
  - 确认键点击 + 波纹反馈，可穿透触发底层控件与网页
  - 支持按住拖动 (Drag & Drop)
  - 菜单键切换滚动模式
- **桌面小组件**：时钟、天气 / 硬件状态监控（内存、CPU）
- **家长控制**：简易锁屏（默认密码 `1234`）
- **开机自启**：`BootReceiver` 支持

### 📦 项目结构

```
Win12TvLauncher/
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       └── java/com/win12/tvlauncher/
│           ├── MainActivity.kt          # 主界面与窗口管理
│           ├── CursorOverlayView.kt     # 虚拟光标
│           ├── DesktopWidgetsView.kt    # 桌面小组件
│           ├── LockScreenManager.kt     # 锁屏
│           ├── ThemeManager.kt          # 主题
│           └── BootReceiver.kt          # 开机启动
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

### 🚀 编译与安装

1. 用 **Android Studio** 打开项目根目录
2. 连接 Android TV 设备或启动模拟器（建议 API 21+）
3. `Build → Build APK(s)` 生成 APK
4. 侧载安装到电视
5. 按遥控器 **Home** 键，选择本应用设为默认桌面

> **注意**：当前版本为源码原型，缺少部分资源文件（图标、styles 等）。首次打开可能需要在 Android Studio 中补充 `res/` 目录或让 IDE 自动生成基础资源。

### ⚠️ 已知限制

- 默认锁屏密码为 `1234`，正式使用前请修改
- 虚拟鼠标依赖系统注入触摸事件，部分系统可能有权限限制
- 天气等需要网络权限的功能需自行完善数据源

---

## English

### Features

- Fluent Design desktop for Android TV (gradient wallpaper, centered dock, start menu, themes)
- Virtual mouse controlled by D-pad (acceleration, click ripple, drag & drop, scroll mode)
- Desktop widgets (clock, basic system stats)
- Simple PIN lock screen (default `1234`)
- Auto-start on boot

### Build & Install

1. Open the project in **Android Studio**
2. Connect an Android TV device or emulator (API 21+)
3. Build → Build APK(s)
4. Sideload the APK
5. Press **Home** on the remote and set this app as the default launcher

### License

This project is provided as-is for learning and personal use. Feel free to fork and improve.

---

**Repository**: [yxing7249-cmd/Win](https://github.com/yxing7249-cmd/Win)
