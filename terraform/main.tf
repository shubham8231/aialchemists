# resource "google_storage_bucket" "my_github_bucket" {
#   name                        = "hack-team-aialchemists-2026_tfc_bucket"
#   location                    = "europe-west1"
#   force_destroy               = true
#   public_access_prevention    = "enforced"
#   uniform_bucket_level_access = true
# }
resource "google_service_account" "incident_agent" {

  account_id   = var.service_account_name

  display_name = "Incident AI Agent Service Account"

  description  = "Service account used by Cloud Run to access Vertex AI and Cloud SQL"
}
