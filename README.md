# Slay the Spire Richer Presence Mod

一个为《杀戮尖塔》游戏设计的丰富状态显示Mod，可以在Steam上显示更详细的游戏状态信息。

> **致谢**：本项目基于Somdy的原始代码进行改进和优化，感谢Somdy的开源贡献。

## 功能特性

### 🎮 游戏状态显示
- **角色信息**：显示当前使用的角色和游戏模式
- **楼层进度**：显示当前所在楼层和章节
- **战斗状态**：显示正在战斗的怪物名称
- **事件状态**：显示当前遇到的事件名称
- **卡牌升级**：显示重要的卡牌升级信息

### 🌍 多语言支持
- 支持中文（简体/繁体）和英文
- 自动根据游戏语言设置切换显示语言

### 🔧 平台兼容
- **Steam**：在Steam好友列表中显示详细状态

## 安装方法

### 前置要求
- [ModTheSpire](https://github.com/kiooeht/ModTheSpire)
- [BaseMod](https://github.com/daviscook477/BaseMod)
- [Lazy Man's Kits](https://github.com/Somdy/LazyManKits)

### 安装步骤
1. 确保已安装前置Mod
2. 下载本Mod的jar文件
3. 将jar文件放入Slay the Spire的mods文件夹
4. 启动游戏时通过ModTheSpire加载本Mod

## 开发说明

### 项目结构
```
src/main/java/rs/richerpresence/
├── character/          # 角色相关状态显示
├── core/               # 核心功能模块
├── patches/            # 游戏补丁
└── utils/              # 工具类
```

### 构建方法
```bash
mvn clean package
```

### 主要类说明
- **Presenter**：主控制器，处理状态更新和分发
- **RichPresenceUpdater**：状态信息更新器
- **CharacterRichPresenceProxy**：角色状态显示代理
- **RichPresenceDistributor**：状态信息分发器


## 配置说明

### 本地化文件
Mod支持多语言，语言文件位于：
```
src/main/resources/RPAssets/locals/
├── eng/ui.json    # 英文
└── zhs/ui.json    # 简体中文
```


### 日志查看
Mod会在游戏日志中输出调试信息，可以通过查看日志来排查问题。

## 贡献指南

欢迎提交Issue和Pull Request来改进这个Mod！

## 许可证

本项目采用MIT许可证，详见LICENSE文件。

