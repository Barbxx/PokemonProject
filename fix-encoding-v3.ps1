# Script para corregir codificacion - Version 3
# Usa codigo hexadecimal para evitar problemas de encoding

Write-Host "=== Corrector de Codificacion UTF-8 ===" -ForegroundColor Cyan

$projectPath = "c:\ProyectosJava\PokemonProject\core\src\main\java\com\mypokemon\game"
$backupPath = "c:\ProyectosJava\PokemonProject\backup_$(Get-Date -Format 'yyyyMMdd_HHmmss')"

New-Item -ItemType Directory -Path $backupPath -Force | Out-Null
Write-Host "Backup creado en: $backupPath`n"

$files = Get-ChildItem -Path $projectPath -Filter "*.java" -Recurse
$modified = 0
$total = 0

foreach ($file in $files) {
    # Backup
    $rel = $file.FullName.Replace($projectPath, "")
    $bak = Join-Path $backupPath $rel
    $bakDir = Split-Path $bak -Parent
    if (!(Test-Path $bakDir)) { New-Item -ItemType Directory -Path $bakDir -Force | Out-Null }
    Copy-Item $file.FullName $bak -Force
    
    # Leer y procesar
    $bytes = [System.IO.File]::ReadAllBytes($file.FullName)
    $content = [System.Text.Encoding]::UTF8.GetString($bytes)
    $original = $content
    
    # Patrones y reemplazos - Parte 1: Combinaciones complejas
    $content = $content -replace 'ƒÆ\u0019‚¡', 'a'
    $content = $content -replace 'ƒÆ\u0019‚©', 'e'
    $content = $content -replace 'ƒÆ\u0019‚­', 'i'
    $content = $content -replace 'ƒÆ\u0019‚³', 'o'
    $content = $content -replace 'ƒÆ\u0019‚º', 'u'
    $content = $content -replace 'ƒÆ\u0019‚±', 'n'
    
    # Parte 2: Combinaciones con flecha
    $content = $content -replace 'ƒâ€š‚¡', '!'
    $content = $content -replace 'ƒâ€š‚¿', '?'
    $content = $content -replace 'ƒâ€°', 'E'
    $content = $content -replace 'ƒâ€œ', 'O'
    
    # Parte 3: Simples
    $content = $content -replace 'ƒÂ©', 'e'
    $content = $content -replace 'ƒÂ­', 'i'
    $content = $content -replace 'ƒÂ³', 'o'
    $content = $content -replace 'ƒÂº', 'u'
    $content = $content -replace 'ƒ¡', 'a'
    $content = $content -replace 'ƒÂ±', 'n'
    $content = $content -replace '‚¡', '!'
    $content = $content -replace '‚¿', '?'
    
    # Guardar si cambio
    if ($content -ne $original) {
        $utf8 = New-Object System.Text.UTF8Encoding $false
        [System.IO.File]::WriteAllText($file.FullName, $content, $utf8)
        $modified++
        Write-Host "OK: $($file.Name)" -ForegroundColor Green
    }
    $total++
}

Write-Host "`n=== RESUMEN ===" -ForegroundColor Cyan
Write-Host "Archivos revisados: $total"
Write-Host "Archivos corregidos: $modified" -ForegroundColor Green
Write-Host "`nNOTA: Los acentos se removieron para evitar problemas." -ForegroundColor Yellow
Write-Host "Los Spanish strings ahora usan letras sin acento (e, i, o, u, a, n)."
