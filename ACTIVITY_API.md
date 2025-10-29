# Now Bar & Live Activity API Implementation

## Overview
This implementation adds support for "Now Bar" and "Live Activity" features to the GAEA Asset Management API. These features enable real-time status updates and activity monitoring for mobile applications.

## New Endpoint

### GET /activity
Retrieves current activity information and statistics for the logged-in user.

**Authentication:** Required (JWT token)

**Response Body:**
```json
{
  "transactionTime": "2025-10-29T23:39:53",
  "resultCode": "200",
  "description": "OK",
  "data": {
    "recentActivities": [
      {
        "activityType": "APPROVAL_APPROVED",
        "deviceNum": 1,
        "deviceType": "컴퓨터",
        "userName": "홍길동",
        "description": "컴퓨터 승인이 완료되었습니다",
        "createDatetime": "2025-10-29T23:39:53"
      }
    ],
    "pendingApprovalCount": 5,
    "unreadMessageCount": 3,
    "totalDeviceCount": 150,
    "activeDeviceCount": 120,
    "myDeviceCount": 2,
    "lastUpdateTime": "2025-10-29T23:39:53"
  }
}
```

## Features

### 1. Recent Activities
- Shows the last 5 device-related activities
- Includes device history changes, approvals, rejections
- Displays activity type, device information, user, and description

### 2. Statistics Dashboard
- **Pending Approvals**: Count of items waiting for user's approval
  - Team managers see items pending team approval (A1)
  - Asset managers see items pending manager approval (A2)
  - Regular users see their own pending items
- **Unread Messages**: Count of unread notifications
- **Total Device Count**: Total number of devices in the system
- **Active Device Count**: Devices currently in use (status code '01')
- **My Device Count**: Devices assigned to the logged-in user

### 3. Activity Types
- `APPROVAL_PENDING_TEAM`: Waiting for team manager approval
- `APPROVAL_PENDING_MANAGER`: Waiting for asset manager approval
- `APPROVAL_APPROVED`: Approval completed
- `APPROVAL_REJECTED`: Approval rejected
- `DEVICE_UPDATED`: Device status changed

## Database Queries

The implementation uses existing tables:
- **DEVICE**: For device counts and status
- **DEVICE_HISTORY**: For recent activities
- **MESSAGE**: For unread message count

No new tables were added, minimizing database changes.

## Usage for Mobile Apps

### iOS Live Activity
The `/activity` endpoint can be polled periodically (e.g., every 30 seconds) to update iOS Live Activity widgets with:
- Current pending approval count
- Unread notification count
- Recent device activity updates

### Android Now Bar
Similar functionality for Android devices, showing real-time status in the notification area.

## Implementation Details

### Files Created
1. `ActivityVO.java` - Value object for activity data
2. `ActivityService.java` - Business logic for aggregating activity data
3. `ActivityMapper.java` - MyBatis mapper interface
4. `ActivityController.java` - REST API controller
5. `activity.xml` - MyBatis SQL mapper

### Security
- Uses Spring Security for authentication
- Requires valid JWT token
- User-specific data filtered by logged-in user's role and ID

## Testing

### Swagger UI
Access the API documentation at:
```
http://localhost:8090/swagger-ui/index.html
```

### Sample Request
```bash
curl -X GET "http://localhost:8090/activity" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Integration Notes

For mobile app developers:
1. Call `/activity` endpoint after user login
2. Poll periodically for updates (recommended: 30-60 seconds)
3. Use `lastUpdateTime` to track data freshness
4. Display `pendingApprovalCount` and `unreadMessageCount` as badges
5. Show `recentActivities` in a scrollable list
6. Display statistics in a dashboard view

## Future Enhancements

Potential improvements:
- WebSocket support for real-time push updates
- Customizable activity filters
- Activity time range selection
- Push notification integration
- Activity read/unread status tracking
