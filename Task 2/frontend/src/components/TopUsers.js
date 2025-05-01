import React, { useEffect, useState } from "react";
import axios from "axios";

import "./TopUsers.css";

const TopUsers = () => {
  const [users, setUsers] = useState([]);

  useEffect(() => {
    axios
      .get("http://localhost:8080/api/users")
      .then((response) => {
        setUsers(response.data);
      })
      .catch((error) => {
        console.error("Error fetching top users:", error);
      });
  }, []);

  return (
    <div className="container my-5">
      <h3 className="text-start mb-4 text-dark">Top 5 Users by Posts</h3>
      <table className="table table-hover bg-white rounded shadow-sm overflow-hidden">
        <tbody>
          {users.map((user, index) => (
            <tr key={user.id} className="align-middle">
              <td className="d-flex align-items-center gap-3">
                <span>{user.name}</span>
              </td>
              <td className="text-end">
                <span className="badge bg-primary rounded-pill px-3 py-2">
                  {user.postCount} Posts
                </span>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default TopUsers;
