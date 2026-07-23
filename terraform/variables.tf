variable "project_id" {
  description = "GCP Project ID"
  type        = string
}

variable "region" {
  description = "Deployment Region"
  type        = string
  default     = "us-central1"
}

variable "service_account_name" {
  description = "Cloud Run Service Account Name"
  type        = string
  default     = "incident-agent-sa"
}
