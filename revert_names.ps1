$mappings = [ordered]@{
    'ExcepcionEspacio' = 'EspacioException'
    'ExcepcionPokeball' = 'PokeballException'
    'GestorPNJ' = 'NPCManager'
    'BrennerPNJ' = 'BrennerNPC'
    'FeidPNJ' = 'FeidNPC'
    'HarryPotterPNJ' = 'HarryPotterNPC'
    'HarryStylesPNJ' = 'HarryStylesNPC'
    'ColisionPNJ' = 'ColisionNPC'
    'PNJ' = 'NPC'
}

$root = 'c:\ProyectosJava\PokemonProject'

Write-Host 'Updating file contents...'
$files = Get-ChildItem -Path $root -Filter '*.java' -Recurse
foreach ($file in $files) {
    $content = Get-Content $file.FullName -Raw -Encoding utf8
    $changed = $false
    foreach ($key in $mappings.Keys) {
        $val = $mappings[$key]
        if ($content -match "\b$key\b") {
            $content = $content -replace "\b$key\b", $val
            $changed = $true
        }
    }
    if ($changed) {
        Set-Content $file.FullName $content -Encoding utf8
    }
}

Write-Host 'Renaming files...'
$allJavaFiles = Get-ChildItem -Path $root -Filter '*.java' -Recurse | Select-Object -ExpandProperty FullName
foreach ($key in $mappings.Keys) {
    $val = $mappings[$key]
    foreach ($filePath in $allJavaFiles) {
        $fileName = [System.IO.Path]::GetFileNameWithoutExtension($filePath)
        if ($fileName -eq $key) {
            $dir = [System.IO.Path]::GetDirectoryName($filePath)
            $newPath = Join-Path $dir "$val.java"
            if (Test-Path $filePath) {
                Write-Host "Renaming $filePath to $newPath"
                Rename-Item $filePath -NewName "$val.java" -Force
            }
        }
    }
    # Refresh file list after rename to avoid using old paths
    $allJavaFiles = Get-ChildItem -Path $root -Filter '*.java' -Recurse | Select-Object -ExpandProperty FullName
}

# Fix BOM added by Set-Content
foreach ($file in (Get-ChildItem -Path $root -Filter '*.java' -Recurse)) {
    $content = Get-Content $file.FullName -Raw
    $utf8NoBOM = New-Object System.Text.UTF8Encoding($false)
    [System.IO.File]::WriteAllText($file.FullName, $content, $utf8NoBOM)
}
