namespace('goldblum.Main', {
  'goldblum.apiService': 'apiService',
  'goldblum.helpers': 'helpers',
  'goldblum.components.ApiTester': 'ApiTester',
  'goldblum.components.UsersList': 'UsersList',
  'goldblum.components.PostsList': 'PostsList'
}, ({ apiService, helpers, ApiTester, UsersList, PostsList }) => {
  return class Main extends React.Component {
    constructor(props) {
      super(props);
      this.state = {
        serverStatus: 'checking...',
        isOnline: false
      };
    }

    componentDidMount() {
      this.checkServerStatus();
      // Check status every 30 seconds
      this.statusInterval = setInterval(this.checkServerStatus, 30000);
    }

    componentWillUnmount() {
      if (this.statusInterval) {
        clearInterval(this.statusInterval);
      }
    }

    checkServerStatus = async () => {
      const isOnline = await apiService.health();
      this.setState({
        isOnline,
        serverStatus: isOnline ? 'Online' : 'Offline'
      });
    };

    render() {
      const { serverStatus, isOnline } = this.state;

      return (
        <>
          {/* Navigation */}
          <nav className="navbar navbar-expand-lg navbar-dark bg-primary mb-4">
            <div className="container-fluid">
              <span className="navbar-brand">
                <i className="fas fa-gem me-2"></i>Goldblum
              </span>
              <span className="ms-auto text-white">
                <i className={`fas fa-circle me-2 ${isOnline ? 'text-success' : 'text-danger'}`}></i>
                Server: <strong>{serverStatus}</strong>
              </span>
            </div>
          </nav>

          <div className="container-lg py-4">
            {/* Hero Section */}
            <div className="bg-primary text-white p-5 mb-4 rounded">
              <h1 className="mb-3">
                <i className="fas fa-wave-square me-2"></i>
                Hello World! 🎉
              </h1>
              <p className="lead mb-0">
                A zero-build React application with Clojure/Ring backend deployed to fly.io
              </p>
            </div>

            {/* Status Alert */}
            <div className={`alert alert-${isOnline ? 'success' : 'warning'} alert-dismissible fade show`} role="alert">
              <i className={`fas fa-${isOnline ? 'check-circle' : 'exclamation-circle'} me-2`}></i>
              <strong>{isOnline ? 'Connected!' : 'Connection Issue'}</strong>
              {isOnline ? (
                ' Your backend API is running and responding to requests.'
              ) : (
                ' The backend API is not responding. Check the server logs.'
              )}
              <button type="button" className="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>

            {/* Main Content Grid */}
            <div className="row">
              <div className="col-lg-12">
                <ApiTester />
              </div>
            </div>

            <div className="row">
              <div className="col-lg-6">
                <UsersList />
              </div>
              <div className="col-lg-6">
                <PostsList />
              </div>
            </div>

            {/* Info Section */}
            <div className="card mt-4">
              <div className="card-header bg-primary">
                <h5 className="mb-0">
                  <i className="fas fa-info-circle"></i> About This Application
                </h5>
              </div>
              <div className="card-body">
                <div className="row">
                  <div className="col-md-6 mb-3">
                    <h6>Backend Stack</h6>
                    <ul className="list-unstyled small">
                      <li><i className="fas fa-check text-success me-2"></i>Clojure & Ring (Web Server)</li>
                      <li><i className="fas fa-check text-success me-2"></i>Reitit (Routing)</li>
                      <li><i className="fas fa-check text-success me-2"></i>Swagger UI (API Documentation)</li>
                      <li><i className="fas fa-check text-success me-2"></i>CORS Support</li>
                    </ul>
                  </div>
                  <div className="col-md-6 mb-3">
                    <h6>Frontend Stack</h6>
                    <ul className="list-unstyled small">
                      <li><i className="fas fa-check text-success me-2"></i>React 18 (CDN)</li>
                      <li><i className="fas fa-check text-success me-2"></i>Zero-Build Architecture</li>
                      <li><i className="fas fa-check text-success me-2"></i>Bootstrap 5 CSS</li>
                      <li><i className="fas fa-check text-success me-2"></i>importNamespace Modules</li>
                    </ul>
                  </div>
                </div>

                <hr />

                <div className="row">
                  <div className="col-md-6">
                    <h6>API Endpoints</h6>
                    <ul className="list-unstyled small">
                      <li><span className="badge bg-info">GET</span>/api/hello</li>
                      <li><span className="badge bg-info">GET</span>/api/health</li>
                      <li><span className="badge bg-info">GET</span>/api/users</li>
                      <li><span className="badge bg-success">POST</span>/api/users</li>
                      <li><span className="badge bg-info">GET</span>/api/users/:id</li>
                      <li><span className="badge bg-warning">PUT</span>/api/users/:id</li>
                      <li><span className="badge bg-danger">DELETE</span>/api/users/:id</li>
                      <li><span className="badge bg-info">GET</span>/api/posts</li>
                      <li><span className="badge bg-success">POST</span>/api/posts</li>
                      <li><span className="badge bg-danger">DELETE</span>/api/posts/:id</li>
                    </ul>
                  </div>
                  <div className="col-md-6">
                    <h6>Quick Links</h6>
                    <ul className="list-unstyled small">
                      <li>
                        <a href="/swagger-ui" target="_blank" rel="noopener noreferrer">
                          <i className="fas fa-external-link-alt me-2"></i>Swagger UI Documentation
                        </a>
                      </li>
                      <li>
                        <a href="/swagger.json" target="_blank" rel="noopener noreferrer">
                          <i className="fas fa-external-link-alt me-2"></i>OpenAPI Specification
                        </a>
                      </li>
                      <li className="mt-2">
                        <strong>Test the API:</strong> Use the API Tester panel above to make requests
                      </li>
                    </ul>
                  </div>
                </div>
              </div>
            </div>

            {/* Footer */}
            <div className="text-center mt-4 py-3 text-muted border-top">
              <p className="mb-1">
                <i className="fas fa-code me-2"></i>Built with Clojure, React, and Bootstrap
              </p>
              <p className="mb-0">
                Deployed to <strong>fly.io</strong> for serverless deployment
              </p>
            </div>
          </div>
        </>
      );
    }
  };
});
