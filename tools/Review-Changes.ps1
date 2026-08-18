param(
    [switch]$Build
)

$ErrorActionPreference = "Stop"

function Invoke-Safe {
    param([scriptblock]$Command)

    $previous = $ErrorActionPreference
    try {
        $ErrorActionPreference = "Continue"
        $output = @(& $Command 2>&1)
        $exitCode = $LASTEXITCODE
    }
    finally {
        $ErrorActionPreference = $previous
    }

    [pscustomobject]@{
        Output = $output
        ExitCode = $exitCode
    }
}

$repo = Invoke-Safe { & git rev-parse --show-toplevel }
if ($repo.ExitCode -ne 0 -or $repo.Output.Count -eq 0) {
    throw "Not inside a Git repository."
}

$repoRoot = [string]$repo.Output[0]
Set-Location $repoRoot

Write-Host ""
Write-Host "=== ANTARCHY - ASCENSION COMPANION REVIEW ==="
Write-Host ""

& git status --short --untracked-files=all
Write-Host ""

Write-Host "=== UNSTAGED STAT ==="
& git diff --stat
Write-Host ""

Write-Host "=== STAGED STAT ==="
& git diff --cached --stat
Write-Host ""

Write-Host "=== UNSTAGED FILES ==="
& git diff --name-status
Write-Host ""

Write-Host "=== STAGED FILES ==="
& git diff --cached --name-status
Write-Host ""

Write-Host "=== WHITESPACE CHECK ==="
$unstagedCheck = Invoke-Safe { & git diff --check }
$stagedCheck = Invoke-Safe { & git diff --cached --check }

if ($unstagedCheck.Output.Count -eq 0 -and $stagedCheck.Output.Count -eq 0) {
    Write-Host "PASS: No whitespace errors detected."
}
else {
    foreach ($line in $unstagedCheck.Output) { Write-Host $line }
    foreach ($line in $stagedCheck.Output) { Write-Host $line }
}

if ($Build) {
    Write-Host ""
    Write-Host "=== GRADLE BUILD ==="
    $gradlew = Join-Path $repoRoot "gradlew.bat"

    if (-not (Test-Path -LiteralPath $gradlew)) {
        throw "gradlew.bat was not found."
    }

    & $gradlew build
    if ($LASTEXITCODE -ne 0) {
        exit $LASTEXITCODE
    }
}

Write-Host ""
Write-Host "Review complete."
exit 0
