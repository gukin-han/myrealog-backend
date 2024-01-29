# MyReaLog API Server
last updated: 2023.12.29

## API Specification

### OAuth

| Method | End Point                 | Description                | Sample Valid Request Body |
|--------|---------------------------|----------------------------|---------|
| GET    | `/api/v1/signin/oauth/google`           | Redirect to OAuth Provider |         |
| GET    | `/api/v1/signin/callback/google`        | Sign in                    |         |

### User

| Method | End Point         | Description      | Sample Valid Request Body                 |
|--------|-------------------|------------------|-------------------------------------------|
| POST   | `/api/v1/users`        | Register User    |                                           |
| GET    | `/api/v1/users/me`   | Get user profile | [JSON](#apiv1usersme) |

## API Details

### User
#### `/api/v1/users/me`
```json
{
  "username": "user123",
  "displayName": "홍길동",
  "avatarUrl": "https://example.com/avatar/user123.jpg"
}

```

## ERD

![my-real-blog](https://github.com/gukin-han/myrealog-backend/assets/115940366/3d3fcf71-12c6-4ecb-af99-f875d445cfc1)
