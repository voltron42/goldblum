# Goldblum - Hello World Demo for fly.io

A complete, production-ready "Hello World" application demonstrating:
- **Backend**: Clojure/Ring REST API with Swagger documentation
- **Frontend**: Zero-build React application (CDN-based, no npm)
- **Deployment**: fly.io with Docker

Perfect for learning how to deploy full-stack applications to fly.io.

## Project Structure

```
goldblum/
├── src/goldblum/
│   └── core.clj              # Ring server, API routes, Swagger setup
├── resources/public/
│   ├── index.html            # React app entry point
│   ├── styles.css            # Global styles
│   └── scripts/
│       ├── main.js           # Main React component
│       ├── components/       # React components
│       │   ├── ApiTester.js
│       │   ├── UsersList.js
│       │   └── PostsList.js
│       └── utils/            # Utility modules
│           ├── apiService.js # API client
│           └── helpers.js    # Helper functions
├── project.clj               # Clojure dependencies
├── Dockerfile                # Multi-stage build
├── fly.toml                  # fly.io configuration
└── README.md                 # This file
```

## Features

### Backend API
- **REST Endpoints**: Users, Posts (full CRUD operations)
- **Health Check**: `/api/health` for monitoring
- **Swagger UI**: Interactive API documentation at `/swagger-ui`
- **CORS Support**: Cross-origin requests enabled
- **JSON Processing**: Automatic JSON serialization/deserialization
- **Error Handling**: Graceful HTTP error responses

### Frontend
- **Zero-Build Architecture**: No npm, webpack, or build pipeline
- **React 18 (CDN)**: Latest React from unpkg CDN
- **Bootstrap 5**: Responsive, professional UI
- **API Tester Panel**: Interactive tool to test endpoints
- **Real-time Status**: Server connection monitoring
- **Component Architecture**: Modular, namespace-based organization

### Deployment
- **Docker**: Multi-stage build for optimized image size
- **fly.io**: One-command deployment
- **Health Checks**: Automatic monitoring and restarts
- **Port Configuration**: Configurable via `PORT` environment variable

### Testing
- **Unit Tests**: Comprehensive test suite for all API endpoints
- **Integration Tests**: Full HTTP request/response testing
- **Fixtures**: Automatic state reset between tests
- **Coverage**: Users, Posts, Health, Hello endpoints + CRUD operations

## Getting Started

### Local Development

#### 1. Install Dependencies
```bash
# Install Clojure/Leiningen
# macOS: brew install leiningen
# Or download from: https://leiningen.org/
```

#### 2. Run the Development Server
```bash
cd goldblum
lein run
```

The application will start on `http://localhost:8080`

#### 3. Run Tests
```bash
lein test
```

See [TESTING.md](TESTING.md) for detailed testing information.

#### 4. Access the Application
- **Frontend**: http://localhost:8080/
- **Swagger UI**: http://localhost:8080/swagger-ui
- **API Base**: http://localhost:8080/api

### API Examples

#### Hello Endpoint
```bash
curl http://localhost:8080/api/hello
# Response: {"message":"Hello from Goldblum!"}
```

#### Get All Users
```bash
curl http://localhost:8080/api/users
```

#### Create a User
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"Alice","email":"alice@example.com"}'
```

#### Get User by ID
```bash
curl http://localhost:8080/api/users/1
```

#### Update User
```bash
curl -X PUT http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"Alice Updated","email":"alice.updated@example.com"}'
```

#### Delete User
```bash
curl -X DELETE http://localhost:8080/api/users/1
```

#### Get All Posts
```bash
curl http://localhost:8080/api/posts
```

#### Create a Post
```bash
curl -X POST http://localhost:8080/api/posts \
  -H "Content-Type: application/json" \
  -d '{"userId":1,"title":"Hello","content":"Welcome to Goldblum"}'
```

#### Health Check
```bash
curl http://localhost:8080/api/health
# Response: {"status":"ok"}
```

## Building for Production

### Build Uberjar
```bash
lein uberjar
```

This creates a standalone JAR file in `target/`:
```
target/goldblum-0.1.0-SNAPSHOT-standalone.jar
```

### Build Docker Image
```bash
docker build -t goldblum:latest .
```

### Run Docker Container
```bash
docker run -p 8080:8080 -e PORT=8080 goldblum:latest
```

## Deploying to fly.io

### Prerequisites
- [fly.io CLI](https://fly.io/docs/hands-on/install-flyctl/)
- Account on fly.io

### Deploy Steps

#### 1. Install flyctl
```bash
# macOS
brew install flyctl

# Or download from: https://fly.io/docs/hands-on/install-flyctl/
```

#### 2. Authenticate
```bash
flyctl auth login
```

#### 3. Create fly.io App (First Time Only)
```bash
flyctl apps create goldblum-hello-world
```

#### 4. Deploy
```bash
flyctl deploy
```

The deployment will:
1. Build the Docker image
2. Push to fly.io registry
3. Deploy to the edge
4. Run health checks
5. Activate the new version

#### 5. View Your Application
```bash
flyctl open
```

Your app is now live at: `https://goldblum-hello-world.fly.dev`

### Monitor Deployment
```bash
# View logs
flyctl logs

# Check app status
flyctl status

# Scale machines
flyctl scale count 2

# SSH into machine
flyctl ssh console
```

## Technology Stack

### Backend
- **Language**: Clojure 1.11.1
- **Web Framework**: Ring (HTTP middleware/adapter)
- **Routing**: Reitit (fast HTTP route matching)
- **API Documentation**: Swagger/OpenAPI with Reitit Swagger
- **Serialization**: Cheshire (JSON)
- **Server**: Jetty (embedded web server)
- **Logging**: SLF4J + Logback

### Frontend
- **Library**: React 18 (CDN via unpkg)
- **Styling**: Bootstrap 5 (CDN)
- **Module System**: importNamespace (custom namespace resolution)
- **Transpilation**: Babel Standalone (JSX at runtime)
- **Build**: Zero-build (runs directly in browser)

### DevOps
- **Containerization**: Docker (multi-stage build)
- **Platform**: fly.io (edge computing platform)
- **Base Image**: Alpine Linux + Eclipse Temurin JRE 21

## Environment Variables

```bash
PORT=8080    # HTTP port (default: 8080)
```

## Troubleshooting

### Application won't start
```bash
# Check logs
flyctl logs --app goldblum-hello-world

# Verify the image was built correctly
docker build -t goldblum:test .
docker run -p 8080:8080 goldblum:test
```

### Frontend shows blank page
1. Check browser console for errors (F12)
2. Verify all CDN resources are loading (Network tab)
3. Check API connection status (green indicator in navbar)

### API endpoints not responding
1. Verify backend is running: `curl http://localhost:8080/api/health`
2. Check for CORS errors in browser console
3. Verify request format: must be JSON with `Content-Type: application/json`

### Deployment stuck
```bash
# Cancel current deployment
flyctl deploy --strategy immediate

# Force a rebuild
flyctl deploy --build-only
```

## Development Tips

### Adding a New API Endpoint
1. Add handler function in `src/goldblum/core.clj`
2. Define route with Swagger documentation
3. Test with curl or the API Tester panel

### Adding a New React Component
1. Create new file in `resources/public/scripts/components/`
2. Define namespace with `namespace()` function
3. Import dependencies using `'namespace.Path': 'LocalName'`
4. Add script tag to `index.html`

### Modifying Styles
Edit `resources/public/styles.css` (automatically reloaded)

### Testing in Production
- Use `flyctl logs` to tail logs
- Use `flyctl ssh console` for direct server access
- Use `flyctl deploy --strategy immediate` for instant rollout

## Performance Considerations

- **Frontend**: CDN delivery makes React/Bootstrap load fast
- **Backend**: Jetty can handle thousands of concurrent connections
- **Database**: Currently in-memory (atom-based storage)
- **Scaling**: fly.io auto-scales based on metrics

## Security Notes

- ⚠️ **CORS**: Currently allows all origins (not for production)
- ⚠️ **In-Memory Storage**: Data lost on restart (use a database)
- ⚠️ **No Authentication**: Add auth middleware before production
- ✅ **HTTPS**: Enabled automatically by fly.io

## Next Steps

To extend this for production:

1. **Add Database**: Replace atoms with PostgreSQL/MongoDB
2. **Add Authentication**: JWT tokens or session-based auth
3. **Add Validation**: Input validation for all endpoints
4. **Add Testing**: Unit and integration tests
5. **Restrict CORS**: Limit to specific origins
6. **Add Logging**: Structured JSON logging
7. **Add Monitoring**: Error tracking (Sentry), metrics (Datadog)
8. **Add CI/CD**: GitHub Actions for automated deployments

## Resources

- [fly.io Documentation](https://fly.io/docs/)
- [Clojure Docs](https://clojure.org/)
- [Ring Documentation](https://github.com/ring-clojure/ring)
- [Reitit Documentation](https://cljdoc.org/d/metosin/reitit/)
- [React Documentation](https://react.dev/)
- [Bootstrap Documentation](https://getbootstrap.com/)

## License

Copyright © 2026

This program and the accompanying materials are made available under the terms of the Eclipse Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0.
