# 桌面图标按功能区域排列脚本

$code = @'
using System;
using System.Runtime.InteropServices;

public class DesktopIconArranger {
    [DllImport("user32.dll", SetLastError = true)]
    public static extern IntPtr FindWindow(string lpClassName, string lpWindowName);
    
    [DllImport("user32.dll", SetLastError = true)]
    public static extern IntPtr FindWindowEx(IntPtr hwndParent, IntPtr hwndChildAfter, string lpszClass, string lpszWindow);
    
    [DllImport("user32.dll")]
    public static extern int SendMessage(IntPtr hWnd, uint Msg, IntPtr wParam, IntPtr lParam);
    
    public const int LVM_FIRST = 0x1000;
    public const int LVM_SETITEMPOSITION = LVM_FIRST + 15;
    public const int LVM_GETITEMTEXTW = LVM_FIRST + 75;
    
    [StructLayout(LayoutKind.Sequential, CharSet = CharSet.Unicode)]
    public struct LVITEMW {
        public int mask;
        public int iItem;
        public int iSubItem;
        public int state;
        public int stateMask;
        [MarshalAs(UnmanagedType.LPWStr)]
        public string pszText;
        public int cchTextMax;
    }
    
    public static IntPtr GetDesktopListView() {
        IntPtr h = FindWindowEx(IntPtr.Zero, IntPtr.Zero, "WorkerW", null);
        while (h != IntPtr.Zero) {
            IntPtr defView = FindWindowEx(h, IntPtr.Zero, "SHELLDLL_DefView", null);
            if (defView != IntPtr.Zero) {
                IntPtr listView = FindWindowEx(defView, IntPtr.Zero, "SysListView32", null);
                if (listView != IntPtr.Zero) return listView;
            }
            h = FindWindowEx(IntPtr.Zero, h, "WorkerW", null);
        }
        return IntPtr.Zero;
    }
    
    public static string GetItemText(IntPtr hWnd, int index) {
        LVITEMW item = new LVITEMW();
        item.iItem = index;
        item.cchTextMax = 260;
        item.pszText = new string(' ', 260);
        SendMessage(hWnd, LVM_GETITEMTEXTW, (IntPtr)index, ref item);
        return item.pszText.Trim();
    }
    
    public static bool SetIconPosition(IntPtr hWnd, int index, int x, int y) {
        IntPtr lParam = (IntPtr)((y << 16) | (x & 0xFFFF));
        int result = SendMessage(hWnd, LVM_SETITEMPOSITION, (IntPtr)index, lParam);
        return result != 0;
    }
}
'@

Add-Type -TypeDefinition $code

$desktop = [DesktopIconArranger]::GetDesktopListView()
if ($desktop -eq [IntPtr]::Zero) {
    Write-Host "错误：无法获取桌面列表视图"
    exit 1
}

Write-Host "桌面图标句柄：$desktop"

# 获取所有图标
$icons = @()
for ($i = 0; $i -lt 50; $i++) {
    $name = [DesktopIconArranger]::GetItemText($desktop, $i)
    if ($name) {
        $icons += @{Index=$i; Name=$name}
        Write-Host "[$i] $name"
    }
}

# 功能分组
$devTools = @("eclipse", "idea", "visual studio code", "vscode", "devcpp", "navicat", "plsqldev", "xshell", "apifox", "trae")
$browsers = @("chrome", "edge", "firefox", "safari")
$office = @("word", "excel", "powerpoint", "visio", "wps", "office")
$entertainment = @("抖音", "视频", "音乐")

function Get-Group {
    param($name)
    $lower = $name.ToLower()
    if ($devTools | Where-Object { $lower.Contains($_) }) { return "dev" }
    if ($browsers | Where-Object { $lower.Contains($_) }) { return "browser" }
    if ($office | Where-Object { $lower.Contains($_) }) { return "office" }
    if ($entertainment | Where-Object { $lower.Contains($_) }) { return "entertainment" }
    return "other"
}

$devIcons = $icons | Where-Object { (Get-Group $_.Name) -eq "dev" }
$browserIcons = $icons | Where-Object { (Get-Group $_.Name) -eq "browser" }
$officeIcons = $icons | Where-Object { (Get-Group $_.Name) -eq "office" }
$entertainmentIcons = $icons | Where-Object { (Get-Group $_.Name) -eq "entertainment" }
$otherIcons = $icons | Where-Object { (Get-Group $_.Name) -eq "other" }

Write-Host "`n分组结果:"
Write-Host "  开发工具：$($devIcons.Count)"
Write-Host "  浏览器：$($browserIcons.Count)"
Write-Host "  办公：$($officeIcons.Count)"
Write-Host "  影音：$($entertainmentIcons.Count)"
Write-Host "  其他：$($otherIcons.Count)"

$iconH = 85
$startX = 10
$startY = 10

# 开发工具 - 左上角第 1 列
Write-Host "`n排列开发工具..."
$y = $startY
foreach ($icon in $devIcons) {
    [DesktopIconArranger]::SetIconPosition($desktop, $icon.Index, $startX, $y)
    Write-Host "  $($icon.Name) -> ($startX, $y)"
    $y += $iconH
}

# 浏览器 - 第 2 列
Write-Host "`n排列浏览器..."
$col2 = $startX + 80
$y = $startY
foreach ($icon in $browserIcons) {
    [DesktopIconArranger]::SetIconPosition($desktop, $icon.Index, $col2, $y)
    Write-Host "  $($icon.Name) -> ($col2, $y)"
    $y += $iconH
}

# 办公 - 第 3 列
Write-Host "`n排列办公软件..."
$col3 = $col2 + 80
$y = $startY
foreach ($icon in $officeIcons) {
    [DesktopIconArranger]::SetIconPosition($desktop, $icon.Index, $col3, $y)
    Write-Host "  $($icon.Name) -> ($col3, $y)"
    $y += $iconH
}

# 影音娱乐 - 左下
Write-Host "`n排列影音娱乐..."
$y = $startY + 250
foreach ($icon in $entertainmentIcons) {
    [DesktopIconArranger]::SetIconPosition($desktop, $icon.Index, $startX, $y)
    Write-Host "  $($icon.Name) -> ($startX, $y)"
    $y += $iconH
}

# 其他工具 - 第 4 列
Write-Host "`n排列其他工具..."
$col4 = $col3 + 80
$y = $startY
foreach ($icon in $otherIcons) {
    [DesktopIconArranger]::SetIconPosition($desktop, $icon.Index, $col4, $y)
    Write-Host "  $($icon.Name) -> ($col4, $y)"
    $y += $iconH
}

Write-Host "`n排列完成！"
