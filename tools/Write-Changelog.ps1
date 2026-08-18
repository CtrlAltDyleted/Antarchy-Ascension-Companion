param(
    [string]$Version = "",
    [string]$Category = "",
    [string[]]$Items = @(),
    [string]$ChangelogPath = "CHANGELOG.md"
)

$ErrorActionPreference = "Stop"

function Get-RepoRoot {
    $previous = $ErrorActionPreference

    try {
        $ErrorActionPreference = "Continue"
        $output = @(& git rev-parse --show-toplevel 2>&1)
        $exitCode = $LASTEXITCODE
    }
    finally {
        $ErrorActionPreference = $previous
    }

    if ($exitCode -ne 0 -or $output.Count -eq 0) {
        throw "This script must be run from inside the Antarchy - Ascension Companion Git repository."
    }

    return [string]$output[0]
}

function Get-DetectedVersion {
    param([string]$RepoRoot)

    $gradleProperties = Join-Path $RepoRoot "gradle.properties"

    if (Test-Path -LiteralPath $gradleProperties) {
        foreach ($line in Get-Content -LiteralPath $gradleProperties) {
            if ($line -match '^\s*mod_version\s*=\s*(.+?)\s*$') {
                return $matches[1]
            }

            if ($line -match '^\s*version\s*=\s*(.+?)\s*$') {
                return $matches[1]
            }
        }
    }

    return ""
}

function Normalize-Bullet {
    param([string]$Text)

    $trimmed = $Text.Trim()

    if ([string]::IsNullOrWhiteSpace($trimmed)) {
        return $null
    }

    $trimmed = $trimmed -replace '^[\-\*\+]\s*', ''

    return "- $trimmed"
}

$repoRoot = Get-RepoRoot
Set-Location $repoRoot

if (-not [System.IO.Path]::IsPathRooted($ChangelogPath)) {
    $ChangelogPath = Join-Path $repoRoot $ChangelogPath
}

Write-Host ""
Write-Host "=== ANTARCHY - ASCENSION COMPANION CHANGELOG ==="
Write-Host ""

if ([string]::IsNullOrWhiteSpace($Version)) {
    $detectedVersion = Get-DetectedVersion -RepoRoot $repoRoot

    if ([string]::IsNullOrWhiteSpace($detectedVersion)) {
        $inputVersion = Read-Host "Version [Unreleased]"

        if ([string]::IsNullOrWhiteSpace($inputVersion)) {
            $Version = "Unreleased"
        }
        else {
            $Version = $inputVersion.Trim()
        }
    }
    else {
        $inputVersion = Read-Host "Version [$detectedVersion]"

        if ([string]::IsNullOrWhiteSpace($inputVersion)) {
            $Version = $detectedVersion
        }
        else {
            $Version = $inputVersion.Trim()
        }
    }
}

if ([string]::IsNullOrWhiteSpace($Category)) {
    $inputCategory = Read-Host "Category [Changed]"

    if ([string]::IsNullOrWhiteSpace($inputCategory)) {
        $Category = "Changed"
    }
    else {
        $Category = $inputCategory.Trim()
    }
}

$normalizedItems = New-Object System.Collections.Generic.List[string]

foreach ($item in $Items) {
    $bullet = Normalize-Bullet -Text $item

    if ($null -ne $bullet) {
        $normalizedItems.Add($bullet)
    }
}

if ($normalizedItems.Count -eq 0) {
    Write-Host ""
    Write-Host "Enter changelog bullets one at a time."
    Write-Host "Press ENTER on a blank line when finished."
    Write-Host ""

    while ($true) {
        $item = Read-Host "-"

        if ([string]::IsNullOrWhiteSpace($item)) {
            break
        }

        $bullet = Normalize-Bullet -Text $item

        if ($null -ne $bullet) {
            $normalizedItems.Add($bullet)
        }
    }
}

if ($normalizedItems.Count -eq 0) {
    throw "No changelog items were supplied."
}

$date = Get-Date -Format "yyyy-MM-dd"
$heading = "## $Version - $date"

$entryLines = New-Object System.Collections.Generic.List[string]

$entryLines.Add($heading)
$entryLines.Add("")
$entryLines.Add("### $Category")
$entryLines.Add("")

foreach ($bullet in $normalizedItems) {
    $entryLines.Add($bullet)
}

$entryLines.Add("")

$entryText = $entryLines -join "`n"

if (Test-Path -LiteralPath $ChangelogPath) {
    $existing = [System.IO.File]::ReadAllText(
        [System.IO.Path]::GetFullPath($ChangelogPath)
    )

    if ($existing -match "(?m)^##\s+$([regex]::Escape($Version))(\s+-\s+\d{4}-\d{2}-\d{2})?\s*$") {
        throw "CHANGELOG.md already contains a section for version '$Version'. Edit that section manually instead of creating a duplicate."
    }

    if ($existing -match '(?m)^#\s+.+$') {
        $firstNewline = $existing.IndexOf("`n")

        if ($firstNewline -ge 0) {
            $header = $existing.Substring(
                0,
                $firstNewline + 1
            ).TrimEnd("`r", "`n")

            $rest = $existing.Substring(
                $firstNewline + 1
            ).TrimStart("`r", "`n")

            $final = $header + "`n`n" + $entryText

            if (-not [string]::IsNullOrWhiteSpace($rest)) {
                $final += "`n" + $rest
            }
        }
        else {
            $final = $existing.TrimEnd() + "`n`n" + $entryText
        }
    }
    else {
        $final =
            "# Antarchy - Ascension Companion Changelog`n`n" +
            $entryText +
            "`n" +
            $existing.TrimStart("`r", "`n")
    }
}
else {
    $final =
        "# Antarchy - Ascension Companion Changelog`n`n" +
        $entryText
}

$utf8NoBom = New-Object System.Text.UTF8Encoding($false)

[System.IO.File]::WriteAllText(
    [System.IO.Path]::GetFullPath($ChangelogPath),
    $final.TrimEnd() + "`n",
    $utf8NoBom
)

Write-Host ""
Write-Host "Updated:"
Write-Host $ChangelogPath
Write-Host ""
Write-Host "Added $Version / $Category with $($normalizedItems.Count) item(s)."
Write-Host ""

exit 0
