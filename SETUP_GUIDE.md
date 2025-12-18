# Setup Guide - Running PathLearner

This guide will help you run the PathLearner application. **No prior experience needed!**

## What You Need

1. **Java 17** (free, download from https://adoptium.net/)
2. **Maven** (free, download from https://maven.apache.org/download.cgi)
   - OR use IntelliJ IDEA (which includes Maven)

## Method 1: Run from Command Line (Easiest - No IntelliJ Needed!)

### Step 1: Install Java 17
1. Download from: https://adoptium.net/
2. Install it
3. Verify: Open terminal/PowerShell and type:
   ```bash
   java -version
   ```
   Should show: `openjdk version "17"` or `java version "17"`

### Step 2: Install Maven
1. Download from: https://maven.apache.org/download.cgi
2. Extract and add to PATH (or use IntelliJ's bundled Maven)
3. Verify: Open terminal/PowerShell and type:
   ```bash
   mvn -version
   ```
   Should show Maven version

### Step 3: Run the Application
1. **Extract the zip file** to any folder
2. **Open terminal/PowerShell** in the extracted folder
3. **Run this command**:
   ```bash
   mvn clean compile javafx:run
   ```
4. **Wait** (first time takes 2-5 minutes to download dependencies)
5. **Login window appears!** ✅

**That's it!** The application is running.

---

## Method 2: Run from IntelliJ IDEA

### Step 1: Install Prerequisites
- **Java 17 JDK**: https://adoptium.net/
- **IntelliJ IDEA**: https://www.jetbrains.com/idea/download/ (Community edition is free)

### Step 2: Open Project
1. Launch IntelliJ IDEA
2. Click **File** → **Open**
3. Select the extracted `SC_assignment` folder
4. Click **Trust Project** if asked
5. Wait for indexing to finish (1-2 minutes)

### Step 3: Set Java Version
1. **File** → **Project Structure** (or `Ctrl+Alt+Shift+S`)
2. Under **Project** → **SDK**, select **Java 17**
   - If not listed: Click **Add SDK** → **Download JDK** → Select **17** → **Download**
3. Click **OK**

### Step 4: Run the Application

**Method 1: Use IntelliJ Run Configuration (Recommended - No Errors!)**
1. **Click the run configuration dropdown** (top-right, next to green play button)
2. **Select "PathLearner (Maven)"** from the dropdown
3. **Click the green play button** (▶)
4. Wait for application to start

**Method 2: Use Terminal (Alternative)**
1. Open terminal in IntelliJ (bottom panel, or `View` → `Tool Windows` → `Terminal`)
2. Type: `mvn clean compile javafx:run`
3. Press Enter

**Done!** Login window should appear.

**✅ Both methods work perfectly - no JavaFX errors!**

---

## Troubleshooting

### "Java not found" or "mvn: command not found"
- **Solution**: Install Java 17 and/or Maven (see links above)
- Make sure they're added to your system PATH

### "JavaFX runtime components are missing"
- **Solution**: Use Maven command: `mvn javafx:run`
- Maven automatically handles JavaFX

### "Classes not compiled"
- **Solution**: Run `mvn clean compile` first
- Or in IntelliJ: `Build` → `Build Project`

### Application doesn't start
- Check Java version: `java -version` (must be 17)
- Check terminal for error messages
- Try: `mvn clean compile javafx:run`

### Still having issues?
- See `QUICK_START.md` for the simplest method
- Make sure you have Java 17 (not Java 8 or 11)
- Make sure Maven is installed and in PATH

---

## Quick Reference

**Fastest way to run:**
```bash
mvn clean compile javafx:run
```

**Works on:** Windows, macOS, Linux

**First time:** Takes 2-5 minutes (downloading dependencies)

**After that:** Takes 10-30 seconds

---

## Test Account (if included)

- Username: `lwy`
- Password: `123`

Or register a new account.

---

**Need more help?** See `QUICK_START.md` for even simpler instructions.
