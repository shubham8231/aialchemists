output "service_account_email" {
  value = google_service_account.incident_agent.email
}

output "service_account_name" {
  value = google_service_account.incident_agent.name
}
