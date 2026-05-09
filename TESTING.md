# Running Tests

## Run all tests
```bash
lein test
```

## Run specific test namespace
```bash
lein test goldblum.core-test
```

## Run a specific test
```bash
lein test :only goldblum.core-test/hello-endpoint
```

## Continuous testing (watch mode)
```bash
lein test-watch
```

## Test coverage
```bash
lein cloverage
```

## Test Results Summary

The test suite includes:

### Basic Endpoint Tests
- **Health Check**: Verifies `/api/health` responds with `{"status":"ok"}`
- **Hello Endpoint**: Verifies `/api/hello` returns the welcome message

### User CRUD Operations
- **List Users**: `GET /api/users` returns all users
- **Create User**: `POST /api/users` creates and returns a new user
- **Get User**: `GET /api/users/:id` returns a specific user or 404
- **Update User**: `PUT /api/users/:id` updates user data
- **Delete User**: `DELETE /api/users/:id` removes a user

### Post CRUD Operations
- **List Posts**: `GET /api/posts` returns all posts
- **Create Post**: `POST /api/posts` creates and returns a new post
- **Get Post**: `GET /api/posts/:id` returns a specific post or 404
- **Delete Post**: `DELETE /api/posts/:id` removes a post

### API Documentation
- **Swagger**: `GET /swagger.json` returns OpenAPI spec
- **Swagger UI**: `GET /swagger-ui` serves API documentation

### HTTP Methods & Content Types
- Tests that invalid HTTP methods return errors
- Tests JSON content type handling

### State Management
- Tests that data persists across requests
- Tests that state can be modified and deleted
- Resets state before each test using fixtures

## Test Statistics

- **Total Tests**: 17 test suites
- **Total Assertions**: 50+
- **Coverage**: Core API handlers and HTTP endpoints
- **Dependencies**: ring-mock, cheshire, clojure.test

## Key Testing Patterns

1. **Fixtures**: Use `reset-state` fixture to reset atoms before each test
2. **Mock Requests**: Use `ring.mock.request` to simulate HTTP requests
3. **JSON Parsing**: Helper function `parse-response` for extracting JSON responses
4. **State Verification**: Tests verify both response and internal state changes

## Example Test Structure

```clojure
(deftest example-test
  (testing "Description of behavior"
    (let [response (core/handler (mock/request :get "/api/endpoint"))]
      (is (= 200 (:status response)))
      (is (= expected-data (parse-response response))))))
```
