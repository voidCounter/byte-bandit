# API Authentication

The Oakcan API uses JSON Web Tokens (JWT) for authentication. To access protected endpoints, users must register, verify their email, log in, and include the JWT stored in a cookie with their requests. This guide explains each step of the authentication process.

## Authentication Flow

1.  **Register**: Create a user account using the registration endpoint.
2.  **Verify Email**: Confirm your email address via a verification link sent to the MailHog UI (development environment).
3.  **Log In**: Authenticate to receive a JWT, which is stored in an HTTP-only cookie named `access_token`.
4.  **Access Protected Endpoints**: Include the `access_token` cookie in API requests to authenticate with the API Gateway.

## Step 1: Register

Create a new user account.

**Endpoint**: `POST /api/v1/auth/register`

**Request Body**:

```json
{
  "email": "user@example.com",
  "password": "securepassword",
  "fullName": "John Doe"
}
```

**Example Request**:

```bash
curl -X POST http://localhost:8084/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"securepassword","fullName":"John Doe"}'
```

**Response**:

```json
{
  "status": "200",
  "data": {
    "email": "user@example.com",
    "fullName": "John Doe"
  },
  "message": "User registered. Please verify your email."
}
```

**Next**: Check the MailHog UI (`http://localhost:8025`) for a verification email.

## Step 2: Verify Email

After registration, a verification email is sent to the provided email address. In the development environment, access the email via MailHog.

1.  Navigate to `http://localhost:8025`.
2.  Open the verification email and click the provided link (e.g., `http://localhost:8084/api/v1/auth/verify?token=<verification-token>`).

**Response (on successful verification)**:
You’ll be redirected to a success page or receive a JSON response:

```json
{
  "status": "success",
  "message": "Email verified successfully"
}
```

If the verification link expires or is invalid, request a new link via the frontend or contact support.

## Step 3: Log In

Authenticate to receive a JWT.

**Endpoint**: `POST /api/v1/auth/login`

**Request Body**:

```json
{
  "email": "user@example.com",
  "password": "securepassword"
}
```

**Example Request**:

```bash
curl -X POST http://localhost:8084/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"securepassword"}' \
  --cookie-jar cookies.txt
```

**Response (200 OK)**:

```json
{
  "status": "success",
  "data": "true"
  "message": "Login successful"
}
```

The response includes a `Set-Cookie` header with an HTTP-only cookie named `access_token` containing the JWT. Save this cookie (e.g., in `cookies.txt`) for subsequent requests.

!!! Tip 
    If you're using postman, then you don't need to store this cookie manually, as they're handled by default.

## Step 4: Access Protected Endpoints

Include the `access_token` cookie in requests to authenticated endpoints.

**Example Request (Get User Profile)**:

**Endpoint**: `GET /api/v1/users/me`

```bash
curl http://localhost:8084/api/v1/auth/me \
  --cookie cookies.txt
```

**Response (200 OK)**:

```json
{
  "status": "success",
  "data": {
    "email": "user@example.com",
    "name": "John Doe"
  },
  "message": "User is authenticated."
}
```

**Error (401 Unauthorized)**:
If the cookie is missing or the JWT is invalid/expired:

```json
{
  "status": "error",
  "message": "Invalid or missing token",
  "code": 401
}
```

## Notes

* **Cookie Security**: The `access_token` cookie is HTTP-only to prevent JavaScript access, enhancing security.
* **Token Expiry**: The JWT has a limited lifespan (configured in the backend). If it expires, re-authenticate via `/auth/login`.
* **CORS**: Ensure your client is hosted at an allowed origin (e.g., `http://localhost:3000`) as per the `CORS_ALLOWED_ORIGINS` setting in the `.env` file.

## Troubleshooting

* **No Verification Email**: Confirm the `mailhog` service is running (`docker ps | grep mailhog`) and check logs (`docker compose -f docker-compose.apps.yml logs user-service`).
* **Invalid Token**: Ensure the cookie is included and the JWT hasn’t expired. Re-authenticate if needed.
* **CORS Errors**: Verify the `CORS_ALLOWED_ORIGINS` setting in the `.env` file matches your client’s origin.

## Next Steps

* Explore other endpoints in the API Reference.
* Test authentication flows using the frontend (`http://localhost:3000`).
* Monitor service status via the Eureka dashboard (`http://localhost:8761`).
