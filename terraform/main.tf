provider "aws" {
  profile = "default"
  region  = var.region
}

module "eb-cb-event-build" {
  source = "terraform-aws-modules/eventbridge/aws"

  create_bus = false

  attach_sqs_policy = true
  sqs_target_arns = [
    aws_sqs_queue.queue.arn
  ]

  rules = {
    codebuild-event-build = {
      description = "Generates event when codebuild builds project"
      event_pattern = <<EOF
  {
    "source": [
      "aws.codebuild"
    ],
    "detail-type": [
      "CodeBuild Build State Change"
    ],
    "detail": {
      "build-status": [
        "FAILED", "SUCCEEDED"
      ]
    }
  }
EOF
    }
  }
}

module "iam-user" {
  source  = "terraform-aws-modules/iam/aws//modules/iam-user"

  name = "simple-user"
}

module "assume-role-user" {
  source = "terraform-aws-modules/iam/aws//modules/iam-assumable-role"

  trusted_role_arns = [
    module.iam-user.iam_user_arn
  ]

  create_role = true

  role_name         = "s3-codebuild-access"

  custom_role_policy_arns = [
    module.s3_full_access.arn,
    module.codebuild_developer_access.arn
  ]
}

module "iam_assumable_role_custom" {
  source = "terraform-aws-modules/iam/aws//modules/iam-assumable-role"

  trusted_role_services = [
    "codebuild.amazonaws.com"
  ]

  create_role = true

  role_name         = "s3-ecr-access"

  custom_role_policy_arns = [
    module.s3_read_only_access.arn,
    module.ecr_full_access.arn
  ]
}

resource "aws_sqs_queue" "queue" {
  name = var.queue-name
}

resource "aws_s3_bucket" "main-for-sre" {
  bucket = "main-for-sre"
  acl    = "private"
}

resource "aws_s3_bucket" "additional-sre-zip" {
  bucket = "additional-sre-zip"
  acl    = "private"
}