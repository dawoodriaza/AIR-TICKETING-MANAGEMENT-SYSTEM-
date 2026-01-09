
--- 
## API Documentation (DRAFT)

### Email Verification

| Request Type | URL                                           | Functionality             | Access | 
|--------------|-----------------------------------------------|---------------------------|--------|
| GET          | /auth/users/verify?token=(verification-token) | Verify Email              | PUBLIC |
| POST         | /auth/users/resend-verification               | Resend Email Verification | PUBLIC |

### Airport
| Method | Endpoint                    | Functionality             | Acceess |
| ------ | --------------------------- | ------------------------- | ------- |
| GET    | `/api/airports/{airportId}` | Retrieve airport          | PUBLIC  |
| GET    | `/api/airports`             | Retrieve List of airports | PUBLIC  |
| POST   | `/api/airports`             | Create airport            | PRIVATE |
| PUT    | `/api/airports/{airportId}` | Update airport            | PRIVATE |
| DELETE | `/api/airports/{airportId}` | Delete airport            | PRIVATE |
---
