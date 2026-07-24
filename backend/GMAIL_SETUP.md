# Gmail Integration Setup Guide

This guide explains how to integrate Gmail to automatically fetch APP SUPPORT emails and create incidents.

## Step 1: Enable Gmail API

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select an existing one
3. Search for "Gmail API" and enable it
4. Go to **Credentials** > **Create Credentials** > **OAuth 2.0 Client ID**
5. Select **Desktop application**
6. Download the credentials as JSON file
7. Save it as `credentials.json` in the backend root directory

## Step 2: Configure Credentials File

Place your `credentials.json` file in:
```
/Users/dineshramanathan/IdeaProjects/AI-IncidentInvestigator/backend/credentials.json
```

The file should look like:
```json
{
  "installed": {
    "client_id": "your-client-id.apps.googleusercontent.com",
    "client_secret": "your-client-secret",
    "auth_uri": "https://accounts.google.com/o/oauth2/auth",
    "token_uri": "https://oauth2.googleapis.com/token",
    "redirect_uris": ["http://localhost:8888/"]
  }
}
```

## Step 3: Configure application.properties

Update `application.properties`:
```properties
gmail.credentials.file=credentials.json
gmail.email=your-email@gmail.com
gmail.sync.enabled=true
gmail.sync.interval=300
```

**Configuration Options:**
- `gmail.credentials.file`: Path to credentials JSON file
- `gmail.email`: Gmail account email address
- `gmail.sync.enabled`: Enable/disable automatic sync (default: false)
- `gmail.sync.interval`: Sync interval in seconds (default: 300 = 5 minutes)

## Step 4: First Authentication

1. Rebuild the backend:
```bash
cd backend
mvn clean install
```

2. Start the backend:
```bash
mvn spring-boot:run
```

3. First time only: A browser window will automatically open asking for Gmail authorization
4. Click "Allow" to grant the application access to your Gmail

## Step 5: API Endpoints

### Test Gmail Connection
```bash
curl http://localhost:8080/api/gmail/test-connection
```

Response:
```json
{
  "status": "success",
  "message": "Gmail API connection successful"
}
```

### Manual Sync (Fetch and Create Incidents)
```bash
curl http://localhost:8080/api/gmail/sync-app-support
```

Response:
```json
{
  "status": "success",
  "totalEmails": 5,
  "incidentsCreated": 5,
  "message": "5 incidents created from 5 APP SUPPORT emails"
}
```

### View Created Incidents
```bash
curl http://localhost:8080/api/incidents
```

## Email Subject Pattern

The system looks for emails with subject containing **"APP SUPPORT"**

Examples:
- `APP SUPPORT - Payment Service Down`
- `APP SUPPORT - Database Connection Error`
- `APP SUPPORT: User Authentication Failed`

Only unread emails are processed to avoid duplicates.

## Troubleshooting

### Error: "Credentials file not found"
- Make sure `credentials.json` is in the backend root directory
- Check the path in `application.properties`

### Error: "Gmail API connection failed"
- Verify the credentials file is valid JSON
- Check that Gmail API is enabled in Google Cloud Console
- Ensure the redirect URI in credentials matches (http://localhost:8888/)

### No emails being synced
- Check if `gmail.sync.enabled=true` in application.properties
- Verify you have unread APP SUPPORT emails
- Check backend logs for errors
- Try manual sync endpoint first

### Email Subject Not Matching
- Subject must contain exactly "APP SUPPORT"
- Case-sensitive search
- Modify the query in GmailService.fetchAppSupportEmails() if needed

## Gmail Scopes

The application uses the following scope (read-only):
```
https://www.googleapis.com/auth/gmail.readonly
```

This allows:
- Reading email messages
- Reading email headers
- Searching emails

It does NOT allow:
- Sending emails
- Deleting emails
- Modifying labels

## Example Email Processing Flow

1. **Email received**: Subject "APP SUPPORT - Payment Service Down"
2. **System detects**: Searches for unread APP SUPPORT emails
3. **Data extraction**:
   - Application Name: Payment Service (extracted from subject)
   - Severity: High (default for email alerts)
   - Description: Payment Service Down
   - Raw Log: Email body content
   - Source: Marked as EMAIL_ALERT
4. **Incident created**: INC-XXXX with all email details
5. **Available in system**: Visible in Dashboard and Incidents list

## Security Notes

- Credentials are stored locally in `tokens/` directory
- Tokens are encrypted and only accessible to the application
- Credentials file should NOT be committed to git
- Use `.gitignore` to exclude sensitive files

## Support

For issues:
1. Check Gmail API quota in Google Cloud Console
2. Verify email account has Gmail access
3. Review backend logs in `./logs/application.log`
4. Test with manual sync endpoint first
