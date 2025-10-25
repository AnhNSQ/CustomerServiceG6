# Create Ticket API Endpoint

## Overview
This document describes the implementation of the "Create Ticket" API endpoint for authenticated customers.

## Endpoint Details
- **URL**: `POST /api/customers/tickets`
- **Authentication**: Required (Session-based)
- **Content-Type**: `application/json`

## Request Body
```json
{
  "subject": "string (required, max 255 characters)",
  "description": "string (required, max 2000 characters)"
}
```

## Response
### Success Response (201 Created)
```json
{
  "success": true,
  "message": "Ticket created successfully",
  "data": {
    "message": "Ticket created successfully",
    "ticketId": 123,
    "status": "PENDING"
  }
}
```

### Error Responses
#### 401 Unauthorized
```json
{
  "success": false,
  "message": "Authentication required"
}
```

#### 400 Bad Request
```json
{
  "success": false,
  "message": "Error message describing the issue"
}
```

## Implementation Details

### Files Modified/Created:
1. **CustomerTicketCreateRequest.java** - DTO for simplified ticket creation
2. **CustomerController.java** - Added `/tickets` endpoint calling CustomerService
3. **CustomerService.java** - Added ticket management methods
4. **WebController.java** - Added routes for customer ticket views
5. **create-ticket.html** - Customer ticket creation form
6. **dashboard.html** - Updated customer dashboard with ticket stats
7. **Ticket.java** - Added `PENDING` status to Status enum
8. **SecurityConfig.java** - Added authentication requirement for tickets endpoint

### Key Features:
- ✅ Authenticated customers only (using session-based authentication)
- ✅ Simplified request body (only subject and description required)
- ✅ Automatic assignment to admin department (by name)
- ✅ PENDING status on creation
- ✅ Proper error handling and validation
- ✅ SOLID principles compliance
- ✅ Service layer architecture (CustomerService handles ticket logic)
- ✅ Clean separation of concerns

### Architecture Changes:
- **Removed**: TicketService, TicketCreateRequest DTO
- **Added**: Ticket management methods in CustomerService
- **Simplified**: Only CustomerTicketCreateRequest DTO is used
- **Enhanced**: Customer ticket management with dedicated views
- **Improved**: Proper service layer architecture
- **Updated**: Tickets automatically assigned to admin department

### Database Changes:
- Added `PENDING` status to the `Ticket.Status` enum
- Tickets are created with `PENDING` status by default
- Automatic timestamp setting with `LocalDateTime.now()`
- **Updated**: Tickets automatically assigned to admin department (by name)

## Customer Views
- **Create Ticket**: `/customer/tickets/create` - Form to create new tickets
- **Dashboard**: `/dashboard` - Main customer dashboard with ticket stats and recent tickets

## Usage Example
```bash
curl -X POST http://localhost:8080/api/customers/tickets \
  -H "Content-Type: application/json" \
  -H "Cookie: JSESSIONID=your-session-id" \
  -d '{
    "subject": "Product inquiry",
    "description": "I need help with my recent order"
  }'
```
