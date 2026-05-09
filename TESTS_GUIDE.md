# Unit Tests - Complete Setup

## What Was Built

A comprehensive test suite for the Goldblum application with 17 test suites and 50+ assertions.

## Files Created/Modified

### Test Files
1. **`test/goldblum/core_test.clj`** (220+ lines)
   - Complete API endpoint testing
   - 17 test suites covering all routes
   - Fixtures for state management
   - Response parsing helpers

2. **`test/goldblum/test_utils.clj`** (100+ lines)
   - Reusable test helpers and utilities
   - Request builders (GET, POST, PUT, DELETE)
   - Response assertions
   - Data factory functions
   - Batch operation helpers

### Configuration Files
3. **`project.clj`** (Updated)
   - Added `ring/ring-mock` to `:test` profile
   - Ensures test dependencies only in test mode

### CI/CD
4. **`.github/workflows/tests.yml`** (New)
   - GitHub Actions workflow
   - Runs tests on push and pull requests
   - Caches Maven dependencies
   - Uploads test artifacts

### Documentation
5. **`TESTING.md`** (New)
   - Quick reference for running tests
   - Test patterns and examples

6. **`TEST_COVERAGE.md`** (New)
   - Detailed coverage report
   - Test category breakdown
   - Response code matrix
   - Future enhancements

## Test Suite Breakdown

### Core API Tests (17 suites)

#### Endpoint Tests (2)
- `health-check`: GET /api/health → 200 OK
- `hello-endpoint`: GET /api/hello → 200 OK

#### User CRUD (5)
- `get-all-users`: List users with count verification
- `create-user`: POST creates user, ID increments, stored in atom
- `get-user-by-id`: Retrieve specific user or 404
- `update-user`: PUT modifies user, persists in store
- `delete-user`: DELETE removes user, idempotent

#### Post CRUD (4)
- `get-all-posts`: List posts with content verification
- `create-post`: POST creates post with userId link
- `get-post-by-id`: Retrieve specific post or 404
- `delete-post`: DELETE removes post, idempotent

#### Documentation (1)
- `swagger-endpoints`: Swagger JSON and UI availability

#### HTTP Behavior (2)
- `http-methods`: Invalid methods return 404/405
- `content-type-handling`: JSON headers and parsing

## Test Utilities Available

### Request Builders
```clojure
(get-request "/api/users")
(post-request "/api/users" {:name "Alice"})
(put-request "/api/users/1" {:name "Alice Updated"})
(delete-request "/api/users/1")
```

### Response Helpers
```clojure
(get-status response)           ; Extract HTTP status
(get-body response)             ; Parse JSON body
(is-success? response)          ; Check 2xx status
(is-not-found? response)        ; Check 404
(is-created? response)          ; Check 201
```

### Assertion Helpers
```clojure
(assert-status response 200)
(assert-body-equals response {:status "ok"})
(assert-contains-keys response [:id :name :email])
(assert-json-response response)
```

### Data Factories
```clojure
(user-fixture :id 1 :name "Test" :email "test@example.com")
(post-fixture :id 1 :userId 1 :title "Test" :content "Content")
(create-users handler 5)        ; Create 5 users via API
(create-posts handler 3 1)      ; Create 3 posts for user 1
```

## Running Tests

### Basic Commands
```bash
# Run all tests
lein test

# Run specific test
lein test :only goldblum.core-test/create-user

# Run with verbose output
lein test :verbose

# Run specific namespace
lein test goldblum.core-test
```

### Test Examples

**Basic Endpoint Test:**
```clojure
(deftest hello-endpoint
  (testing "GET /api/hello returns 200 with message"
    (let [response (core/handler (mock/request :get "/api/hello"))]
      (is (= 200 (:status response)))
      (is (= "Hello from Goldblum!" (:message (parse-response response)))))))
```

**CRUD Test:**
```clojure
(deftest create-user
  (testing "POST /api/users creates a new user"
    (let [body {:name "Charlie" :email "charlie@example.com"}
          response (core/handler
                     (-> (mock/request :post "/api/users")
                         (mock/content-type "application/json")
                         (mock/body (json/generate-string body))))
          created-user (parse-response response)]
      (is (= 201 (:status response)))
      (is (= 3 (:id created-user)))
      (is (= "Charlie" (:name created-user))))))
```

**State Verification Test:**
```clojure
(deftest delete-user
  (testing "DELETE /api/users/1 removes user"
    (let [response (core/handler (mock/request :delete "/api/users/1"))]
      (is (= 200 (:status response)))
      (is (= {:deleted 1} (parse-response response)))
      (is (= 1 (count @core/users))))))
```

## Test Coverage Matrix

| Resource | GET | POST | PUT | DELETE |
|----------|-----|------|-----|--------|
| `/api/health` | ✅ | - | - | - |
| `/api/hello` | ✅ | - | - | - |
| `/api/users` | ✅ | ✅ | - | - |
| `/api/users/:id` | ✅ | - | ✅ | ✅ |
| `/api/posts` | ✅ | ✅ | - | - |
| `/api/posts/:id` | ✅ | - | - | ✅ |

## CI/CD Integration

### GitHub Actions Workflow
- **Trigger**: Push to main, Pull requests
- **Job**: `test`
- **Steps**:
  1. Checkout code
  2. Install Clojure + Leiningen
  3. Cache Maven dependencies
  4. Run tests: `lein test`
  5. Build uberjar: `lein uberjar`
  6. Upload artifacts

### Local Pre-commit Testing
```bash
# Before committing
lein test && echo "✓ All tests pass"

# Or add git hook
echo 'lein test' > .git/hooks/pre-commit
chmod +x .git/hooks/pre-commit
```

## Testing Philosophy

1. **Isolation**: Each test resets state using fixtures
2. **No Side Effects**: Tests don't depend on execution order
3. **Clear Names**: Test names describe the behavior
4. **Single Responsibility**: Each test verifies one thing
5. **Real HTTP**: Uses ring-mock for actual request/response testing
6. **Helper Functions**: Reusable utilities reduce boilerplate

## Future Test Enhancements

- [ ] Input validation tests (invalid names, emails, etc.)
- [ ] Concurrent request handling
- [ ] Performance benchmarks
- [ ] Load testing with fixtures
- [ ] Error scenario tests
- [ ] Authentication/authorization (when added)
- [ ] Database integration tests (when added)
- [ ] End-to-end browser tests (Playwright, Selenium)

## Test Maintenance

### Adding New Tests
1. Create test in `test/goldblum/core_test.clj`
2. Use `deftest` with descriptive name
3. Use `testing` blocks for grouping
4. Use fixture reset for state management
5. Leverage helper functions from `test_utils.clj`

### Debugging Tests
```bash
# Run with stack traces
lein test :verbose

# Run single test for debugging
lein test :only goldblum.core-test/specific-test

# Print intermediate values
(println "Debug:" value)  ; Shows in test output
```

## Key Dependencies

- **ring-mock** (0.4.0): Mock HTTP requests
- **cheshire** (5.11.0): JSON serialization (already in main deps)
- **clojure.test**: Built-in test framework
