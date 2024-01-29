# MyReaLog API Server
**README update history** 
* 2024.01.29 - Api specification and others added
* 2023.12.23 - README initially created
<br />

**Relevant Blog Articles**
* [2. 서버 액션에서 리디렉트하면 레이아웃 두 번 렌더링하는 문제](https://velog.io/@gukin_dev/%EC%84%9C%EB%B2%84-%EC%95%A1%EC%85%98%EC%97%90%EC%84%9C-%EB%A6%AC%EB%94%94%EB%A0%89%ED%8A%B8%ED%95%98%EB%A9%B4-%EB%A0%88%EC%9D%B4%EC%95%84%EC%9B%83-%EB%91%90-%EB%B2%88-%EB%A0%8C%EB%8D%94%EB%A7%81%ED%95%98%EB%8A%94-%EB%AC%B8%EC%A0%9C)
* [1. 블로그 플랫폼 ERD와 엔티티 설계](https://velog.io/@gukin_dev/%EB%B8%94%EB%A1%9C%EA%B7%B8-%ED%94%8C%EB%9E%AB%ED%8F%BC-%EC%97%94%ED%8B%B0%ED%8B%B0-%EC%84%A4%EA%B3%84-%EB%B0%8F-%EA%B5%AC%ED%98%84)
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

<br />

## ERD

![my-real-blog](https://github.com/gukin-han/myrealog-backend/assets/115940366/3d3fcf71-12c6-4ecb-af99-f875d445cfc1)

<br />

## Expected Architecture

![](https://github.com/gukin-han/myrealog-backend/assets/115940366/e193e010-6a6b-49a7-9284-2b35a2744f9b)