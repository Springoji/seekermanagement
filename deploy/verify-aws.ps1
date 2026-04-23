Write-Host "=== Seeker CRM: AWS Verification Script (deploy) ===" -ForegroundColor Cyan

# 1) Check AWS CLI identity
Write-Host "\n1) Checking AWS identity..." -ForegroundColor Yellow
try {
    $identity = aws sts get-caller-identity 2>$null | ConvertFrom-Json
    if ($identity) {
        Write-Host "✔ AWS identity:" -ForegroundColor Green
        Write-Host "  Account: $($identity.Account)"
        Write-Host "  Arn:     $($identity.Arn)"
    } else {
        Write-Host "✖ Unable to get caller identity. Ensure AWS CLI is configured and credentials are set." -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "✖ aws sts get-caller-identity failed. Ensure aws CLI is installed and in PATH." -ForegroundColor Red
    exit 1
}

# 2) Check S3 bucket exists
$bucketName = "seeker-crm-bucket"
Write-Host "\n2) Checking S3 bucket '$bucketName'..." -ForegroundColor Yellow
try {
    $buckets = aws s3 ls 2>$null
    if ($buckets -match $bucketName) {
        Write-Host "✔ Bucket appears in list." -ForegroundColor Green
    } else {
        Write-Host "⚠ Bucket not found in account. Create the bucket or check region/permissions." -ForegroundColor Yellow
    }
} catch {
    Write-Host "✖ Failed to list S3 buckets." -ForegroundColor Red
}

# 3) Test S3 upload/download/delete
Write-Host "\n3) Testing S3 upload/download/delete..." -ForegroundColor Yellow
$testFile = "aws-verify-$(Get-Date -Format 'yyyyMMddHHmmss').txt"
"seeker-crm-connection-test" | Out-File -FilePath $testFile -Encoding UTF8
aws s3 cp $testFile "s3://$bucketName/$testFile" 2>$null
if ($LASTEXITCODE -eq 0) {
    Write-Host "✔ Upload successful: s3://$bucketName/$testFile" -ForegroundColor Green
    aws s3 cp "s3://$bucketName/$testFile" "downloaded-$testFile" 2>$null
    if (Test-Path "downloaded-$testFile") { Write-Host "✔ Download successful" -ForegroundColor Green }
    aws s3 rm "s3://$bucketName/$testFile" 2>$null
    Remove-Item $testFile -ErrorAction SilentlyContinue
    Remove-Item "downloaded-$testFile" -ErrorAction SilentlyContinue
} else {
    Write-Host "✖ Upload failed. Check IAM permissions and bucket name." -ForegroundColor Red
}

# 4) Check backend health (application)
Write-Host "\n4) Checking local backend (http://localhost:8081)..." -ForegroundColor Yellow
try {
    $resp = Invoke-WebRequest -Uri "http://localhost:8081/" -Method Head -UseBasicParsing -ErrorAction SilentlyContinue
    if ($resp.StatusCode) {
        Write-Host "✔ Backend reachable (returned status $($resp.StatusCode))." -ForegroundColor Green
    } else {
        Write-Host "⚠ Backend not reachable. Start the app with: mvn spring-boot:run" -ForegroundColor Yellow
    }
} catch {
    Write-Host "⚠ Backend not reachable. Start the app with: mvn spring-boot:run" -ForegroundColor Yellow
}

Write-Host "\n=== Verification Complete ===" -ForegroundColor Cyan
Write-Host "If upload succeeded, your app can communicate with S3 using current credentials." -ForegroundColor Green
Write-Host "To run again: powershell -ExecutionPolicy Bypass -File deploy\verify-aws.ps1" -ForegroundColor Cyan
