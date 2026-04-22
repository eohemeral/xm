# One-click project startup script
# Starts backend and frontend services

Write-Host "=== Office Learning Platform Startup Script ==="

# Check Java installation
Write-Host "Checking Java environment..."
try {
    $javaVersion = java -version 2>&1
    Write-Host "Java installed: $javaVersion"
} catch {
    Write-Host "Error: Java not found. Please install Java 8 or higher."
    exit 1
}

# Check Maven installation
Write-Host "Checking Maven environment..."
try {
    $mavenVersion = mvn -version 2>&1
    Write-Host "Maven installed: $mavenVersion"
} catch {
    Write-Host "Error: Maven not found. Please install Maven 3.6 or higher."
    exit 1
}

# Check Node.js installation
Write-Host "Checking Node.js environment..."
try {
    $nodeVersion = node -version
    Write-Host "Node.js installed: $nodeVersion"
} catch {
    Write-Host "Error: Node.js not found. Please install Node.js 14 or higher."
    exit 1
}

# Check npm installation
Write-Host "Checking npm environment..."
try {
    $npmVersion = npm -version
    Write-Host "npm installed: $npmVersion"
} catch {
    Write-Host "Error: npm not found. Please install npm."
    exit 1
}

# Start backend service
Write-Host "`n=== Starting Backend Service ==="
Write-Host "Entering springboot directory..."
Set-Location -Path ".\springboot"

# Check if backend is already built
if (-not (Test-Path ".\target\springboot-0.0.1-SNAPSHOT.jar")) {
    Write-Host "Backend not built, building now..."
    try {
        mvn clean install
        if ($LASTEXITCODE -ne 0) {
            Write-Host "Error: Backend build failed"
            exit 1
        }
        Write-Host "Backend build successful"
    } catch {
        Write-Host "Error: Backend build failed: $_"
        exit 1
    }
} else {
    Write-Host "Backend already built, skipping build step"
}

Write-Host "Starting backend service..."
# Start backend service in new window
Start-Process powershell -ArgumentList "java -jar target/springboot-0.0.1-SNAPSHOT.jar"
Write-Host "Backend service started at http://localhost:9090/api"

# Wait for backend service to start
Write-Host "Waiting for backend service to start..."
Start-Sleep -Seconds 5

# Start frontend service
Write-Host "`n=== Starting Frontend Service ==="
Write-Host "Entering vue directory..."
Set-Location -Path "..\vue"

# Check if frontend dependencies are installed
if (-not (Test-Path ".\node_modules")) {
    Write-Host "Frontend dependencies not installed, installing now..."
    try {
        npm install
        if ($LASTEXITCODE -ne 0) {
            Write-Host "Error: Frontend dependency installation failed"
            exit 1
        }
        Write-Host "Frontend dependencies installed successfully"
    } catch {
        Write-Host "Error: Frontend dependency installation failed: $_"
        exit 1
    }
} else {
    Write-Host "Frontend dependencies already installed, skipping installation step"
}

Write-Host "Starting frontend service..."
# Start frontend service in new window
Start-Process powershell -ArgumentList "npm run serve"
Write-Host "Frontend service started at http://localhost:8080"

# Return to project root directory
Set-Location -Path ".."

Write-Host "`n=== Startup Complete ==="
Write-Host "Backend service: http://localhost:9090/api"
Write-Host "Frontend service: http://localhost:8080"
Write-Host "`nPress any key to exit..."
$null = $Host.UI.RawUI.ReadKey('NoEcho,IncludeKeyDown')
