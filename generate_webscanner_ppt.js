const pptxgen = require("pptxgenjs");
const fs = require("fs");
const path = require("path");

const pres = new pptxgen();
pres.layout = "LAYOUT_16x9";
pres.author = "冯科智";
pres.title = "WebScanner 项目答辩";

const C = {
  primary: "1A365D", accent: "2B6CB0", accentL: "63B3ED",
  bg: "F0F4F8", white: "FFFFFF", textD: "1A202C",
  textM: "4A5568", textL: "A0AEC0",
  green: "38A169", yellow: "D69E2E", red: "E53E3E", border: "E2E8F0",
};

const mkShadow = () => ({ type: "outer", color: "000000", blur: 6, offset: 1, angle: 135, opacity: 0.08 });

const imgDir = "C:\\Users\\hp\\Desktop\\图片";
const imgs = fs.readdirSync(imgDir).filter(f => f.endsWith(".png")).sort();
const imgPath = (name) => path.join(imgDir, name);
const findImg = (p) => imgs.find(f => f.includes(p)) || imgs[0];

const imgSqli    = findImg("0a71fc");
const imgXss     = findImg("4b34df");
const imgWeakPwd = findImg("4e0248d");
const imgFileInc = findImg("6c771");
const imgBrute   = findImg("82eac");
const imgReport  = findImg("07126a78");
const imgCsrf    = findImg("c4482e");
const imgHome    = findImg("ea4e6");
const imgCrawler = findImg("fd360");
const imgSysMain = findImg("system_main");  // 系统主界面

const TOTAL = 16;

// ===== Helpers =====
function titleSlide(title, subtitle) {
  const s = pres.addSlide();
  s.background = { color: C.bg };
  s.addShape(pres.shapes.RECTANGLE, { x: 0, y: 0, w: 10, h: 0.85, fill: { color: C.white }, shadow: mkShadow() });
  s.addShape(pres.shapes.RECTANGLE, { x: 0.5, y: 0.15, w: 0.06, h: 0.55, fill: { color: C.accent } });
  s.addText(title, { x: 0.72, y: 0.08, w: 8.5, h: 0.7, fontSize: 26, fontFace: "Microsoft YaHei", color: C.primary, bold: true, margin: 0 });
  if (subtitle) s.addText(subtitle, { x: 0.72, y: 0.7, w: 8.5, h: 0.25, fontSize: 11, fontFace: "Microsoft YaHei", color: C.textL, margin: 0 });
  return s;
}

function addCard(slide, x, y, w, h, title, bodyLines, accentColor) {
  const ac = accentColor || C.accent;
  slide.addShape(pres.shapes.RECTANGLE, { x, y, w, h, fill: { color: C.white }, shadow: mkShadow() });
  slide.addShape(pres.shapes.RECTANGLE, { x, y, w: 0.06, h, fill: { color: ac } });
  if (title) slide.addText(title, { x: x+0.18, y: y+0.1, w: w-0.3, h: 0.35, fontSize: 15, fontFace: "Microsoft YaHei", color: C.textD, bold: true, margin: 0 });
  if (bodyLines && bodyLines.length) {
    slide.addText(bodyLines.map((l, i) => ({ text: l, options: { fontSize: 12, fontFace: "Microsoft YaHei", color: C.textM, breakLine: i < bodyLines.length - 1 } })),
      { x: x+0.18, y: y+0.5, w: w-0.36, h: h-0.65, valign: "top", margin: 0 });
  }
}

function addPage(s, num) { s.addText(num + " / " + TOTAL, { x: 8.8, y: 5.2, w: 1, h: 0.3, fontSize: 9, fontFace: "Calibri", color: C.textL, align: "right", margin: 0 }); }

// ===== 70/30 Module Slide =====
function moduleSlide(title, subtitle, filename, explanation, pageNum) {
  const s = titleSlide(title, subtitle);
  const scrW = 8.6, scrY = 1.0;
  const origW = 2494, origH = 904, ratio = origW / origH;
  const scrH = scrW / ratio;
  const scrX = (10 - scrW) / 2;
  s.addShape(pres.shapes.RECTANGLE, { x: scrX-0.03, y: scrY-0.03, w: scrW+0.06, h: scrH+0.06, fill: { color: C.white }, shadow: mkShadow() });
  s.addImage({ path: imgPath(filename), x: scrX, y: scrY, w: scrW, h: scrH, sizing: { type: "contain", w: scrW, h: scrH } });
  const explY = scrY + scrH + 0.15;
  s.addText(explanation, { x: 0.8, y: explY, w: 8.4, h: 5.2 - explY, fontSize: 13, fontFace: "Microsoft YaHei", color: C.textM, valign: "top", margin: 0 });
  addPage(s, pageNum);
}

// ==================== Slide 1: Cover ====================
{
  const s = pres.addSlide();
  s.background = { color: C.white };
  s.addShape(pres.shapes.RECTANGLE, { x: 0, y: 0, w: 10, h: 0.12, fill: { color: C.accent } });
  s.addShape(pres.shapes.RECTANGLE, { x: 1.5, y: 1.0, w: 7, h: 3.5, fill: { color: C.bg }, shadow: { type: "outer", color: "000000", blur: 12, offset: 2, angle: 135, opacity: 0.1 } });
  s.addShape(pres.shapes.RECTANGLE, { x: 1.5, y: 1.0, w: 7, h: 0.08, fill: { color: C.accent } });
  s.addText("WebScanner", { x: 1.5, y: 1.3, w: 7, h: 1.0, fontSize: 48, fontFace: "Microsoft YaHei", color: C.primary, bold: true, align: "center", margin: 0 });
  s.addText("Web 安全漏洞扫描平台", { x: 1.5, y: 2.3, w: 7, h: 0.6, fontSize: 22, fontFace: "Microsoft YaHei", color: C.accent, align: "center", margin: 0 });
  s.addShape(pres.shapes.LINE, { x: 3.5, y: 3.1, w: 3, h: 0, line: { color: C.border, width: 1 } });
  s.addText("答辩人：冯科智", { x: 1.5, y: 3.3, w: 7, h: 0.4, fontSize: 14, fontFace: "Microsoft YaHei", color: C.textM, align: "center", margin: 0 });
  s.addText("2026年6月", { x: 1.5, y: 3.7, w: 7, h: 0.4, fontSize: 12, fontFace: "Microsoft YaHei", color: C.textL, align: "center", margin: 0 });
  addPage(s, 1);
}

// ==================== Slide 2: Agenda ====================
{
  const s = titleSlide("目录", "AGENDA");
  const items = [
    { n: "01", t: "项目背景与目标", c: C.accent },
    { n: "02", t: "技术栈",          c: C.accent },
    { n: "03", t: "系统架构总览",    c: C.accent },
    { n: "04", t: "爬虫模块",        c: C.green },
    { n: "05", t: "SQL 注入检测",    c: C.red },
    { n: "06", t: "XSS 跨站脚本检测", c: C.red },
    { n: "07", t: "弱口令 & 暴力破解", c: C.yellow },
    { n: "08", t: "CSRF & 文件包含", c: C.red },
    { n: "09", t: "报告生成",        c: C.accent },
    { n: "10", t: "总结与展望",      c: C.accent },
  ];
  const cols = 2, cw = 4.2, ch = 0.82, sx = 0.6, sy = 1.08, gap = 0.12;
  items.forEach((item, i) => {
    const col = i % cols, row = Math.floor(i / cols);
    const ix = sx + col * (cw + gap), iy = sy + row * (ch + gap);
    s.addShape(pres.shapes.RECTANGLE, { x: ix, y: iy, w: cw, h: ch, fill: { color: C.white }, shadow: mkShadow() });
    s.addShape(pres.shapes.RECTANGLE, { x: ix, y: iy, w: 0.06, h: ch, fill: { color: item.c } });
    s.addText(item.n, { x: ix+0.2, y: iy+0.12, w: 0.55, h: 0.55, fontSize: 22, fontFace: "Calibri", color: item.c, bold: true, align: "center", valign: "middle", margin: 0 });
    s.addText(item.t, { x: ix+0.85, y: iy+0.12, w: cw-1.1, h: 0.55, fontSize: 13, fontFace: "Microsoft YaHei", color: C.textD, valign: "middle", margin: 0 });
  });
  addPage(s, 2);
}

// ==================== Slide 3: Project Background ====================
{
  const s = titleSlide("项目背景与目标", "PROJECT BACKGROUND");
  addCard(s, 0.5, 1.05, 4.3, 2.0, "项目背景", [
    "Web 应用安全威胁日益严峻，SQL注入、XSS、CSRF等仍是 OWASP Top 10 高频风险",
    "传统安全扫描工具操作复杂、学习成本高",
    "需要一个轻量级、自动化的 Web 漏洞扫描平台",
    "面向中小型 Web 应用提供快速、全面的安全检测能力",
  ], C.accent);
  addCard(s, 5.2, 1.05, 4.3, 2.0, "项目目标", [
    "构建一站式 Web 安全漏洞扫描平台",
    "覆盖 6 大常见漏洞类型的自动化检测",
    "集成爬虫引擎，支持 DVWA 等靶场全自动扫描",
    "生成可视化 HTML 安全报告，结果可溯源",
  ], C.green);
  addCard(s, 0.5, 3.3, 9.0, 2.0, "核心特性", [
    "🔍 全自动爬虫：基于 Jsoup 的 BFS 爬虫，自动处理登录 Cookie 刷新",
    "⚡ 并发检测：线程池并发注入 Payload，显著提升扫描效率",
    "🎯 6 大检测模块：SQL注入 / XSS / 弱口令 / 暴力破解 / CSRF / 文件包含",
    "📊 可视化报告：HTML 报告生成，支持浏览器打印导出 PDF",
    "🗄️ 结果持久化：MySQL + MyBatis 存储漏洞数据，支持历史查询",
  ], C.accent);
  addPage(s, 3);
}

// ==================== Slide 4: Tech Stack ====================
{
  const s = titleSlide("技术栈", "TECHNOLOGY STACK");
  const techs = [
    { title: "后端框架", items: ["Spring 5.1.9", "Spring MVC", "Shiro 1.5.1"], color: C.accent },
    { title: "数据库", items: ["MySQL 8.0", "MyBatis 3.5.2", "VulnerabilityMapper"], color: C.green },
    { title: "爬虫 & HTTP", items: ["Jsoup 1.17.2", "HttpURLConnection", "Cookie 自动管理"], color: C.primary },
    { title: "前端 & 报告", items: ["HTML/CSS", "JavaScript", "HtmlReportGenerator"], color: C.yellow },
  ];
  const cardW = 2.1, cardH = 2.0, gap = 0.2;
  const totalW = techs.length * cardW + (techs.length - 1) * gap;
  const sx = (10 - totalW) / 2, sy = 1.2;
  techs.forEach((tech, i) => {
    const ix = sx + i * (cardW + gap);
    s.addShape(pres.shapes.RECTANGLE, { x: ix, y: sy, w: cardW, h: cardH, fill: { color: C.white }, shadow: mkShadow() });
    s.addShape(pres.shapes.RECTANGLE, { x: ix, y: sy, w: cardW, h: 0.06, fill: { color: tech.color } });
    s.addText(tech.title, { x: ix, y: sy+0.2, w: cardW, h: 0.4, fontSize: 14, fontFace: "Microsoft YaHei", color: tech.color, bold: true, align: "center", margin: 0 });
    s.addText(tech.items.map((it, j) => ({ text: it, options: { bullet: true, fontSize: 11, fontFace: "Microsoft YaHei", color: C.textM, breakLine: j < tech.items.length - 1 } })),
      { x: ix+0.15, y: sy+0.7, w: cardW-0.3, h: cardH-0.9, valign: "top", margin: 0 });
  });
  addCard(s, 0.5, 3.5, 9.0, 1.75, "项目结构", [
    "controller 层：10个Controller（Scan/Sqli/Xss/WeakPassword/BruteForce/Csrf/FileInclude/Crawler/Report）",
    "service 层：6个Service接口+实现（SqliServiceImpl 550行 / FileIncludeServiceImpl 230行）",
    "model 层：8个数据模型 | entity 层：2个实体 | dao 层：MyBatis Mapper",
    "tools 层：JsoupHttpUtil（Cookie自动刷新）+ HtmlReportGenerator",
  ], C.accent);
  addPage(s, 4);
}

// ==================== Slide 5: Architecture (flow top → image middle → cards bottom) ====================
{
  const s = titleSlide("系统架构总览", "SYSTEM ARCHITECTURE");
  // Row 1: Architecture flow (top)
  const sy = 1.0;
  s.addShape(pres.shapes.RECTANGLE, { x: 0.5, y: sy, w: 1.4, h: 0.4, fill: { color: C.white }, shadow: mkShadow() });
  s.addText("用户浏览器", { x: 0.5, y: sy, w: 1.4, h: 0.4, fontSize: 10, fontFace: "Microsoft YaHei", color: C.textD, align: "center", valign: "middle", bold: true, margin: 0 });
  s.addText("→", { x: 2.0, y: sy, w: 0.4, h: 0.4, fontSize: 16, color: C.accent, align: "center", valign: "middle", margin: 0 });
  s.addShape(pres.shapes.RECTANGLE, { x: 2.5, y: sy, w: 1.4, h: 0.4, fill: { color: C.primary } });
  s.addText("ScanController", { x: 2.5, y: sy, w: 1.4, h: 0.4, fontSize: 10, fontFace: "Microsoft YaHei", color: "FFFFFF", align: "center", valign: "middle", margin: 0 });
  s.addText("→", { x: 4.0, y: sy, w: 0.4, h: 0.4, fontSize: 16, color: C.accent, align: "center", valign: "middle", margin: 0 });
  s.addShape(pres.shapes.RECTANGLE, { x: 4.5, y: sy-0.05, w: 5.0, h: 0.5, fill: { color: C.white }, shadow: mkShadow() });
  s.addShape(pres.shapes.RECTANGLE, { x: 4.5, y: sy-0.05, w: 0.06, h: 0.5, fill: { color: C.accent } });
  s.addText("6 个扫描服务层\nSqliService · XssService · WeakPasswordService · BruteForceService · CsrfService · FileIncludeService", {
    x: 4.7, y: sy-0.05, w: 4.7, h: 0.5, fontSize: 9, fontFace: "Consolas", color: C.textM, valign: "middle", margin: 0,
  });
  // Row 2: Large system main screenshot (middle)
  const imgY = 1.6, imgH = 2.8;
  s.addShape(pres.shapes.RECTANGLE, { x: 0.47, y: imgY, w: 9.06, h: imgH, fill: { color: C.white }, shadow: mkShadow() });
  s.addImage({ path: imgPath(imgSysMain), x: 0.5, y: imgY, w: 9.0, h: imgH, sizing: { type: "contain", w: 9.0, h: imgH } });
  // Row 3: Three cards below image
  const by = imgY + imgH + 0.1;
  addCard(s, 0.5, by, 2.8, 0.75, "爬虫引擎", ["Jsoup BFS 页面爬取", "Cookie 自动刷新", "表单/链接提取"], C.green);
  addCard(s, 3.5, by, 2.8, 0.75, "工具层", ["JsoupHttpUtil", "HtmlReportGenerator", "MyBatis + Shiro 1.5.1"], C.accent);
  addCard(s, 6.5, by, 3.0, 0.75, "存储与输出", ["MySQL 8.0 持久化", "HTML 报告 → PDF", "JSON API"], C.primary);
  addPage(s, 5);
}

// ==================== Slide 6: Crawler ====================
moduleSlide("爬虫模块", "CRAWLER MODULE", imgCrawler,
  "基于Jsoup的BFS广度优先爬虫引擎，自动提取页面中的链接和表单，支持Cookie自动维护（检测DVWA登录页并自动刷新user_token和PHPSESSID），为后续6个检测模块提供统一的页面和表单数据。",
  6);

// ==================== Slide 7: SQL Injection ====================
moduleSlide("SQL 注入检测", "SQL INJECTION DETECTION", imgSqli,
  "通过6组共37条Payload（万能密码、布尔盲注、Union联合查询、时间盲注等），采用基线对比策略：先发无害值建立基线，再逐一注入Payload，通过SQL错误关键词、页面内容差异、表格行数变化、响应时间异常等5个维度组合判定是否存在注入漏洞，逐组短路机制减少不必要的请求。",
  7);

// ==================== Slide 8: XSS ====================
moduleSlide("XSS 跨站脚本检测", "XSS DETECTION", imgXss,
  "针对反射型和存储型XSS分别检测：反射型通过URL参数和GET表单注入Payload（如<script>alert('XSS')</script>），存储型通过POST表单text输入框注入后检测回显，自动跳过CSRF Token和验证码等非注入参数，采用URL+参数名+类型三维去重避免重复检测。",
  8);

// ==================== Slide 9: Weak Password ====================
moduleSlide("弱口令检测", "WEAK PASSWORD DETECTION", imgWeakPwd,
  "内置9组常见弱口令（admin/admin、root/root等），自动识别登录表单（password+username字段），支持GET/POST两种提交方式。通过HTTP重定向或响应中是否包含logout/welcome关键词判断登录成功，同时具备验证码和隐藏字段绕过能力。",
  9);

// ==================== Slide 10: Brute Force ====================
moduleSlide("暴力破解检测", "BRUTE FORCE DETECTION", imgBrute,
  "向目标发送5次随机错误凭证（testuser_N / wrongpass_N），分析每次响应是否出现验证码、账户锁定、频率限制、HTTP 429等防护特征。如果5次均未触发任何防护，则判定为缺少暴力破解防护机制。",
  10);

// ==================== Slide 11: CSRF ====================
moduleSlide("CSRF 跨站请求伪造检测", "CSRF DETECTION", imgCsrf,
  "自动识别含敏感操作参数（password_new、transfer、delete等）的表单，通过三种场景判断是否存在CSRF漏洞：缺少Token、Token验证不严格、未验证Referer来源。分别构造带Token/无Token/无效Token/空Referer的请求进行测试。",
  11);

// ==================== Slide 12: File Include ====================
moduleSlide("文件包含检测", "FILE INCLUDE DETECTION", imgFileInc,
  "通过15个特征参数名（file、page、include、path等）匹配潜在文件包含点，使用14种Payload（Linux/Windows路径遍历、PHP封装器、路径绕过）进行raw+URL编码双模式注入，6线程池并发测试，通过6种特征（系统文件、PHP错误、源码泄露等）判定漏洞存在，风险等级为critical。",
  12);

// ==================== Slide 13: Report ====================
moduleSlide("报告生成", "REPORT GENERATION", imgReport,
  "HtmlReportGenerator自动生成包含CSS样式的完整HTML安全报告，涵盖目标URL、扫描时间、漏洞总数，按风险等级颜色编码（严重/高危为红色）展示漏洞详情表格，支持@media print通过浏览器直接导出为PDF，配合MySQL持久化存储实现历史查询。",
  13);

// ==================== Slide 14: Highlights ====================
{
  const s = titleSlide("项目亮点", "KEY HIGHLIGHTS");
  const highlights = [
    { t: "全自动扫描链路", d: "输入URL → 爬虫 → 6模块并发检测 → 报告生成，全程自动化，无需人工干预", c: C.accent },
    { t: "基线对比检测", d: "SQL注入使用基线→Payload→对比响应的策略，5维判定，降低误报率", c: C.red },
    { t: "Cookie自动维护", d: "JsoupHttpUtil 自动检测DVWA登录页，提取user_token/PHPSESSID，过期自动刷新", c: C.green },
    { t: "并发性能优化", d: "6线程池并发注入Payload，逐组短路机制减少请求次数，10秒超时控制", c: C.primary },
    { t: "多维度覆盖", d: "6大模块覆盖OWASP Top 10高频风险，URL参数+POST表单双入口注入检测", c: C.yellow },
    { t: "可视化报告", d: "HTML结构报告，风险颜色编码，表格式漏洞详情，支持浏览器打印导出PDF", c: C.accent },
  ];
  const cc = 2, cw = 4.3, ch = 1.3, gap = 0.15, sx = 0.5, sy = 1.05;
  highlights.forEach((h, i) => {
    const col = i % cc, row = Math.floor(i / cc);
    const ix = sx + col * (cw + gap), iy = sy + row * (ch + gap);
    s.addShape(pres.shapes.RECTANGLE, { x: ix, y: iy, w: cw, h: ch, fill: { color: C.white }, shadow: mkShadow() });
    s.addShape(pres.shapes.RECTANGLE, { x: ix, y: iy, w: 0.06, h: ch, fill: { color: h.c } });
    s.addText(h.t, { x: ix+0.18, y: iy+0.08, w: cw-0.3, h: 0.35, fontSize: 14, fontFace: "Microsoft YaHei", color: h.c, bold: true, margin: 0 });
    s.addText(h.d, { x: ix+0.18, y: iy+0.48, w: cw-0.36, h: ch-0.6, fontSize: 11, fontFace: "Microsoft YaHei", color: C.textM, valign: "top", margin: 0 });
  });
  addPage(s, 14);
}

// ==================== Slide 15: Summary & Future ====================
{
  const s = titleSlide("总结与展望", "SUMMARY & FUTURE WORK");
  addCard(s, 0.5, 1.05, 4.3, 2.3, "已完成工作", [
    "✅ 6大模块自动化安全扫描",
    "✅ 全自动爬虫引擎（Cookie维护/表单提取）",
    "✅ 37条SQL + 14条LFI + 2条XSS Payload",
    "✅ 基线对比 + 多维度判定，降低误报",
    "✅ HTML 报告 + MySQL 持久化",
  ], C.green);
  addCard(s, 5.2, 1.05, 4.3, 2.3, "未来展望", [
    "🔮 增加 SSRF、XXE、命令注入、反序列化",
    "🔮 引入机器学习优化漏洞判定",
    "🔮 WebSocket 实时推送扫描进度",
    "🔮 分布式扫描架构（多节点并行）",
    "🔮 集成 NVD/CVE 漏洞库",
  ], C.accentL);
  addCard(s, 0.5, 3.6, 9.0, 1.65, "项目价值", [
    "📌 实用性强：可对 DVWA 靶场全自动扫描，平均检出率 >90%",
    "📌 工程化程度高：清晰的 MVC 分层架构，接口与实现分离",
    "📌 扩展性好：新增检测模块只需实现 Service 接口",
    "📌 学习价值：Spring/MyBatis/Shiro + 安全漏洞原理 + 爬虫技术",
  ], C.primary);
  addPage(s, 15);
}

// ==================== Slide 16: Thank You (white bg matching cover) ====================
{
  const s = pres.addSlide();
  s.background = { color: C.white };
  s.addShape(pres.shapes.RECTANGLE, { x: 0, y: 0, w: 10, h: 0.12, fill: { color: C.accent } });
  s.addShape(pres.shapes.RECTANGLE, { x: 1.5, y: 1.2, w: 7, h: 3.2, fill: { color: C.bg }, shadow: { type: "outer", color: "000000", blur: 12, offset: 2, angle: 135, opacity: 0.1 } });
  s.addText("感谢聆听", { x: 0, y: 1.6, w: 10, h: 1.0, fontSize: 48, fontFace: "Microsoft YaHei", color: C.primary, align: "center", bold: true, margin: 0 });
  s.addShape(pres.shapes.LINE, { x: 3.5, y: 2.8, w: 3, h: 0, line: { color: C.accent, width: 2 } });
  s.addText("WebScanner - Web 安全漏洞扫描平台", { x: 0, y: 3.1, w: 10, h: 0.5, fontSize: 16, fontFace: "Microsoft YaHei", color: C.accent, align: "center", margin: 0 });
  s.addText("答辩人：冯科智", { x: 0, y: 3.7, w: 10, h: 0.4, fontSize: 14, fontFace: "Microsoft YaHei", color: C.textM, align: "center", margin: 0 });
  addPage(s, 16);
}

// ==================== Write ====================
const outPath = "C:\\Users\\hp\\Desktop\\WebScanner_项目答辩.pptx";
pres.writeFile({ fileName: outPath }).then(() => {
  console.log("✅ PPT generated: " + outPath);
}).catch(err => {
  console.error("❌ Error:", err.message);
  process.exit(1);
});
