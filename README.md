# MyReaLog API Server
**README update history** 
* 2024.01.29 - Api specification and others added
* 2023.12.23 - README initially created
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

### Category
| Method  | End Point                   | Description                | Auth | Details |
|---------|-----------------------------|----------------------------|------|---------|
| POST    | `/api/v1/categories/new`    | Create new category        |      |         |
| GET     | `/api/v1/categories`        | Get All categories         |      |         |
| PATCH   | `/api/v1/categories/{id}`   | Update given {id} category |      |         |
| DELETE  | `/api/v1/cateogries/{id}`   | Delete given {id} category |      |         |


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

<br />

## ERD

![my-real-blog](https://github.com/gukin-han/myrealog-backend/assets/115940366/3d3fcf71-12c6-4ecb-af99-f875d445cfc1)

<br />

## Expected Architecture

![](https://github.com/gukin-han/myrealog-backend/assets/115940366/e193e010-6a6b-49a7-9284-2b35a2744f9b)