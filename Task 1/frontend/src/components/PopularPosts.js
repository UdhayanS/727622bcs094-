import React, { useEffect, useState } from "react";
import axios from "axios";
import "./styles/PostAnalytics.css";

const PopularPosts = () => {
  const [posts, setPosts] = useState([]);

  useEffect(() => {
    axios
      .get("http://localhost:8080/api/posts?type=popular")
      .then((res) => setPosts(res.data))
      .catch((err) => console.error(err));
  }, []);

  return (
    <div className="container my-4">
      <h3 className="text-center mb-4">Most Commented Posts</h3>
      <div className="row">
        {posts.map((post) => (
          <div className="col-md-4 mb-3" key={post.id}>
            <div className="card shadow-sm">
              <div className="card-header">
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
