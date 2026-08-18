param(
    [string]$OutputFile = "",
    [switch]$RunBuild
)

$ErrorActionPreference = "Stop"

function Invoke-NativeCommandSafe {
    param([scriptblock]$Command)

    $previous = $ErrorActionPreference
    $result = @()
    $exitCode = 0

    try {
        $ErrorActionPreference = "Continue"
        $result = @(& $Command 2>&1)
        $exitCode = $LASTEXITCODE
    }
    finally {
        $ErrorActionPreference = $previous
    }

    [pscustomobject]@{
        Output   = $result
        ExitCode = $exitCode
    }
}

$repoLookup = Invoke-NativeCommandSafe -Command {
    & git rev-parse --show-toplevel
}

if ($repoLookup.ExitCode -ne 0 -or $repoLookup.Output.Count -eq 0) {
    throw "This script must be run from inside the Antarchy - Ascension Companion Git repository."
}

$repoRoot = [string]$repoLookup.Output[0]
Set-Location $repoRoot

if ([string]::IsNullOrWhiteSpace($OutputFile)) {
    $timestamp = Get-Date -Format "yyyyMMdd-HHmmss"
    $OutputFile = Join-Path $repoRoot "GIT-DIFF-AND-FILE-CONTENTS-$timestamp.txt"
}
elseif (-not [System.IO.Path]::IsPathRooted($OutputFile)) {
    $OutputFile = Join-Path $repoRoot $OutputFile
}

$outputFullPath = [System.IO.Path]::GetFullPath($OutputFile)

function Write-Section {
    param(
        [System.IO.StreamWriter]$Writer,
        [string]$Title
    )

    $Writer.WriteLine("")
    $Writer.WriteLine(("=" * 100))
    $Writer.WriteLine($Title)
    $Writer.WriteLine(("=" * 100))
    $Writer.WriteLine("")
}

function Write-CommandOutput {
    param(
        [System.IO.StreamWriter]$Writer,
        [string]$CommandText,
        [scriptblock]$Command
    )

    $Writer.WriteLine("> $CommandText")
    $Writer.WriteLine("")

    $native = Invoke-NativeCommandSafe -Command $Command

    if ($native.Output.Count -eq 0) {
        $Writer.WriteLine("(no output)")
    }
    else {
        foreach ($line in $native.Output) {
            $Writer.WriteLine([string]$line)
        }
    }

    if ($native.ExitCode -ne 0) {
        $Writer.WriteLine("")
        $Writer.WriteLine("[command exited with code $($native.ExitCode)]")
    }

    $Writer.WriteLine("")
}

function Test-BinaryFile {
    param([string]$Path)

    if (-not (Test-Path -LiteralPath $Path -PathType Leaf)) {
        return $false
    }

    $stream = [System.IO.File]::OpenRead($Path)
    try {
        $bufferLength = [Math]::Min(8192, [int]$stream.Length)
        if ($bufferLength -eq 0) {
            return $false
        }

        $buffer = New-Object byte[] $bufferLength
        [void]$stream.Read($buffer, 0, $bufferLength)
        return ($buffer -contains 0)
    }
    finally {
        $stream.Dispose()
    }
}

function Write-CurrentFile {
    param(
        [System.IO.StreamWriter]$Writer,
        [string]$RelativePath
    )

    $fullPath = Join-Path $repoRoot $RelativePath

    if (-not (Test-Path -LiteralPath $fullPath -PathType Leaf)) {
        $Writer.WriteLine("(file does not exist in working tree)")
        return $false
    }

    $resolved = [System.IO.Path]::GetFullPath($fullPath)
    if ($resolved -eq $outputFullPath) {
        $Writer.WriteLine("(skipped report output file)")
        return $true
    }

    if (Test-BinaryFile -Path $fullPath) {
        $item = Get-Item -LiteralPath $fullPath
        $hash = (Get-FileHash -LiteralPath $fullPath -Algorithm SHA256).Hash
        $Writer.WriteLine("(binary file not dumped as text)")
        $Writer.WriteLine("Size: $($item.Length) bytes")
        $Writer.WriteLine("SHA256: $hash")
        return $true
    }

    try {
        $content = Get-Content -LiteralPath $fullPath -Raw -ErrorAction Stop
        if ($null -eq $content -or $content.Length -eq 0) {
            $Writer.WriteLine("(empty file)")
        }
        else {
            $Writer.Write($content)
            if (-not $content.EndsWith("`n")) {
                $Writer.WriteLine("")
            }
        }
        return $true
    }
    catch {
        $Writer.WriteLine("(could not read as text: $($_.Exception.Message))")
        return $false
    }
}

function Get-GitObjectContent {
    param([string]$Spec)

    $native = Invoke-NativeCommandSafe -Command {
        & git show --text $Spec
    }

    [pscustomobject]@{
        Success  = ($native.ExitCode -eq 0)
        Output   = @($native.Output)
        ExitCode = $native.ExitCode
    }
}

function Write-GitObject {
    param(
        [System.IO.StreamWriter]$Writer,
        [string]$Spec,
        [string]$MissingMessage
    )

    $result = Get-GitObjectContent -Spec $Spec

    if (-not $result.Success) {
        $Writer.WriteLine($MissingMessage)
        return $false
    }

    if ($result.Output.Count -eq 0) {
        $Writer.WriteLine("(empty file)")
        return $true
    }

    foreach ($line in $result.Output) {
        $Writer.WriteLine([string]$line)
    }

    return $true
}

function Write-HeadFile {
    param(
        [System.IO.StreamWriter]$Writer,
        [string]$RelativePath
    )

    return Write-GitObject `
        -Writer $Writer `
        -Spec "HEAD:$RelativePath" `
        -MissingMessage "(file does not exist in HEAD)"
}

function Write-IndexFile {
    param(
        [System.IO.StreamWriter]$Writer,
        [string]$RelativePath
    )

    return Write-GitObject `
        -Writer $Writer `
        -Spec ":$RelativePath" `
        -MissingMessage "(file does not exist in the index)"
}

function Get-CompanionVersion {
    $gradleProperties = Join-Path $repoRoot "gradle.properties"
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

    return "(not detected)"
}

$statusNative = Invoke-NativeCommandSafe -Command {
    & git status --porcelain=v1 -uall
}

if ($statusNative.ExitCode -ne 0) {
    throw "git status failed with exit code $($statusNative.ExitCode)."
}

$statusLines = @($statusNative.Output | ForEach-Object { [string]$_ })
$changed = New-Object System.Collections.Generic.List[object]

foreach ($line in $statusLines) {
    if ([string]::IsNullOrWhiteSpace($line) -or $line.Length -lt 4) {
        continue
    }

    $xy = $line.Substring(0, 2)
    $indexStatus = $xy.Substring(0, 1)
    $workTreeStatus = $xy.Substring(1, 1)
    $pathPart = $line.Substring(3)

    $oldPath = $null
    $newPath = $pathPart

    if ($pathPart -match '^(.*?) -> (.*)$') {
        $oldPath = $matches[1].Trim('"')
        $newPath = $matches[2].Trim('"')
    }
    else {
        $newPath = $pathPart.Trim('"')
    }

    $changed.Add([pscustomobject]@{
        XY             = $xy
        IndexStatus    = $indexStatus
        WorkTreeStatus = $workTreeStatus
        OldPath        = $oldPath
        Path           = $newPath
    })
}

$utf8NoBom = New-Object System.Text.UTF8Encoding($false)
$writer = New-Object System.IO.StreamWriter($OutputFile, $false, $utf8NoBom)

try {
    $writer.WriteLine("ANTARCHY - ASCENSION COMPANION GIT AUDIT")
    $writer.WriteLine("Generated: $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss K')")
    $writer.WriteLine("Repository: $repoRoot")
    $writer.WriteLine("Detected Companion version: $(Get-CompanionVersion)")

    Write-Section -Writer $writer -Title "REPOSITORY INFO"
    Write-CommandOutput -Writer $writer -CommandText "git branch --show-current" -Command {
        & git branch --show-current
    }
    Write-CommandOutput -Writer $writer -CommandText "git rev-parse HEAD" -Command {
        & git rev-parse HEAD
    }
    Write-CommandOutput -Writer $writer -CommandText "git log -5 --oneline" -Command {
        & git log -5 --oneline
    }

    Write-Section -Writer $writer -Title "GIT STATUS"
    Write-CommandOutput -Writer $writer -CommandText "git status" -Command {
        & git status
    }

    Write-Section -Writer $writer -Title "GIT STATUS --SHORT"
    Write-CommandOutput -Writer $writer -CommandText "git status --short --untracked-files=all" -Command {
        & git status --short --untracked-files=all
    }

    Write-Section -Writer $writer -Title "UNSTAGED DIFF"
    Write-CommandOutput -Writer $writer -CommandText "git diff --no-ext-diff --binary" -Command {
        & git diff --no-ext-diff --binary
    }

    Write-Section -Writer $writer -Title "STAGED DIFF"
    Write-CommandOutput -Writer $writer -CommandText "git diff --cached --no-ext-diff --binary" -Command {
        & git diff --cached --no-ext-diff --binary
    }

    Write-Section -Writer $writer -Title "DIFF SUMMARY"
    Write-CommandOutput -Writer $writer -CommandText "git diff --stat" -Command {
        & git diff --stat
    }
    Write-CommandOutput -Writer $writer -CommandText "git diff --cached --stat" -Command {
        & git diff --cached --stat
    }
    Write-CommandOutput -Writer $writer -CommandText "git diff --name-status" -Command {
        & git diff --name-status
    }
    Write-CommandOutput -Writer $writer -CommandText "git diff --cached --name-status" -Command {
        & git diff --cached --name-status
    }

    Write-Section -Writer $writer -Title "WHITESPACE CHECK"
    Write-CommandOutput -Writer $writer -CommandText "git diff --check" -Command {
        & git diff --check
    }
    Write-CommandOutput -Writer $writer -CommandText "git diff --cached --check" -Command {
        & git diff --cached --check
    }

    Write-Section -Writer $writer -Title "COMPANION PROJECT SNAPSHOT"

    $importantFiles = @(
        "gradle.properties",
        "build.gradle",
        "build.gradle.kts",
        "settings.gradle",
        "settings.gradle.kts",
        "src/main/resources/META-INF/neoforge.mods.toml",
        "src/main/resources/pack.mcmeta",
        "CHANGELOG.md",
        "README.md"
    )

    foreach ($important in $importantFiles) {
        if (Test-Path -LiteralPath (Join-Path $repoRoot $important)) {
            $writer.WriteLine($important)
        }
    }

    $writer.WriteLine("")
    $writer.WriteLine("Source tree:")
    $sourceRoot = Join-Path $repoRoot "src"
    if (Test-Path -LiteralPath $sourceRoot) {
        Get-ChildItem -LiteralPath $sourceRoot -Recurse -File |
            ForEach-Object {
                $_.FullName.Substring($repoRoot.Length + 1).Replace("\", "/")
            } |
            Sort-Object |
            ForEach-Object {
                $writer.WriteLine($_)
            }
    }
    else {
        $writer.WriteLine("(src directory not found)")
    }

    if ($RunBuild) {
        Write-Section -Writer $writer -Title "GRADLE BUILD CHECK"

        $gradlewBat = Join-Path $repoRoot "gradlew.bat"
        if (Test-Path -LiteralPath $gradlewBat) {
            Write-CommandOutput -Writer $writer -CommandText ".\gradlew.bat build" -Command {
                & $gradlewBat build
            }
        }
        else {
            $writer.WriteLine("(gradlew.bat not found, build check skipped)")
        }
    }

    Write-Section -Writer $writer -Title "FULL CONTENTS OF CHANGED / ADDED / UNTRACKED FILES"

    if ($changed.Count -eq 0) {
        $writer.WriteLine("(working tree is clean)")
    }
    else {
        foreach ($entry in $changed) {
            $writer.WriteLine("")
            $writer.WriteLine(("-" * 100))
            $writer.WriteLine("STATUS: $($entry.XY)")
            if ($entry.OldPath) {
                $writer.WriteLine("OLD PATH: $($entry.OldPath)")
            }
            $writer.WriteLine("PATH: $($entry.Path)")
            $writer.WriteLine(("-" * 100))
            $writer.WriteLine("")

            $currentFullPath = Join-Path $repoRoot $entry.Path
            $currentExists = Test-Path -LiteralPath $currentFullPath -PathType Leaf

            if ($currentExists) {
                $writer.WriteLine("[CURRENT WORKING TREE CONTENT]")
                $writer.WriteLine("")
                [void](Write-CurrentFile -Writer $writer -RelativePath $entry.Path)
            }
            elseif ($entry.WorkTreeStatus -eq "D" -and $entry.IndexStatus -ne "D" -and $entry.IndexStatus -ne " ") {
                $writer.WriteLine("[STAGED INDEX CONTENT - WORKING TREE FILE IS DELETED]")
                $writer.WriteLine("")
                [void](Write-IndexFile -Writer $writer -RelativePath $entry.Path)
            }
            elseif ($entry.IndexStatus -eq "D" -or $entry.WorkTreeStatus -eq "D") {
                $writer.WriteLine("[LAST COMMITTED CONTENT FROM HEAD]")
                $writer.WriteLine("")
                $headPath = if ($entry.OldPath) { $entry.OldPath } else { $entry.Path }
                [void](Write-HeadFile -Writer $writer -RelativePath $headPath)
            }
            else {
                $writer.WriteLine("(file does not exist in working tree, index, or an applicable HEAD path)")
            }

            $writer.WriteLine("")
        }
    }
}
finally {
    $writer.Dispose()
}

Write-Host ""
Write-Host "Companion audit report created:"
Write-Host $OutputFile

exit 0
