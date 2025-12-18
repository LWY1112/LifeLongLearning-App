# PowerShell script to run JavaFX application with proper module path

Write-Host "Starting JavaFX Application..." -ForegroundColor Green

# Find Java
$JAVA_HOME = $env:JAVA_HOME
if (-not $JAVA_HOME) {
    Write-Host "JAVA_HOME not set. Trying to find Java..." -ForegroundColor Yellow
    $javaPath = (Get-Command java -ErrorAction SilentlyContinue).Source
    if ($javaPath) {
        $JAVA_HOME = Split-Path (Split-Path $javaPath)
        Write-Host "Found Java at: $JAVA_HOME" -ForegroundColor Green
    } else {
        Write-Host "Error: Java not found. Please set JAVA_HOME or add Java to PATH." -ForegroundColor Red
        exit 1
    }
}

# Check if classes are compiled
if (-not (Test-Path "target\classes\org\example\Main.class")) {
    Write-Host "Error: Classes not compiled. Please compile the project first." -ForegroundColor Red
    Write-Host "In IntelliJ IDEA: Build -> Build Project" -ForegroundColor Yellow
    exit 1
}

$MAVEN_REPO = "$env:USERPROFILE\.m2\repository"
$JAVAFX_VERSION = "17.0.2"

# Detect platform for JavaFX
$platform = "win"  # Windows
$os = $env:OS
if ($os -like "*Linux*") { $platform = "linux" }
elseif ($os -like "*Darwin*" -or $os -like "*Mac*") { $platform = "mac" }

# Build module path with JavaFX JARs (try platform-specific first, fallback to generic)
$jars = @(
    "javafx-base",
    "javafx-controls",
    "javafx-fxml",
    "javafx-graphics"
)

$MODULE_PATH_PARTS = @()
foreach ($jar in $jars) {
    # Try platform-specific JAR first
    $platformJarPath = "$MAVEN_REPO\org\openjfx\$jar\$JAVAFX_VERSION\$jar-$JAVAFX_VERSION-$platform.jar"
    $genericJarPath = "$MAVEN_REPO\org\openjfx\$jar\$JAVAFX_VERSION\$jar-$JAVAFX_VERSION.jar"
    
    if (Test-Path $platformJarPath) {
        $MODULE_PATH_PARTS += $platformJarPath
        Write-Host "Using platform-specific: $jar-$platform.jar" -ForegroundColor Gray
    } elseif (Test-Path $genericJarPath) {
        $MODULE_PATH_PARTS += $genericJarPath
        Write-Host "Using generic: $jar.jar" -ForegroundColor Gray
    } else {
        Write-Host "Warning: $jar not found" -ForegroundColor Yellow
    }
}

if ($MODULE_PATH_PARTS.Count -eq 0) {
    Write-Host "Error: No JavaFX JARs found. Please run 'mvn dependency:resolve' first." -ForegroundColor Red
    exit 1
}

$MODULE_PATH = $MODULE_PATH_PARTS -join ";"

# Add target/classes and libs to classpath
$CLASSPATH = "target/classes;libs/json-20250517.jar"

Write-Host "Running application..." -ForegroundColor Green
Write-Host "Module path: $MODULE_PATH" -ForegroundColor Cyan
Write-Host ""

# Run the application - use proper quoting for paths with spaces
$javaExe = "$JAVA_HOME\bin\java.exe"
$args = @(
    "--module-path", $MODULE_PATH,
    "--add-modules", "javafx.controls,javafx.fxml",
    "-cp", $CLASSPATH,
    "org.example.Main"
)

& $javaExe $args

if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "Application exited with error code: $LASTEXITCODE" -ForegroundColor Red
}

