namespace('goldblum.helpers', {}, () => {
  return {
    formatDate(date) {
      return new Date(date).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      });
    },

    formatJson(obj) {
      return JSON.stringify(obj, null, 2);
    },

    truncate(str, length = 50) {
      return str.length > length ? str.substring(0, length) + '...' : str;
    },

    getStatusColor(status) {
      if (status >= 200 && status < 300) return 'success';
      if (status >= 400 && status < 500) return 'warning';
      if (status >= 500) return 'danger';
      return 'info';
    },

    getMethodColor(method) {
      const colors = {
        GET: 'info',
        POST: 'success',
        PUT: 'warning',
        DELETE: 'danger'
      };
      return colors[method] || 'secondary';
    }
  };
});
