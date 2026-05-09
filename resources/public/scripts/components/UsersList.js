namespace('goldblum.components.UsersList', {
  'goldblum.apiService': 'apiService',
  'goldblum.helpers': 'helpers'
}, ({ apiService, helpers }) => {
  return class UsersList extends React.Component {
    constructor(props) {
      super(props);
      this.state = {
        users: [],
        loading: true,
        error: null,
        showForm: false,
        name: '',
        email: ''
      };
    }

    componentDidMount() {
      this.loadUsers();
    }

    loadUsers = async () => {
      this.setState({ loading: true, error: null });
      try {
        const data = await apiService.get('/users');
        this.setState({ users: data, loading: false });
      } catch (error) {
        this.setState({ error: error.message, loading: false });
      }
    };

    handleDelete = async (id) => {
      if (!window.confirm('Are you sure you want to delete this user?')) return;
      try {
        await apiService.delete(`/users/${id}`);
        this.loadUsers();
      } catch (error) {
        alert('Error deleting user: ' + error.message);
      }
    };

    handleAddUser = async () => {
      if (!this.state.name || !this.state.email) {
        alert('Please fill in all fields');
        return;
      }
      try {
        await apiService.post('/users', {
          name: this.state.name,
          email: this.state.email
        });
        this.setState({ name: '', email: '', showForm: false });
        this.loadUsers();
      } catch (error) {
        alert('Error creating user: ' + error.message);
      }
    };

    render() {
      const { users, loading, error, showForm, name, email } = this.state;

      return (
        <div className="card mb-4">
          <div className="card-header bg-primary">
            <div className="d-flex justify-content-between align-items-center">
              <h5 className="mb-0">
                <i className="fas fa-users"></i> Users
              </h5>
              <button
                className="btn btn-sm btn-success"
                onClick={() => this.setState({ showForm: !showForm })}
              >
                <i className="fas fa-plus"></i> Add User
              </button>
            </div>
          </div>

          {showForm && (
            <div className="card-body border-bottom">
              <div className="row">
                <div className="col-md-6">
                  <input
                    type="text"
                    className="form-control mb-2"
                    placeholder="Name"
                    value={name}
                    onChange={(e) => this.setState({ name: e.target.value })}
                  />
                </div>
                <div className="col-md-6">
                  <input
                    type="email"
                    className="form-control mb-2"
                    placeholder="Email"
                    value={email}
                    onChange={(e) => this.setState({ email: e.target.value })}
                  />
                </div>
              </div>
              <button
                className="btn btn-sm btn-success me-2"
                onClick={this.handleAddUser}
              >
                Save
              </button>
              <button
                className="btn btn-sm btn-secondary"
                onClick={() => this.setState({ showForm: false })}
              >
                Cancel
              </button>
            </div>
          )}

          <div className="card-body">
            {loading && (
              <div className="text-center">
                <span className="spinner-border me-2"></span>
                Loading users...
              </div>
            )}

            {error && (
              <div className="alert alert-danger mb-0">
                <strong>Error:</strong> {error}
              </div>
            )}

            {!loading && !error && users.length === 0 && (
              <div className="text-muted text-center">No users found</div>
            )}

            {!loading && !error && users.length > 0 && (
              <div className="list-group">
                {users.map((user) => (
                  <div key={user.id} className="list-group-item">
                    <div className="d-flex justify-content-between align-items-start">
                      <div>
                        <h6 className="mb-1">{user.name}</h6>
                        <p className="text-muted mb-0 small">{user.email}</p>
                      </div>
                      <div>
                        <span className="badge bg-info me-2">ID: {user.id}</span>
                        <button
                          className="btn btn-sm btn-danger"
                          onClick={() => this.handleDelete(user.id)}
                        >
                          <i className="fas fa-trash"></i>
                        </button>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      );
    }
  };
});
