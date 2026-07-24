# Email Integration Setup Guide - APP SUPPORT Email Monitoring

This guide explains how to set up email monitoring to automatically fetch "APP SUPPORT" emails from Gmail and create incidents.

## Quick Start

### Step 1: Generate Gmail App Password

1. Go to [Google Account Security](https://myaccount.google.com/security)
2. Enable 2-Factor Authentication if not already enabled
3. Go to **App passwords** (under "2-Step Verification")
4. Select "Mail" and "Windows Computer"
5. Google will generate a 16-character password - **save this**

### Step 2: Configure Backend

Edit `backend/src/main/resources/application.properties`:

```properties
# Email Configuration (Gmail IMAP)
email.imap.host=imap.gmail.com
email.imap.port=993
email.username=your-email@gmail.com
email.password=your-16-char-app-password
email.sync.enabled=true
email.sync.interval=300
```

**Configuration Details:**
- `email.username`: Your full Gmail address
- `email.password`: 16-character app password (NOT your regular password)
- `email.sync.enabled`: Set to `true` to enable automatic sync
- `email.sync.interval`: Sync interval in seconds (default: 300 = 5 minutes)

### Step 3: Restart Backend

```bash
cd backend
mvn spring-boot:run
```

Backend will automatically scan Gmail for APP SUPPORT emails every 5 minutes.

## API Endpoints

### Test Email Connection
```bash
curl http://localhost:8080/api/email/test-connection
```

Response:
```json
{
  "status": "success",
  "message": "Email service ready. Configure email.username and email.password to enable sync"
}
```

### Manual Sync (Trigger Now)
```bash
curl http://localhost:8080/api/email/sync-app-support
```

Response:
```json
{
  "status": "success",
  "totalEmails": 3,
  "incidentsCreated": 3,
  "message": "3 incidents created from 3 APP SUPPORT emails"
}
```

### View Created Incidents
```bash
curl http://localhost:8080/api/incidents
```

## Email Subject Format

The system looks for emails with subject **containing "APP SUPPORT"** (case-insensitive)

### Supported Subject Patterns:
- `APP SUPPORT - Payment Service Down`
- `APP SUPPORT: Database Connection Error`
- `App Support - User Authentication Failed`
- `APP SUPPORT Database timeout in prod`

### Email Content Parsing

When an email is detected, the system automatically:

1. **Extracts Application Name**: From the subject line after "APP SUPPORT"
   - Example: `APP SUPPORT - Cart Service Error` → Application: "Cart Service"

2. **Creates Incident** with:
   - **Title**: Email subject line
   - **Severity**: High (default for email alerts)
   - **Type**: EMAIL_ALERT
   - **Status**: New
   - **Content**: First 500 characters of email body

3. **Assigns Metadata**:
   - Environment: EMAIL
   - Created By: Email sender address
   - Raw Log: Email body content

## Automatic Sync vs Manual Sync

### Automatic Sync
- Runs every 5 minutes (configurable)
- Enabled via `email.sync.enabled=true`
- Runs in background scheduler
- Processes all APP SUPPORT emails

### Manual Sync
- Triggered on-demand via API endpoint
- Useful for testing or immediate processing
- Returns statistics on created incidents

## Troubleshooting

### "Email credentials not configured"
- Verify `email.username` and `email.password` are set
- Ensure no typos in configuration
- Check `application.properties` file

### "Connection refused" or "Authentication failed"
- Verify Gmail app password (NOT your regular password)
- Check 2-Factor Authentication is enabled
- Ensure Gmail IMAP is enabled: https://myaccount.google.com/lesssecureapps

### No emails being synced
- Check `email.sync.enabled=true`
- Verify you have unread APP SUPPORT emails in Gmail
- Try manual sync endpoint to test
- Check backend logs for errors

### Emails appearing as duplicates
- Each unique email is processed once
- The same email won't create multiple incidents
- Check incident list for existing entries

## Integration Flow

```
Gmail Inbox (with "APP SUPPORT" subject)
         ↓
Email Service (IMAP fetch every 5 min)
         ↓
Parse Email & Extract Details
         ↓
Create Incident via IncidentService
         ↓
Available in Dashboard & Incidents List
         ↓
User can view, acknowledge, and RCA analysis
```

## Example Email → Incident Mapping

**Email:**
```
From: support@paymentgateway.com
Subject: APP SUPPORT - Payment Processing Down
Body: Payment gateway is returning 503 errors. 
      All transactions are failing. Urgent!
```

**Creates Incident:**
```
{
  "incidentNo": "INC-2050",
  "applicationName": "Payment Processing",
  "severity": "High",
  "status": "New",
  "errorType": "EMAIL_ALERT",
  "description": "APP SUPPORT - Payment Processing Down",
  "rawLog": "Payment gateway is returning 503 errors...",
  "createdBy": "support@paymentgateway.com",
  "environment": "EMAIL"
}
```

## Gmail Security & Privacy

✅ **What the system can do:**
- Read unread emails
- Search for specific subjects
- Extract email content

❌ **What the system CANNOT do:**
- Send emails
- Delete emails
- Modify emails
- Access other Gmail features

The integration uses **Gmail App Password** (more secure than master password):
- Generates application-specific credentials
- Can be revoked anytime
- Doesn't grant full account access

## Advanced Configuration

### Custom IMAP Server

To use a different email provider:

```properties
email.imap.host=mail.yourserver.com
email.imap.port=993
```

Tested providers:
- ✅ Gmail (imap.gmail.com:993)
- ✅ Outlook (outlook.office365.com:993)
- ✅ Yahoo (imap.mail.yahoo.com:993)

### Modify Search Pattern

Edit `EmailService.java` line 61 to customize the search:

```java
if (subject != null && subject.toUpperCase().contains("YOUR_PATTERN")) {
```

## Logs

Check backend logs for email sync activity:

```bash
tail -f ./logs/application.log | grep -i "email\|APP SUPPORT"
```

Output example:
```
2026-07-24 00:45:30 - c.monitoring.scheduler.GmailSyncScheduler - Starting email APP SUPPORT sync...
2026-07-24 00:45:32 - c.monitoring.service.EmailService - Found 2 APP SUPPORT emails out of 150
2026-07-24 00:45:33 - c.monitoring.scheduler.GmailSyncScheduler - Created incident INC-2050 from email: APP SUPPORT - Payment Down
2026-07-24 00:45:34 - c.monitoring.scheduler.GmailSyncScheduler - Email sync completed. Created 1 incidents from 2 emails
```

## Support

- Check backend logs: `./logs/application.log`
- Test connection: `GET /email/test-connection`
- Manual sync: `GET /email/sync-app-support`
- View incidents: `GET /incidents`

## FAQ

**Q: Can I monitor multiple Gmail accounts?**
A: Currently supports one account. You can forward emails from multiple accounts to one Gmail address.

**Q: What if the Gmail password contains special characters?**
A: App passwords are typically 16 alphanumeric characters. If using special characters, ensure they're properly escaped in the properties file.

**Q: How often should I sync?**
A: Default is 5 minutes. Adjust `email.sync.interval` based on your needs (in seconds).

**Q: Can incidents be created from deleted emails?**
A: No, only emails currently in the inbox are processed.

**Q: Is there a limit to emails processed?**
A: The system fetches all emails in inbox matching the pattern. Gmail inbox size limits apply.
