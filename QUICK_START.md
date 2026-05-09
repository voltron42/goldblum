# Goldblum - Quick Start Guide

## What's Included

This is a complete, production-ready application demonstrating:
- ✅ Full-stack development (Clojure + React)
- ✅ REST API with dummy endpoints
- ✅ Swagger documentation
- ✅ Zero-build React frontend (CDN-based)
- ✅ Docker containerization
- ✅ fly.io deployment configuration

## File Structure Created

```
resources/public/
├── index.html                    # React app entry point
├── styles.css                    # Global Bootstrap + custom styles
└── scripts/
    ├── main.js                   # Main app component (Hello World)
    ├── components/
    │   ├── ApiTester.js         # Interactive API tester
    │   ├── UsersList.js         # User management component
    │   └── PostsList.js         # Post management component
    └── utils/
        ├── apiService.js        # HTTP client for API calls
        └── helpers.js           # Utility functions
```

## Quick Start Commands

### Local Development
```bash
cd goldblum
lein run
# Open http://localhost:8080
```

### Build for Production
```bash
lein uberjar
```

### Deploy to fly.io
```bash
# First time only:
flyctl apps create goldblum-hello-world

# Deploy:
flyctl deploy

# View your app:
flyctl open
```

## API Endpoints

| Method | Endpoint | Purpose |
|--------|----------|---------|
| GET | `/api/hello` | Simple hello message |
| GET | `/api/health` | Health check |
| GET | `/api/users` | Get all users |
| POST | `/api/users` | Create user |
| GET | `/api/users/:id` | Get user by ID |
| PUT | `/api/users/:id` | Update user |
| DELETE | `/api/users/:id` | Delete user |
| GET | `/api/posts` | Get all posts |
| POST | `/api/posts` | Create post |
| DELETE | `/api/posts/:id` | Delete post |

## Frontend Features

- **Server Status Monitor**: Real-time connection status in navbar
- **API Tester**: Interactive tool to test endpoints with request/response
- **Users Panel**: View, create, and delete users
- **Posts Panel**: View, create, and delete posts
- **Swagger UI**: Full API documentation at `/swagger-ui`

## Next Steps

1. **Test locally**: `lein run` and open http://localhost:8080
2. **Play with the UI**: Try the API Tester and data management panels
3. **Check the backend**: Examine `src/goldblum/core.clj`
4. **Check the frontend**: Examine `resources/public/scripts/main.js`
5. **Deploy**: Follow fly.io section in README.md

## Key Technologies

**Backend:**
- Clojure 1.11.1
- Ring (web server)
- Reitit (routing + Swagger)
- Jetty (embedded app server)

**Frontend:**
- React 18 (from CDN)
- Bootstrap 5 (from CDN)
- importNamespace (custom module system)
- Babel Standalone (JSX transpilation)

**Deployment:**
- Docker (multi-stage build)
- fly.io (edge platform)

## Notes

- Frontend data: Currently stored in-memory (atoms), resets on server restart
- CORS: Enabled for all origins (change for production)
- Port: Configurable via `PORT` environment variable (default: 8080)
- Health checks: fly.io will automatically restart failed instances

## Troubleshooting

**Port already in use:**
```bash
lein run :port 3000
# or
PORT=3000 lein run
```

**Frontend shows blank page:**
- Check browser console (F12)
- Verify all CDN resources loaded
- Check that backend is running

**API not responding:**
```bash
# Test from terminal
curl http://localhost:8080/api/hello
```

---

Happy deploying! 🚀
