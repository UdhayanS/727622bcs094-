import React, { useEffect, useState } from "react";
import axios from "axios";

const PopularPosts = () => {
  const [posts, setPosts] = useState([]);
  const [loading, setLoading] = useState(true); // Add loading state
  const [error, setError] = useState(null); // Add error state

  useEffect(() => {
    axios
      .get("http://localhost:8080/api/posts")
      .then((res) => {
        setPosts(res.data);
        setLoading(false);
      })
      .catch((err) => {
        console.error(err);
        setError("Failed to fetch posts");
        setLoading(false);
      });
  }, []);

  if (loading) {
    return <div className="text-center mt-5">Loading trending posts...</div>;
  }

  if (error) {
    return <div className="text-danger text-center mt-5">{error}</div>;
  }

  return (
    <div className="container my-4">
      <h3 className="text-start text-dark mb-4">
        Trending Posts Based on Comments
      </h3>
      <div className="row">
        {posts.map((post) => (
          <div className="col-md-4 mb-3" key={post.id}>
            <div className="card shadow-sm h-100">
              <div className="card-header bg-dark text-white">
                <strong>Post ID:</strong> {post.id}
              </div>
              <div className="card-body">
                <p>
                  <strong>User ID:</strong> {post.user_id}
                </p>
                <p>
                  <strong>Comments:</strong> {post.commentCount}
                </p>
                <p>
                  <strong>Content:</strong> {post.body}
                </p>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default PopularPosts;
