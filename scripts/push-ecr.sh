#!/usr/bin/env bash
# Build and push the backend and/or frontend images to a private ECR registry.
#
# Usage:
#   VERSION=1.4.0 ./scripts/push-ecr.sh [be|fe|all]
#
# The image tag is read from the VERSION env var (default: "latest"). It can
# also be overridden with a second positional arg: ./scripts/push-ecr.sh fe v1.2.3
#
# Config (env vars, can also be set in scripts/push-ecr.env):
#   VERSION          Tag to build+push                          (default: latest)
#   AWS_REGION       AWS region of the ECR registry            (default: eu-west-1)
#   AWS_ACCOUNT_ID   AWS account ID owning the registry         (required, or resolved via `aws sts`)
#   ECR_REPO_BE      Backend repository name                    (default: multi-studio-fee-tracker-be)
#   ECR_REPO_FE      Frontend repository name                   (default: multi-studio-fee-tracker-fe)
#   IMAGE_PLATFORM   Target platform for the build               (default: linux/amd64)
#
# Examples:
#   VERSION=1.4.0 ./scripts/push-ecr.sh          # build+push be and fe, tag "1.4.0"
#   VERSION=1.4.0 ./scripts/push-ecr.sh be       # build+push backend only
#   ./scripts/push-ecr.sh fe v1.2.3              # build+push frontend, tag "v1.2.3"

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"

# Load optional local config (gitignored) with account/region overrides.
if [[ -f "$SCRIPT_DIR/push-ecr.env" ]]; then
  # shellcheck disable=SC1091
  source "$SCRIPT_DIR/push-ecr.env"
fi

TARGET="${1:-all}"
IMAGE_TAG="${2:-${VERSION:-latest}}"

AWS_REGION="${AWS_REGION:-eu-west-1}"
ECR_REPO_BE="${ECR_REPO_BE:-multi-studio-fee-tracker-be}"
ECR_REPO_FE="${ECR_REPO_FE:-multi-studio-fee-tracker-fe}"
IMAGE_PLATFORM="${IMAGE_PLATFORM:-linux/amd64}"

for cmd in aws docker; do
  if ! command -v "$cmd" >/dev/null 2>&1; then
    echo "error: '$cmd' is required but not found in PATH" >&2
    exit 1
  fi
done

if [[ -z "${AWS_ACCOUNT_ID:-}" ]]; then
  echo "AWS_ACCOUNT_ID not set, resolving via 'aws sts get-caller-identity'..."
  AWS_ACCOUNT_ID="$(aws sts get-caller-identity --query Account --output text)"
fi

REGISTRY="${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com"

case "$TARGET" in
  be|fe|all) ;;
  *)
    echo "error: unknown target '$TARGET' (expected: be, fe, all)" >&2
    exit 1
    ;;
esac

echo "Logging in to ECR registry ${REGISTRY}..."
aws ecr get-login-password --region "$AWS_REGION" \
  | docker login --username AWS --password-stdin "$REGISTRY"

ensure_repo() {
  local repo_name="$1"
  if ! aws ecr describe-repositories --region "$AWS_REGION" --repository-names "$repo_name" >/dev/null 2>&1; then
    echo "Repository '$repo_name' not found, creating it..."
    aws ecr create-repository \
      --region "$AWS_REGION" \
      --repository-name "$repo_name" \
      --image-scanning-configuration scanOnPush=true \
      --image-tag-mutability IMMUTABLE >/dev/null
  fi
}

build_and_push() {
  local context_dir="$1"
  local repo_name="$2"
  local image_uri="${REGISTRY}/${repo_name}:${IMAGE_TAG}"

  ensure_repo "$repo_name"

  echo "Building ${image_uri} from ${context_dir} (platform: ${IMAGE_PLATFORM})..."
  # --provenance=false --sbom=false: without these, BuildKit attaches an
  # attestation manifest alongside the image, which ECR lists as a second,
  # near-empty "image" (size 0) in the repository.
  docker build \
    --platform "$IMAGE_PLATFORM" \
    --provenance=false \
    --sbom=false \
    -t "$image_uri" \
    "$context_dir"

  echo "Pushing ${image_uri}..."
  docker push "$image_uri"

  echo "Done: ${image_uri}"
}

if [[ "$TARGET" == "be" || "$TARGET" == "all" ]]; then
  build_and_push "$ROOT_DIR/multi-studio-fee-tracker-be" "$ECR_REPO_BE"
fi

if [[ "$TARGET" == "fe" || "$TARGET" == "all" ]]; then
  build_and_push "$ROOT_DIR/multi-studio-fee-tracker-fe" "$ECR_REPO_FE"
fi

echo "All requested images pushed successfully."
