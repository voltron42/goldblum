namespace('goldblum.components.PostsList', {
  'goldblum.apiService': 'apiService',
  'goldblum.helpers': 'helpers'
}, ({ apiService, helpers }) => {
  return class PostsList extends React.Component {
    constructor(props) {
      super(props);
      this.state = {
        posts: [],
        loading: true,
        error: null,
        showForm: false,
        userId: '1',
        title: '',
        content: ''
      };
    }

    componentDidMount() {
      this.loadPosts();
    }

    loadPosts = async () => {
      this.setState({ loading: true, error: null });
      try {
        const data = await apiService.get('/posts');
        this.setState({ posts: data, loading: false });
      } catch (error) {
        this.setState({ error: error.message, loading: false });
      }
    };

    handleDelete = async (id) => {
      if (!window.confirm('Are you sure you want to delete this post?')) return;
      try {
        await apiService.delete(`/posts/${id}`);
        this.loadPosts();
      } catch (error) {
        alert('Error deleting post: ' + error.message);
      }
    };

    handleAddPost = async () => {
      if (!this.state.title || !this.state.content) {
        alert('Please fill in all fields');
        return;
      }
      try {
        await apiService.post('/posts', {
          userId: parseInt(this.state.userId),
          title: this.state.title,
          content: this.state.content
        });
        this.setState({ userId: '1', title: '', content: '', showForm: false });
        this.loadPosts();
      } catch (error) {
        alert('Error creating post: ' + error.message);
      }
    };

    render() {
      const { posts, loading, error, showForm, userId, title, content } = this.state;

      return (
        <div className="card mb-4">
          <div className="card-header bg-primary">
            <div className="d-flex justify-content-between align-items-center">
              <h5 className="mb-0">
                <i className="fas fa-newspaper"></i> Posts
              </h5>
              <button
                className="btn btn-sm btn-success"
                onClick={() => this.setState({ showForm: !showForm })}
              >
                <i className="fas fa-plus"></i> Add Post
              </button>
            </div>
          </div>

          {showForm && (
            <div className="card-body border-bottom">
              <div className="mb-3">
                <label className="form-label">User ID</label>
                <input
                  type="number"
                  className="form-control"
                  placeholder="User ID"
                  value={userId}
                  onChange={(e) => this.setState({ userId: e.target.value })}
                />
              </div>
              <div className="mb-3">
                <label className="form-label">Title</label>
                <input
                  type="text"
                  className="form-control"
                  placeholder="Post title"
                  value={title}
                  onChange={(e) => this.setState({ title: e.target.value })}
                />
              </div>
              <div className="mb-3">
                <label className="form-label">Content</label>
                <textarea
                  className="form-control"
                  placeholder="Post content"
                  value={content}
                  onChange={(e) => this.setState({ content: e.target.value })}
                  rows="3"
                ></textarea>
              </div>
              <button
                className="btn btn-sm btn-success me-2"
                onClick={this.handleAddPost}
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
                Loading posts...
              </div>
            )}

            {error && (
              <div className="alert alert-danger mb-0">
                <strong>Error:</strong> {error}
              </div>
            )}

            {!loading && !error && posts.length === 0 && (
              <div className="text-muted text-center">No posts found</div>
            )}

            {!loading && !error && posts.length > 0 && (
              <div className="list-group">
                {posts.map((post) => (
                  <div key={post.id} className="list-group-item">
                    <div className="d-flex justify-content-between align-items-start">
                      <div className="flex-grow-1">
                        <h6 className="mb-1">{post.title}</h6>
                        <p className="text-muted mb-2 small">
                          <i className="fas fa-user me-1"></i>
                          User ID: {post.userId}
                        </p>
                        <p className="mb-0">{post.content}</p>
                      </div>
                      <div>
                        <span className="badge bg-info me-2">ID: {post.id}</span>
                        <button
                          className="btn btn-sm btn-danger"
                          onClick={() => this.handleDelete(post.id)}
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
