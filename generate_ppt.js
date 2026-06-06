const pptxgen = require("pptxgenjs");
const path = require("path");
const React = require("react");
const ReactDOMServer = require("react-dom/server");
const sharp = require("sharp");
const {
  FaShieldAlt, FaSpider, FaCode, FaLock, FaBug, FaUserSecret,
  FaFileCode, FaChartBar, FaCogs, FaRocket, FaSearch, FaExclamationTriangle,
  FaDatabase, FaServer, FaCheckCircle, FaArrowRight, FaGlobe
} = require("react-icons/fa");

// ============ Icon helpers ============
function renderIconSvg(IconComponent, color = "#FFFFFF", size = 256) {
  return ReactDOMServer.renderToStaticMarkup(
    React.createElement(IconComponent, { color, size: String(size) })
  );
}

async function iconToBase64Png(IconComponent, color, size = 256) {
  const svg = renderIconSvg(IconComponent, color, size);
  const pngBuffer = await sharp(Buffer.from(svg)).png().toBuffer();
  return "image/png;base64," + pngBuffer.toString("base64");
}

// ============ Colors ============
const C = {
  bg: "0F1B2D",         // deep navy background
  bgLight: "162840",     // slightly lighter bg
  card: "1A2E45",        // card background
  cardHover: "1E3A5C",   // card border/hover
  accent: "00D4AA",      // cyber green accent
  accentDark: "00A884",  // darker green
  white: "FFFFFF",
  textLight: "B0C4DE",   // light steel blue text
  textMuted: "6B8299",   // muted text
  warn: "FF6B6B",        // warning red
  warnLight: "FF8E8E",   // light warning
  orange: "FFA726",      // orange
  blue: "4FC3F7",        // light blue
  purple: "CE93D8",      // light purple
};

// ============ Factory functions for reusable objects ============
const makeShadow = () => ({ type: "outer", blur: 4, offset: 2, color: "000000", opacity: 0.3 });

// ============ Build Presentation ============
async function build() {
  const pres = new pptxgen();
  pres.layout = "LAYOUT_16x9";
  pres.author = "冯科智";
  pres.title = "WebScanner - Web安全漏洞扫描系统";

  // Pre-render icons
  const icons = {
    shield: await iconToBase64Png(FaShieldAlt, "#" + C.accent),
    spider: await iconToBase64Png(FaSpider, "#" + C.accent),
    code: await iconToBase64Png(FaCode, "#" + C.accent),
    lock: await iconToBase64Png(FaLock, "#" + C.accent),
    bug: await iconToBase64Png(FaBug, "#" + C.accent),
    secret: await iconToBase64Png(FaUserSecret, "#" + C.accent),
    fileCode: await iconToBase64Png(FaFileCode, "#" + C.accent),
    chart: await iconToBase64Png(FaChartBar, "#" + C.accent),
    cogs: await iconToBase64Png(FaCogs, "#" + C.accent),
    rocket: await iconToBase64Png(FaRocket, "#" + C.accent),
    search: await iconToBase64Png(FaSearch, "#" + C.accent),
    exclaim: await iconToBase64Png(FaExclamationTriangle, "#" + C.accent),
    database: await iconToBase64Png(FaDatabase, "#" + C.accent),
    server: await iconToBase64Png(FaServer, "#" + C.accent),
    check: await iconToBase64Png(FaCheckCircle, "#" + C.accent),
    arrow: await iconToBase64Png(FaArrowRight, "#" + C.accent),
    globe: await iconToBase64Png(FaGlobe, "#" + C.accent),
  };

  // Screenshots base path
  const imgDir = "C:\\Users\\hp\\Desktop\\图片";
  const screenshots = {
    crawler: path.join(imgDir, "07126a7802ec3832f8ce52bf6db924d2.png"),
    report1: path.join(imgDir, "0a71fc974fdb2f141021adbcd844cd8f.png"),
    report2: path.join(imgDir, "4b34df1a67b82cad276b765933f5cfec.png"),
    scan1: path.join(imgDir, "4e0248d986a68c83f92349f76a3aa9bd.png"),
    sqli: path.join(imgDir, "6c771100089fbe5304d97c0e6f4e152e.png"),
    scan2: path.join(imgDir, "82eac9d71b2a7dccdce3427ad08db88c.png"),
    scan3: path.join(imgDir, "c4482e31948a1f52a451e7606563cd29.png"),
    scan4: path.join(imgDir, "ea4e69f162fe4d4d8f85e9a6a0405ca7.png"),
    scan5: path.join(imgDir, "fd360990380434c03bd383778492eaf8.png"),
  };

  // ============ Helper: add slide number ============
  function addSlideNumber(slide, num, total) {
    slide.addText(`${num} / ${total}`, {
      x: 8.8, y: 5.2, w: 1, h: 0.3,
      fontSize: 9, color: C.textMuted, align: "right", fontFace: "Calibri",
    });
  }

  // Helper: add accent line (left side)
  function addAccentBar(slide, x, y, h) {
    slide.addShape(pres.shapes.RECTANGLE, {
      x, y, w: 0.06, h,
      fill: { color: C.accent },
    });
  }

  // Helper: dark background
  function darkBg(slide) {
    slide.background = { color: C.bg };
  }

  // Helper: card with content
  function addCard(slide, x, y, w, h, opts = {}) {
    slide.addShape(pres.shapes.RECTANGLE, {
      x, y, w, h,
      fill: { color: opts.fill || C.card },
      shadow: makeShadow(),
    });
  }

  const TOTAL_SLIDES = 20;

  // ================================================================
  // SLIDE 1: 封面 (Cover)
  // ================================================================
  {
    const slide = pres.addSlide();
    slide.background = { color: C.bg };

    // Large decorative shape top-right
    slide.addShape(pres.shapes.RECTANGLE, {
      x: 5.5, y: -1, w: 5.5, h: 3,
      fill: { color: C.accent, transparency: 90 },
      rotate: -15,
    });

    // Accent line
    slide.addShape(pres.shapes.RECTANGLE, {
      x: 0.8, y: 1.5, w: 0.08, h: 2.2,
      fill: { color: C.accent },
    });

    // Shield icon
    slide.addImage({ data: icons.shield, x: 0.8, y: 0.6, w: 0.6, h: 0.6 });

    // Title
    slide.addText("WebScanner", {
      x: 1.2, y: 1.5, w: 7, h: 0.8,
      fontSize: 48, fontFace: "Arial Black", color: C.white, bold: true,
      margin: 0,
    });

    // Subtitle
    slide.addText("Web安全漏洞扫描系统", {
      x: 1.2, y: 2.3, w: 7, h: 0.5,
      fontSize: 24, fontFace: "Calibri", color: C.accent,
      margin: 0,
    });

    // Tagline
    slide.addText("全方位Web应用安全检测与防护平台", {
      x: 1.2, y: 2.9, w: 7, h: 0.4,
      fontSize: 14, fontFace: "Calibri", color: C.textLight, italic: true,
      margin: 0,
    });

    // Bottom info bar
    slide.addShape(pres.shapes.RECTANGLE, {
      x: 0, y: 4.8, w: 10, h: 0.825,
      fill: { color: C.card },
    });

    slide.addText([
      { text: "答辩人：冯科智", options: { breakLine: true } },
      { text: "技术栈：Spring 5.1.9 + MyBatis 3.5.2 + MySQL 8.0", options: { breakLine: true } },
      { text: "2026年6月", options: {} },
    ], {
      x: 0.8, y: 4.9, w: 8.4, h: 0.7,
      fontSize: 12, fontFace: "Calibri", color: C.textLight,
      lineSpacingMultiple: 1.3,
    });
  }

  // ================================================================
  // SL