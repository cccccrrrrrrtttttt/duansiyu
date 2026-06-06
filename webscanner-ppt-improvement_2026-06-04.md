# WebScanner 答辩PPT改进

## 任务目标
基于 D:\IDEA\Project\WebScanner 项目源码和原有 WebScanner使用说明文档.docx、WebScanner_项目答辩_v3.pptx，制作改进版答辩PPT。

## 执行过程

### 1. 信息收集
- 读取了项目核心源码：SecurityController.java（完整的40+ SQL Payload、20+ XSS Payload、基线对比检测法）、MyUrl.java（HTTP请求工具）、HtmlParser.java（HTML解析）、README.md
- 用 python-docx 提取了说明文档内容（含项目架构、模块详情、API文档、FAQ等）
- 用 python-pptx 提取了原v3 PPT的11页内容

### 2. 项目分析
WebScanner 是一个完整的 Java Web 安全漏洞扫描系统：
- 技术栈：Spring 5.1.9 + Spring MVC + MyBatis 3.5.2 + MySQL 8.0
- 前端：JSP + Vanilla JavaScript
- 核心功能：SQL注入检测（6类Payload）、XSS检测（4种攻击面）、网页扫描、HTML解析、综合扫描、漏洞演示、检测报告
- 创新点：基线对比检测法（先发无害值建基线，再对比响应差异，从5个维度综合判定）
- 双通道数据持久化（Session + localStorage）

### 3. PPT制作
使用 pptxgenjs 从零创建了全新的 16 页答辩PPT（v4版）：
- 配色方案：深色主题（深蓝#0A1628背景）+ 青蓝色(#00B4D8)强调 + 白底浅灰(#F0F4F8)交替
- 结构：封面 → 目录 → 项目背景与目标 → 技术栈 → 系统架构 → 功能模块总览 → SQL注入检测 → XSS检测 → 基线对比检测法（核心创新） → URL扫描与HTML解析 → REST API设计 → 漏洞演示与安全教育 → 检测报告与数据持久化 → 项目难点与解决方案 → 总结与展望 → 致谢

## 输出文件
- C:\Users\hp\Desktop\WebScanner_项目答辩_v4.pptx（改进版答辩PPT）
