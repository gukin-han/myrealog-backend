# MyReaLog API Server
last updated: 2023.12.29
<br />

## API Specification
<br />

### OAuth

| Method | End Point                 | Description                | Auth         | Details  |
|--------|---------------------------|----------------------------|--------------|----------|
| GET    | `/api/v1/signin/oauth/google`           | Redirect to OAuth Provider | not required |          |
| GET    | `/api/v1/signin/callback/google`        | Sign in                    | not required |          |
<br />

### User

| Method | End Point         | Description      | Auth                 | Details               |
|--------|-------------------|------------------|----------------------|-----------------------|
| POST   | `/api/v1/users`        | Register User    | SignupToken Required | |
| GET    | `/api/v1/users/me`   | Get user profile | AccessToken Requried | [JSON](#apiv1usersme) |
<br />

## API Details
<br />

### `/api/v1/users/me`
#### Sample Valid Response
```json
{
  "username": "user123",
  "displayName": "홍길동",
  "avatarUrl": "https://example.com/avatar/user123.jpg"
}
```
#### Status code
* 200 Ok: Successfully processed
* 401 Unauthorized: Authentication fail
* 400 Bad Request: Successfully authenticated but not be able to find user info

## ERD

![my-real-blog](https://github.com/gukin-han/myrealog-backend/assets/115940366/3d3fcf71-12c6-4ecb-af99-f875d445cfc1)
