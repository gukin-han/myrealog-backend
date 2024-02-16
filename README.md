# Blog Service - MyReaLog
<br />

## Introduction
This is a Spring Boot based API Server for managing Blog Contents including User Authentication, Article, Comments(=discussions), Reaction to Articles and Comments etc.   
<br />

You can visit demo website: [myrealog.com](https://myrealog.com)
<br />

* **Period:** 2023.12 ~ (In progress)
* **Scope:** To build a robust and secure service for managing blog contents and to provide place where you can create meaningful blog contents.
* **Technology Stack**
  * Backend: Java 17, Spring Boot 3, MySQL 8 [Repository Link - here](https://github.com/gukin-han/myrealog-backend)
  * Frontend: TypeScript, Next.js 14, shadcn/ui [Repository Link](https://github.com/gukin-han/myrealog-frontend)
  * Infra: AWS Cloud(EC2, RDS), Vercel
<br />

## Key Features

The Key Features followed by:
* User Authentication with JWT and OAuth2 without Spring Security.
* Basic CRUD with Articles, Discussions entities (In progress)
* Further features will be included in the future.   
  <br />

## Architecture
![Screenshot 2024-02-16 at 10 27 56 AM](https://github.com/gukin-han/myrealog-backend/assets/115940366/d108b30b-e285-4eab-91cf-230564f08faf)



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
