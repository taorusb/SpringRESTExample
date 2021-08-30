output "user_access_key" {
  value = module.iam-user.iam_access_key_id
}

output "user_secret_key" {
  value = module.iam-user.iam_access_key_secret
}