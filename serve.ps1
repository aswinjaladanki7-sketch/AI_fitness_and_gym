# ============================================================
# AI GYM & FITNESS ASSISTANT - Built-in Zero-Dependency Web Server
# Runs using pure Windows PowerShell & System.Net.HttpListener
# Port: 8080 (or custom via -Port)
# ============================================================

param (
    [int]$Port = 8080,
    [string]$Directory = "$PSScriptRoot\AI-GYM-FITNESS\frontend"
)

$listener = New-Object System.Net.HttpListener
$prefix = "http://localhost:$Port/"
$listener.Prefixes.Add($prefix)

try {
    $listener.Start()
    Write-Host ""
    Write-Host "=======================================================" -ForegroundColor Cyan
    Write-Host "  AI GYM & FITNESS ASSISTANT - LOCAL WEB SERVER" -ForegroundColor Green
    Write-Host "  SRM INSTITUTE OF SCIENCE AND TECHNOLOGY" -ForegroundColor Yellow
    Write-Host "  Server URL: http://localhost:$Port" -ForegroundColor Green
    Write-Host "  Serving:    $Directory" -ForegroundColor Gray
    Write-Host "  Press Ctrl+C to stop the server" -ForegroundColor Gray
    Write-Host "=======================================================" -ForegroundColor Cyan
    Write-Host ""
} catch {
    Write-Error "Could not start listener: $_"
    exit 1
}

$mimeTypes = @{
    ".html" = "text/html; charset=utf-8"
    ".css"  = "text/css; charset=utf-8"
    ".js"   = "application/javascript; charset=utf-8"
    ".json" = "application/json; charset=utf-8"
    ".png"  = "image/png"
    ".jpg"  = "image/jpeg"
    ".svg"  = "image/svg+xml"
    ".ico"   = "image/x-icon"
    ".webp"  = "image/webp"
    ".mp4"   = "video/mp4"
    ".webm"  = "video/webm"
    ".woff2" = "font/woff2"
    ".woff"  = "font/woff"
    ".ttf"   = "font/ttf"
}

while ($listener.IsListening) {
    $context = $null
    try {
        $context = $listener.GetContext()
        $request = $context.Request
        $response = $context.Response

        # Add CORS Headers
        $response.AddHeader("Access-Control-Allow-Origin", "*")
        $response.AddHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
        $response.AddHeader("Access-Control-Allow-Headers", "*")

        if ($request.HttpMethod -eq "OPTIONS") {
            $response.StatusCode = 200
            $response.Close()
            continue
        }

        $urlPath = $request.Url.LocalPath.TrimStart('/')
        if ([string]::IsNullOrEmpty($urlPath) -or $urlPath -eq "/") {
            $urlPath = "index.html"
        }

        $filePath = Join-Path $Directory $urlPath
        if (-not (Test-Path $filePath -PathType Leaf)) {
            $filePath = Join-Path $Directory "index.html"
        }

        if (Test-Path $filePath -PathType Leaf) {
            $ext = [System.IO.Path]::GetExtension($filePath).ToLower()
            $contentType = if ($mimeTypes.ContainsKey($ext)) { $mimeTypes[$ext] } else { "application/octet-stream" }

            $bytes = [System.IO.File]::ReadAllBytes($filePath)
            $response.ContentType = $contentType
            $response.ContentLength64 = $bytes.Length
            $response.OutputStream.Write($bytes, 0, $bytes.Length)
            $response.StatusCode = 200
        } else {
            $response.StatusCode = 404
        }
        $response.Close()
    } catch {
        if ($context -and $context.Response) {
            try { $context.Response.Close() } catch {}
        }
        if (-not $listener.IsListening) {
            break
        }
    }
}
