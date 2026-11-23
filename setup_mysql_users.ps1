param(
    [string]$EnvFile = ".env",
    [string]$SqlFile = "data/database/init/06_users_and_privileges.sql",
    [string]$ContainerName = "gamehub_db",
    [string]$RootPassword = ""
)

$envValues = @{}
Get-Content $EnvFile | ForEach-Object {
    $line = $_.Trim()
    if ($line -and -not $line.StartsWith('#') -and $line.Contains('=')) {
        $parts = $line.Split('=', 2)
        $envValues[$parts[0].Trim()] = $parts[1].Trim()
    }
}

$appPassword = $envValues['DB_PASSWORD']
$adminPassword = $envValues['DB_ADMIN_PASSWORD']

if (-not $appPassword -or $appPassword -eq 'CHANGE_APP_PASSWORD') {
    throw 'DB_PASSWORD is missing or still set to CHANGE_APP_PASSWORD in .env.'
}
if (-not $adminPassword -or $adminPassword -eq 'CHANGE_ADMIN_PASSWORD') {
    throw 'DB_ADMIN_PASSWORD is missing or still set to CHANGE_ADMIN_PASSWORD in .env.'
}

if (-not $RootPassword) {
    $RootPassword = $envValues['DB_PASSWORD_ROOT']
}
if (-not $RootPassword) {
    throw 'Set DB_PASSWORD_ROOT in .env or pass -RootPassword.'
}

$sqlTemplate = Get-Content $SqlFile -Raw
$sql = $sqlTemplate.Replace('{{APP_PASSWORD}}', $appPassword).Replace('{{ADMIN_PASSWORD}}', $adminPassword)

$tempFile = Join-Path ([System.IO.Path]::GetTempPath()) ("gamehub-users-{0}.sql" -f [guid]::NewGuid())
Set-Content -Path $tempFile -Value $sql -Encoding UTF8

try {
    Get-Content $tempFile | docker exec -i $ContainerName mysql -uroot --password=$RootPassword
}
finally {
    Remove-Item $tempFile -Force
}
