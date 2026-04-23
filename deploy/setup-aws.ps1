<#
Production-style PowerShell AWS setup script for Seeker CRM
Location: deploy/setup-aws.ps1

USAGE:
1. Open PowerShell as your user (do NOT paste secrets here).
2. Ensure AWS CLI is installed and you have IAM credentials configured.
3. Run: powershell -ExecutionPolicy Bypass -File .\deploy\setup-aws.ps1

This script will:
- Create an S3 bucket (if it doesn't exist)
- Optionally create a Cognito user pool and app client
- Print recommended `setx` commands for environment variables

Security: This script does not store or transmit secrets.
#>

param(
    [string]$BucketName = "seeker-crm-bucket",
    [string]$Region = "us-east-1",
    [switch]$CreateCognito = $false
)

Write-Host "=== Seeker CRM AWS Setup (deploy) ===" -ForegroundColor Cyan

# Check aws CLI
try {
    aws --version | Out-Null
} catch {
    Write-Host "aws CLI not found. Install AWS CLI v2 and configure credentials first." -ForegroundColor Red
    exit 1
}

# Create S3 bucket if not exists
Write-Host "\n1) Ensuring S3 bucket exists: $BucketName ($Region)" -ForegroundColor Yellow
$exists = aws s3 ls "s3://$BucketName" 2>$null
if ($LASTEXITCODE -eq 0) {
    Write-Host "Bucket already exists: s3://$BucketName" -ForegroundColor Green
} else {
    Write-Host "Creating bucket: s3://$BucketName" -ForegroundColor Yellow
    aws s3 mb "s3://$BucketName" --region $Region
    if ($LASTEXITCODE -eq 0) { Write-Host "Bucket created" -ForegroundColor Green } else { Write-Host "Failed to create bucket" -ForegroundColor Red }
}

# Optional: Create Cognito resources
if ($CreateCognito) {
    Write-Host "\n2) Creating Cognito user pool and app client..." -ForegroundColor Yellow
    $poolId = aws cognito-idp create-user-pool --pool-name SeekerCRM --policies PasswordPolicy={MinimumLength=8,RequireUppercase=true,RequireLowercase=true,RequireNumbers=true} --query 'UserPool.Id' --output text
    if ($?) {
        Write-Host "Cognito User Pool created: $poolId" -ForegroundColor Green
        $clientJson = aws cognito-idp create-user-pool-client --user-pool-id $poolId --client-name seeker-app --generate-secret --explicit-auth-flows ALLOW_USER_PASSWORD_AUTH ALLOW_REFRESH_TOKEN_AUTH ALLOW_USER_SRP_AUTH
        Write-Host "App client created. Save these values to your environment or application.properties:" -ForegroundColor Green
        Write-Host $clientJson
    } else {
        Write-Host "Failed to create Cognito resources" -ForegroundColor Red
    }
}

# 3) Print environment variable guidance
Write-Host "\n3) Environment variables (run these or add to .env / application.properties)" -ForegroundColor Cyan
Write-Host "# PowerShell (persistent):" -ForegroundColor Yellow
Write-Host "setx AWS_S3_BUCKET \"$BucketName\"" -ForegroundColor White
Write-Host "setx AWS_DEFAULT_REGION \"$Region\"" -ForegroundColor White
Write-Host "setx AWS_ACCESS_KEY_ID \"YOUR_ACCESS_KEY_ID\"" -ForegroundColor White
Write-Host "setx AWS_SECRET_ACCESS_KEY \"YOUR_SECRET_ACCESS_KEY\"" -ForegroundColor White
Write-Host "setx COGNITO_USER_POOL_ID \"us-east-1_xxxxx\"" -ForegroundColor White
Write-Host "setx COGNITO_CLIENT_ID \"your-client-id\"" -ForegroundColor White
Write-Host "setx COGNITO_CLIENT_SECRET \"your-client-secret\"" -ForegroundColor White

Write-Host "\nFinished. Restart your terminal after running setx commands, then restart the Spring Boot app." -ForegroundColor Cyan
