# Messenger Project Status (as of 2026-03-13)

## What is already done

- Spring Boot backend scaffolded with Java 21 and Gradle.
- Core layers are in place:
  - `controller`: `UserController`
  - `service`: `UserService`, `OtpService` with `UserServiceImpl`, `OtpServiceImpl`
  - `repository`: `UserRepository`, `OtpRepository`
  - `models`: `Users`, `Otp`
  - `dto` + mapper: `UserCreateDTO`, `OtpVerifyDto`, `UserMapper`
- User onboarding flow implemented:
  - `POST /api/v1/register` creates user and triggers OTP via SMS/email.
  - Password is encoded via `PasswordEncoder` before saving.
- OTP flow implemented:
  - OTP generation (`OtpGenerator`), persistence (`Otp` entity), delivery (`SmsService` / mail sender), and verification (`verifyOtp`).
  - OTP supports expiry and one-time use flags.
- Basic login method exists in service and controller (`/api/v1/login`).
- Security baseline exists in `WebSecurityConfig`.
- WebSocket setup started:
  - STOMP endpoint `/ws` with SockJS fallback.
  - Inbound auth interceptor (`WebSocketAuthInterceptor`).
  - Presence listener (`WebSocketEventListener`) that broadcasts ONLINE/OFFLINE events on `/topic/presence`.

## Current architecture snapshot

- API base path: `/api` (from `application.properties`).
- REST routes currently in `UserController`:
  - `/v1/register`
  - `/v1/verifyOtp`
  - `/v1/login`
- Data store: PostgreSQL via Spring Data JPA.
- Async external SMS integration: TextBee API (`SmsService` with `@Async`).
- Email OTP via SMTP (`JavaMailSender`).
- Auth model currently uses Spring Security config + password encoding, but not full token/session auth.

## Gaps and risks to address next

1. **Credential exposure (critical)**
   - `application.properties` currently contains plaintext DB password, SMTP app password, and TextBee API key.

2. **Auth/login endpoint mismatch and weak request contract**
   - Security permits `/api/v1/login`, but controller mapping is `/v1/login` (actual URL becomes `/api/v1/login` because of context path; still the method lacks `@RequestBody`/`@RequestParam` and explicit DTO).
   - `login(String email, String password)` is not clearly bound; this can fail or behave unexpectedly.

3. **OTP verification only updates phone-verified flag**
   - `verifyOtp` currently checks `findByMobNumber(identifier)` only.
   - If OTP is sent to email, user verification flag is not updated.

4. **Potential null/error paths in login**
   - `findByEmail` returns `Users` directly; if user not found, null can cause runtime errors.

5. **WebSocket auth likely incomplete for real clients**
   - Interceptor expects authenticated `Principal` on CONNECT, but no implemented auth handshake/token flow is visible.

6. **Insufficient test coverage**
   - Only `contextLoads` exists.

7. **Dev-only persistence config in active properties**
   - `spring.jpa.hibernate.ddl-auto=create-drop` will drop schema at restart.

8. **Minor code hygiene**
   - `NumberVerifyHandler` is injected in `OtpServiceImpl` but unused.
   - `OtpVerifyDto` uses JPA `@Column` annotations though it is not an entity.

## Recommended next steps (priority order)

### P0 - Security and configuration hygiene

- Move all secrets from `application.properties` to environment variables.
- Add profile-based configs (`application-dev.properties`, `application-prod.properties`).
- Rotate compromised credentials immediately (DB, SMTP, TextBee).

### P1 - Correctness in auth + OTP

- Introduce `LoginRequest`/`LoginResponse` DTOs and make `/v1/login` explicit with `@RequestBody`.
- Return proper HTTP status codes for invalid login and OTP cases.
- Update OTP verification logic to handle both phone and email identifiers consistently.
- Add guards for missing users (`Optional`-based repository lookup).

### P1 - WebSocket authentication completion

- Decide auth strategy for STOMP CONNECT (JWT in headers or session-based auth).
- Parse and validate token in `WebSocketAuthInterceptor` before allowing CONNECT.
- Add tests for connect/subscribe flows.

### P2 - Reliability and maintainability

- Add rate limiting / resend cooldown for OTP.
- Add cleanup strategy for expired OTP records.
- Replace generic string responses with structured API response DTOs.
- Add global exception handling (`@ControllerAdvice`).

### P2 - Testing baseline

- Add unit tests for `UserServiceImpl` and `OtpServiceImpl`.
- Add integration tests for `/register`, `/verifyOtp`, and `/login`.
- Add a happy-path + failure-path test matrix for OTP expiry, reused OTP, wrong OTP.

## Suggested immediate execution plan

1. Security cleanup + credentials rotation.
2. Fix login endpoint contract with DTO + tests.
3. Fix OTP verify logic for both identifier types + tests.
4. Complete WebSocket auth handshake (JWT/session).
5. Expand integration test coverage before adding new features.

