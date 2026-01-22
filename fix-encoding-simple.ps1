# Script para corregir codificacion UTF-8 en archivos Java
# Version simplificada que evita problemas de encoding en el script mismo

Write-Host "=== Corrector de Codificacion UTF-8 para Proyecto Pokemon ===" -ForegroundColor Cyan
Write-Host ""

$projectPath = "c:\ProyectosJava\PokemonProject\core\src\main\java\com\mypokemon\game"
$backupPath = "c:\ProyectosJava\PokemonProject\backup_encoding_$(Get-Date -Format 'yyyyMMdd_HHmmss')"

Write-Host "Creando backup en: $backupPath" -ForegroundColor Yellow
New-Item -ItemType Directory -Path $backupPath -Force | Out-Null

$javaFiles = Get-ChildItem -Path $projectPath -Filter "*.java" -Recurse

Write-Host "Encontrados $($javaFiles.Count) archivos Java" -ForegroundColor Green
Write-Host ""

$totalFixed = 0
$filesModified = 0

foreach ($file in $javaFiles) {
    try {
        # Backup
        $relativePath = $file.FullName.Replace($projectPath, "")
        $backupFile = Join-Path $backupPath $relativePath
        $backupDir = Split-Path $backupFile -Parent
        
        if (-not (Test-Path $backupDir)) {
            New-Item -ItemType Directory -Path $backupDir -Force | Out-Null
        }
        
        Copy-Item $file.FullName $backupFile -Force
        
        # Leer contenido
        $content = Get-Content $file.FullName -Raw -Encoding UTF8
        $originalContent = $content
        $fixCount = 0
        
        # Reemplazos simples usando -replace
        $replacements = @(
            @('ƒÂ©', 'e'),
            @('ƒÂ­', 'i'),
            @('ƒÂ³', 'o'),
            @('ƒÂº', 'u'),
            @('ƒ¡', 'a'),
            @('ƒÂ±', 'n'),
            @('‚¡', '!'),
            @('‚¿', '?'),
            @('ƒâ€°', 'E'),
            @('ƒâ€œ', 'O'),
            @('ƒÆ'‚¡', 'a'),
            @('ƒÆ'‚©', 'e'),
            @('ƒÆ'‚­', 'i'),
            @('ƒÆ'‚³', 'o'),
            @('ƒÆ'‚º', 'u'),
            @('ƒÆ'‚±', 'n'),
            @('ƒâ€š‚¡', '!'),
            @('ƒâ€š‚¿', '?'),
            @('ƒâ€š‚Â', 'I'),
            @('ƒâ€š‚Í', 'I'),
            @('ƒÆ'‚Â', 'I')
        )
        
        foreach ($pair in $replacements) {
            $pattern = [regex]::Escape($pair[0])
            $matches = ([regex]::Matches($content, $pattern)).Count
            if ($matches -gt 0) {
                $content = $content -replace $pattern, $pair[1]
                $fixCount += $matches
            }
        }
        
        # Guardar si hubo cambios
        if ($content -ne $originalContent) {
            $utf8NoBom = New-Object System.Text.UTF8Encoding $false
            [System.IO.File]::WriteAllText($file.FullName, $content, $utf8NoBom)
            
            $filesModified++
            $totalFixed += $fixCount
            
            Write-Host "OK $($file.Name): $fixCount correcciones" -ForegroundColor Green
        }
        else {
            Write-Host "   $($file.Name): Sin cambios" -ForegroundColor Gray
        }
        
    }
    catch {
        Write-Host "ERROR en $($file.Name): $_" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "=== RESUMEN ===" -ForegroundColor Cyan
Write-Host "Archivos procesados: $($javaFiles.Count)"
Write-Host "Archivos modificados: $filesModified" -ForegroundColor Green
Write-Host "Total de correcciones: $totalFixed" -ForegroundColor Green
Write-Host "Backup en: $backupPath" -ForegroundColor Yellow
Write-Host ""
Write-Host "Completado!" -ForegroundColor Cyan
Write-Host ""
Write-Host "NOTA: Los acentos fueron reemplazados por letras simples (a, e, i, o, u)" -ForegroundColor Yellow
Write-Host "Esto evita problemas de compilacion pero pierde los acentos originales." -ForegroundColor Yellow
