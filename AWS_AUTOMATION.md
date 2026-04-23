# AWS Automation for Seeker CRM

This document explains the deployment helpers in `/deploy` and the minimal steps to connect your running backend to AWS.

Files added:
- `deploy/setup-aws.ps1` - PowerShell script to create S3 bucket and optionally Cognito resources, and to print `setx` commands. Safe: it does not store secrets.
- `deploy/setup-aws.sh` - Bash equivalent for Linux/macOS.
- `deploy/verify-aws.ps1` - quick verification script to test S3 upload and backend reachability.

Quick start (PowerShell):
1. Ensure `aws` CLI is installed and you have IAM access keys.
2. In PowerShell, run:
```powershell
# Option A: interactive configure
aws configure

# Option B: run the helper to create bucket (replace placeholders as needed)
powershell -ExecutionPolicy Bypass -File .\deploy\setup-aws.ps1 -BucketName seeker-crm-bucket -Region us-east-1
```
3. Set environment variables (the scripts print recommended `setx` commands). After `setx`, restart PowerShell.
4. Restart your backend:
```powershell
cd .\usermanagement
mvn spring-boot:run
```
5. Verify with the included verifier:
```powershell
powershell -ExecutionPolicy Bypass -File .\deploy\verify-aws.ps1
```

Notes and security:
- Never commit real credentials to git.
- Use IAM with least privilege (we recommended `AmazonS3FullAccess` and `AmazonCognitoPowerUser` for convenience during development).
- The Cognito creation block is optional and safe to skip if you prefer to create Cognito from console.

If you want, I can now:
- Run these scripts locally (you must run them on your machine because they use your AWS credentials), OR
- Walk you step-by-step while you run them and paste outputs here.
