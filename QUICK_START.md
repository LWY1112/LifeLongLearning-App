# Quick Start Guide - Run PathLearner

This is the **fastest way** to run the PathLearner application.

## Option 1: Using Maven (Recommended - Works Everywhere)

### Prerequisites
- Java 17 JDK installed
- Maven installed (download from https://maven.apache.org/download.cgi)

### Steps

1. **Open terminal/PowerShell in the project folder**

2. **Run this single command**:
   ```bash
   mvn clean compile javafx:run
   ```

3. **Wait for**:
   - Maven to download dependencies (first time only, ~2-5 minutes)
   - Project to compile
   - Application window to appear

**That's it!** The login window should appear.

### If Maven is Not Installed

**Windows:**
1. Download Maven from: https://maven.apache.org/download.cgi
2. Extract and add to PATH
3. Or use IntelliJ's bundled Maven (see Option 2)

**macOS:**
```bash
brew install maven
```

**Linux (Ubuntu/Debian):**
```bash
sudo apt install maven
```

## Option 2: Using IntelliJ IDEA (Easiest - No Configuration Needed!)

1. **Open project** in IntelliJ IDEA
2. **Wait for Maven import** (automatic)
3. **Run application**:
   - Click run configuration dropdown (top-right)
   - Select **"PathLearner (Maven)"**
   - Click green play button (▶)
   - **That's it!** No errors, no configuration needed!

**Alternative**: Open terminal in IntelliJ → Run: `mvn clean compile javafx:run`

## Option 3: PowerShell Script (Windows Only)

**Prerequisites:**
- Project compiled (use IntelliJ: `Build` → `Build Project`)
- Maven dependencies downloaded: `mvn dependency:resolve`

**Run:**
```powershell
.\run.ps1
```

## Troubleshooting

### "mvn: command not found"
- **Solution**: Install Maven or use IntelliJ's bundled Maven
- In IntelliJ: Open terminal → Maven should be available

### "JavaFX runtime components are missing"
- **Solution**: Use `mvn javafx:run` (handles JavaFX automatically)
- OR follow SETUP_GUIDE.md for manual configuration

### "Classes not compiled"
- **Solution**: Run `mvn clean compile` first
- OR build in IntelliJ: `Build` → `Build Project`

### Application doesn't start
- Check Java version: `java -version` (should show 17)
- Check terminal for error messages
- Try: `mvn clean compile javafx:run`

## Success!

When successful, you should see:
- ✅ Login window appears (1000x750)
- ✅ Title: "My App - Login"
- ✅ Login form visible

## Test Account

If a test account is included:
- Username: `lwy`
- Password: `123`

Or register a new account.

---

**Need more help?** See `SETUP_GUIDE.md` for detailed instructions.

