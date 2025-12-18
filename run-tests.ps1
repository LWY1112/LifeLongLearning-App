# PowerShell script to run JUnit tests without Maven

Write-Host "Running JUnit Tests..." -ForegroundColor Green

# Find Java
$JAVA_HOME = $env:JAVA_HOME
if (-not $JAVA_HOME) {
    $javaPath = (Get-Command java -ErrorAction SilentlyContinue).Source
    if ($javaPath) {
        $JAVA_HOME = Split-Path (Split-Path $javaPath)
    } else {
        Write-Host "Error: Java not found. Please set JAVA_HOME or add Java to PATH." -ForegroundColor Red
        exit 1
    }
}

# Check if classes are compiled
if (-not (Test-Path "target\test-classes")) {
    Write-Host "Error: Test classes not compiled." -ForegroundColor Red
    Write-Host "Please compile tests first in IntelliJ IDEA: Build -> Build Project" -ForegroundColor Yellow
    exit 1
}

# Build classpath
$classpath = @(
    "target\classes"
    "target\test-classes"
    "libs\json-20250517.jar"
)

# Add JUnit JARs if they exist
if (Test-Path "libs") {
    $junitJars = Get-ChildItem -Path "libs" -Filter "junit-*.jar" -ErrorAction SilentlyContinue
    foreach ($jar in $junitJars) {
        $classpath += $jar.FullName
    }
    
    $otherJars = Get-ChildItem -Path "libs" -Filter "*.jar" -Exclude "json-*.jar" -ErrorAction SilentlyContinue
    foreach ($jar in $otherJars) {
        if ($jar.Name -notlike "junit-*") {
            $classpath += $jar.FullName
        }
    }
}

$CLASSPATH = $classpath -join ";"

Write-Host "Classpath: $CLASSPATH" -ForegroundColor Cyan
Write-Host ""

# Run tests using JUnit Platform Console Launcher
$javaExe = "$JAVA_HOME\bin\java.exe"

# Try to find JUnit Platform Console Launcher
$consoleLauncher = Get-ChildItem -Path "$env:USERPROFILE\.m2\repository" -Recurse -Filter "junit-platform-console-standalone*.jar" -ErrorAction SilentlyContinue | Select-Object -First 1

if ($consoleLauncher) {
    Write-Host "Using JUnit Platform Console Launcher..." -ForegroundColor Green
    $args = @(
        "-cp", "$CLASSPATH;$($consoleLauncher.FullName)",
        "org.junit.platform.console.ConsoleLauncher",
        "--class-path", "target\test-classes",
        "--scan-class-path"
    )
    & $javaExe $args
} else {
    Write-Host "JUnit Platform Console Launcher not found." -ForegroundColor Yellow
    Write-Host "Please use IntelliJ IDEA to run tests (recommended):" -ForegroundColor Cyan
    Write-Host "1. Right-click on src/test/java folder" -ForegroundColor White
    Write-Host "2. Select 'Run All Tests'" -ForegroundColor White
    Write-Host "`nOr install Maven and run: mvn test" -ForegroundColor Cyan
}

