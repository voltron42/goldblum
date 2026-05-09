namespace('goldblum.components.ApiTester', {
  'goldblum.apiService': 'apiService',
  'goldblum.helpers': 'helpers'
}, ({ apiService, helpers }) => {
  const { useState } = React;

  return class ApiTester extends React.Component {
    constructor(props) {
      super(props);
      this.state = {
        endpoint: '/hello',
        method: 'GET',
        requestBody: '',
        response: null,
        loading: false,
        error: null
      };
    }

    handleEndpointChange = (e) => {
      this.setState({ endpoint: e.target.value });
    };

    handleMethodChange = (e) => {
      this.setState({ method: e.target.value });
    };

    handleBodyChange = (e) => {
      this.setState({ requestBody: e.target.value });
    };

    handleExecute = async () => {
      this.setState({ loading: true, error: null, response: null });
      try {
        let result;
        switch (this.state.method) {
          case 'GET':
            result = await apiService.get(this.state.endpoint);
            break;
          case 'POST':
            result = await apiService.post(this.state.endpoint, JSON.parse(this.state.requestBody));
            break;
          case 'PUT':
            result = await apiService.put(this.state.endpoint, JSON.parse(this.state.requestBody));
            break;
          case 'DELETE':
            result = await apiService.delete(this.state.endpoint);
            break;
          default:
            result = null;
        }
        this.setState({ response: result, loading: false });
      } catch (error) {
        this.setState({ error: error.message, loading: false });
      }
    };

    render() {
      const { endpoint, method, requestBody, response, loading, error } = this.state;

      return (
        <div className="card mb-4">
          <div className="card-header bg-primary">
            <h5 className="mb-0">
              <i className="fas fa-code"></i> API Tester
            </h5>
          </div>
          <div className="card-body">
            <div className="row mb-3">
              <div className="col-md-3">
                <label className="form-label">Method</label>
                <select
                  className="form-select"
                  value={method}
                  onChange={this.handleMethodChange}
                  disabled={loading}
                >
                  <option value="GET">GET</option>
                  <option value="POST">POST</option>
                  <option value="PUT">PUT</option>
                  <option value="DELETE">DELETE</option>
                </select>
              </div>
              <div className="col-md-9">
                <label className="form-label">Endpoint</label>
                <input
                  type="text"
                  className="form-control"
                  placeholder="/api/hello"
                  value={endpoint}
                  onChange={this.handleEndpointChange}
                  disabled={loading}
                />
              </div>
            </div>

            {(method === 'POST' || method === 'PUT') && (
              <div className="mb-3">
                <label className="form-label">Request Body (JSON)</label>
                <textarea
                  className="form-control font-monospace"
                  placeholder='{"key": "value"}'
                  value={requestBody}
                  onChange={this.handleBodyChange}
                  rows="4"
                  disabled={loading}
                  style={{ fontSize: '0.875rem' }}
                ></textarea>
              </div>
            )}

            <button
              className="btn btn-primary"
              onClick={this.handleExecute}
              disabled={loading}
            >
              {loading ? (
                <>
                  <span className="spinner-border spinner-border-sm me-2"></span>
                  Executing...
                </>
              ) : (
                <>
                  <i className="fas fa-play me-2"></i>
                  Execute
                </>
              )}
            </button>

            {error && (
              <div className="response-box error mt-3">
                <strong className="text-danger">Error:</strong> {error}
              </div>
            )}

            {response && (
              <div className="response-box success">
                <strong className="text-success">Response:</strong>
                <pre className="mb-0">{helpers.formatJson(response)}</pre>
              </div>
            )}
          </div>
        </div>
      );
    }
  };
});
