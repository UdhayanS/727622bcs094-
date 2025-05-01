
import React, { useEffect, useState } from 'react';
import axios from 'axios';

import './styles/TopUsers.css';

const TopUsers = () => {
  const [users, setUsers] = useState([]);

  useEffect(() => {
    axios.get('http://localhost:8080/api/users')
      .then(response => {
        setUsers(response.data);
      })
      .catch(error => {
        console.error('Error fetching top users:', error);
      });
  }, []);

  return (
    <div className="container my-5">
      <h3 className="text-center mb-4">Top 5 Users by Commented Posts</h3>
      <table className="table table-striped table-bordered shadow-sm">
        <thead>
          <tr>
            <th scope="col">Rank</th>
            <th scope="col">User Name</th>
          </tr>
        </thead>
        <tbody>
          {users.map((user, index) => (
            <tr key={user.id}>
              <td>{index + 1}</td>
              <td>{user.name}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default TopUsers;

