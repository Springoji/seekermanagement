#!/usr/bin/env bash
# Production-style Bash AWS setup script for Seeker CRM
# Location: deploy/setup-aws.sh
# Usage: bash ./deploy/setup-aws.sh

BUCKET_NAME=${1:-seeker-crm-bucket}
REGION=${2:-us-east-1}
CREATE_COGNITO=${3:-false}

echo "=== Seeker CRM AWS Setup (deploy) ==="

# Check aws
if ! command -v aws >/dev/null 2>&1; then
  echo "aws CLI not found. Install AWS CLI v2 and configure credentials first." >&2
  exit 1
fi

# Create bucket if not exists
if aws s3 ls "s3://$BUCKET_NAME" >/dev/null 2>&1; then
  echo "Bucket exists: s3://$BUCKET_NAME"
else
  echo "Creating bucket: s3://$BUCKET_NAME"
  aws s3 mb "s3://$BUCKET_NAME" --region $REGION
fi

# Optional: Create Cognito
if [ "$CREATE_COGNITO" = "true" ]; then
  echo "Creating Cognito user pool..."
  POOL_ID=$(aws cognito-idp create-user-pool --pool-name SeekerCRM --policies PasswordPolicy={MinimumLength=8,RequireUppercase=true,RequireLowercase=true,RequireNumbers=true} --query 'UserPool.Id' --output text)
  echo "User Pool ID: $POOL_ID"
  CLIENT_JSON=$(aws cognito-idp create-user-pool-client --user-pool-id $POOL_ID --client-name seeker-app --generate-secret --explicit-auth-flows ALLOW_USER_PASSWORD_AUTH ALLOW_REFRESH_TOKEN_AUTH ALLOW_USER_SRP_AUTH)
  echo "App client JSON: $CLIENT_JSON"
fi

cat <<EOF

Run these environment variable exports (or add to your shell profile):

export AWS_S3_BUCKET=$BUCKET_NAME
export AWS_DEFAULT_REGION=$REGION
export AWS_ACCESS_KEY_ID=YOUR_ACCESS_KEY_ID
export AWS_SECRET_ACCESS_KEY=YOUR_SECRET_ACCESS_KEY
export COGNITO_USER_POOL_ID=us-east-1_xxxxx
export COGNITO_CLIENT_ID=your-client-id
export COGNITO_CLIENT_SECRET=your-client-secret

EOF