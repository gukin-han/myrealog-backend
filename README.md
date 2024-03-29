# Blog Service - MyReaLog
<br />

![image](https://github.com/gukin-han/myrealog-backend/assets/115940366/02a1a07c-2751-488c-b954-f9498171bfe7)

You can visit demo website: [myrealog.com](https://myrealog.com)
<br />

## Introduction
This is a Spring Boot based API Server for managing Blog Contents including User Authentication, Article, Comments(=discussions), Reaction to Articles and Comments etc.   
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
1. Request (always be HTTP POST) from Browser send to Next Server deployed in Vercel.
2. Next Server analyzes it and makes request based on the request from browser to external API server which is Spring Application
3. Spring Application handles and can query Database to retrieve data from MySQL Server installed in RDS.
4. Spring Application then make response and return it to Next Server.
5. Next Server handles response data and renders the page at the specific URL route.
6. Rendered page and data returned to Browser.

## Tech Stacks

![image](https://github.com/gukin-han/myrealog-backend/assets/115940366/ade7329b-2074-4fc5-bb50-25b5b1e704c3)


## ERD

![my-real-blog](https://github.com/gukin-han/myrealog-backend/assets/115940366/3d3fcf71-12c6-4ecb-af99-f875d445cfc1)
* [Click here](https://github.com/gukin-han/myrealog-backend/assets/115940366/3d3fcf71-12c6-4ecb-af99-f875d445cfc1) to see a magnified image.
* ERD can be modified at any moment while it's being developed.
* Lastly Updated at 2023.01.22
<br />

## API Specification
<br />

* Authentication required: ✔
* Authentication not required: ✘

### OAuth
| Method | End Point                            | Description                | Auth | Details  |
|--------|--------------------------------------|----------------------------|------|----------|
| GET    | `/api/v1/signin/oauth/google`        | Redirect to OAuth Provider | ✘    |          |
| GET    | `/api/v1/signin/callback/google`     | Sign in                    | ✘    |          |
<br />

### User
| Method | End Point                                    | Description                      | Auth |
|--------|----------------------------------------------|----------------------------------|------|
| POST   | `/api/v1/users`                              | Register User                    | ✔    |
| GET    | `/api/v1/users/me`                           | Get user with profile            | ✔    |
| DELETE | `/api/v1/users/{userId}`                     | Delete a user by {userId}        | ✔    |
| PATCH  | `/api/v1/users/{userId}/profile/{profileId}` | Update profile elements by {Ids} | ✔    |
<br />

### Article
| Method | End Point                              | Description                                  | Auth |
|--------|----------------------------------------|----------------------------------------------|------|
| POST   | `/api/v1/articles/draft`               | Create a draft or return the existing one    | ✔    |
| GET    | `/api/v1/articles/{username}/{slug}`   | Get an existing article by username and slug |      |
| GET    | `/api/v1/articles/users/{userId}`      | Get All recent articles by {userId}          |      |
| GET    | `/api/v1/articles/recent`              | Get All recent articles                      |      |
| PATCH  | `/api/v1/articles/{articleId}`         | Update an article by {articleId}             | ✔    |
| PATCH  | `/api/v1/articles/{articleId}/publish` | Make a draft publish by {articleId}          | ✔    |
| DELETE | `/api/v1/articles/{articleId}`         | Delete an article by {articleId}             | ✔    |



### Discussion
| Method  | End Point                                       | Description                            | Auth | Details |
|---------|-------------------------------------------------|----------------------------------------|------|---------|
| POST    | `/api/v1/articles/{articleId}/discussions/new`  | Create new discussion in the article   | ✔    |         |
| GET     | `/api/v1/articles/{articleId}/discussions`      | Get All article related discussions    |      |         |
| PATCH   | `/api/v1/discussions/{discussionId}`            | Update given {discussionId} discussion | ✔    |         |
| DELETE  | `/api/v1/discussions/{discussionId}`            | Delete given {discussionId} discussion | ✔    |         |
<br />


### Category
| Method  | End Point                   | Description                | Auth | Details |
|---------|-----------------------------|----------------------------|------|---------|
| POST    | `/api/v1/categories/new`    | Create new category        |      |         |
| GET     | `/api/v1/categories`        | Get All categories         |      |         |
| PATCH   | `/api/v1/categories/{id}`   | Update given {id} category |      |         |
| DELETE  | `/api/v1/cateogries/{id}`   | Delete given {id} category |      |         |
<br />

## API Details
<br />


### Base API Response example
```json
{
    "code": 200,
    "httpStatus": "OK",
    "message": "message example",
    "data": "object",
    "success": true
}
```

* Api response will be wrapped by ApiResponse object

### `/api/v1/users/me`

```json
{
    "code": 200,
    "httpStatus": "OK",
    "success": true,
    "message": "",
    "data": {
        "id": 1,
        "username": "Gukin-Han",
        "email": "gukin.dev@gmail.com",
        "password": null,
        "recentlyPublishedDate": null,
        "profile": {
          "id": 1,
          "displayName": "Gukin-Han",
          "avatarUrl": "",
          "bio": "Gukin-Han"
        }
    }
  
}
```
<br />

#### Status code
<br />

**2xx**
* 200 Ok: Successfully processed
* 203 Created: Successfully created

**4xx**
* 400 Bad Request: Successfully authenticated but not be able to find user info
* 401 Unauthorized: Authentication fail
<br />



