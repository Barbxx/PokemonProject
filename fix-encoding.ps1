# Script para corregir codificación UTF-8 en archivos Java
# Crea backup antes de modificar

Write-Host "=== Corrector de Codificación UTF-8 para Proyecto Pokemon ===" -ForegroundColor Cyan
Write-Host ""

# Configurar codificación
$OutputEncoding = [System.Text.Encoding]::UTF8

# Directorio del proyecto
$projectPath = "c:\ProyectosJava\PokemonProject\core\src\main\java\com\mypokemon\game"
$backupPath = "c:\ProyectosJava\PokemonProject\backup_encoding_$(Get-Date -Format 'yyyyMMdd_HHmmss')"

# Crear carpeta de backup
Write-Host "Creando backup en: $backupPath" -ForegroundColor Yellow
New-Item -ItemType Directory -Path $backupPath -Force | Out-Null

# Buscar todos los archivos .java
$javaFiles = Get-ChildItem -Path $projectPath -Filter "*.java" -Recurse

Write-Host "Encontrados $($javaFiles.Count) archivos Java" -ForegroundColor Green
Write-Host ""

# Definir mapeo de reemplazos (de mal codificado -> correcto)
$replacements = @{
    # Vocales con acentos
    'ƒÂ©' = 'é'
    'ƒÂ­' = 'í'
    'ƒÂ³' = 'ó'
    'ƒÂº' = 'ú'
    'ƒ¡' = 'á'
    'ƒÆ'‚¡' = 'á'
    'ƒÆ'‚©' = 'é'
    'ƒÆ'‚­' = 'í'
    'ƒÆ'‚³' = 'ó'
    'ƒÆ'‚º' = 'ú'
    
    # Signos de exclamación e interrogación
    '‚¡' = '¡'
    '‚¿' = '¿'
    'ƒâ€š‚¡' = '¡'
    'ƒâ€š‚¿' = '¿'
    
    # Eñe
    'ƒÂ±' = 'ñ'
    'ƒÆ'‚±' = 'ñ'
    
    # Mayúsculas con acentos
    'ƒâ€°' = 'É'
    'ƒâ€œ' = 'Ó'
    'ƒâ€š‚Â' = 'Í'
    'ƒâ€š‚Í' = 'Í'
    'SƒÂ' = 'SÍ'
    'ƒÆ'‚Â' = 'Í'
    
    # Combinaciones específicas encontradas
    'ƒÆ'†â€™ƒâ€š‚©' = 'é'
    'ƒÆ'†â€™ƒâ€š‚¡' = 'á'
    'ƒÆ'†â€™ƒâ€š‚­' = 'í'
    'ƒÆ'†â€™ƒâ€š‚³' = 'ó'
    'ƒÆ'†â€™ƒâ€š‚±' = 'ñ'
}

$totalFixed = 0
$filesModified = 0

foreach ($file in $javaFiles) {
    try {
        # Crear backup del archivo
        $relativePath = $file.FullName.Replace($projectPath, "")
        $backupFile = Join-Path $backupPath $relativePath
        $backupDir = Split-Path $backupFile -Parent
        
        if (-not (Test-Path $backupDir)) {
            New-Item -ItemType Directory -Path $backupDir -Force | Out-Null
        }
        
        Copy-Item $file.FullName $backupFile -Force
        
        # Leer contenido con UTF-8
        $content = Get-Content $file.FullName -Raw -Encoding UTF8
        $originalContent = $content
        $fixCount = 0
        
        # Aplicar cada reemplazo
        foreach ($key in $replacements.Keys) {
            $oldContent = $content
            $content = $content -replace [regex]::Escape($key), $replacements[$key]
            
            # Contar reemplazos en este archivo
            $matches = ([regex]::Matches($oldContent, [regex]::Escape($key))).Count
            if ($matches -gt 0) {
                $fixCount += $matches
            }
        }
        
        # Si hubo cambios, guardar el archivo
        if ($content -ne $originalContent) {
            # Guardar con UTF-8 sin BOM
            $utf8NoBom = New-Object System.Text.UTF8Encoding $false
            [System.IO.File]::WriteAllText($file.FullName, $content, $utf8NoBom)
            
            $filesModified++
            $totalFixed += $fixCount
            
            Write-Host "✓ $($file.Name): $fixCount reemplazos" -ForegroundColor Green
        } else {
            Write-Host "  $($file.Name): Sin cambios" -ForegroundColor Gray
        }
        
    } catch {
        Write-Host "✗ Error en $($file.Name): $_" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "=== RESUMEN ===" -ForegroundColor Cyan
Write-Host "Archivos procesados: $($javaFiles.Count)" -ForegroundColor White
Write-Host "Archivos modificados: $filesModified" -ForegroundColor Green
Write-Host "Total de correcciones: $totalFixed" -ForegroundColor Green
Write-Host "Backup guardado en: $backupPath" -ForegroundColor Yellow
Write-Host ""
Write-Host "¡Proceso completado!" -ForegroundColor Cyan
