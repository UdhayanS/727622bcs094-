import React from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import TopUsers from './components/TopUsers';
import PopularPosts from './components/PopularPosts';
import LatestPosts from './components/LatestPosts';
import 'bootstrap/dist/css/bootstrap.min.css';



function App() {
  return (
    <Router>
      <nav className="navbar navbar-expand-lg navbar-dark bg-primary px-4">
        <Link className="navbar-brand" to="/">Social Analytics</Link>
        <div className="navbar-nav">
          <Link className="nav-link" to="/top-users">Top Users</Link>
          <Link className="nav-link" to="/posts/popular">Popular Posts</Link>
          <Link className="nav-link" to="/posts/latest">Latest Posts</Link>
        </div>
      </nav>

      <Routes>
        <Route path="/" element={<TopUsers />} />
        <Route path="/top-users" element={<TopUsers />} />
        <Route path="/posts/popular" element={<PopularPosts />} />
        <Route path="/posts/latest" element={<LatestPosts />} />
      </Routes>
    </Router>
  );
}

export default App;



// import React from 'react';
// import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
// import 'bootstrap/dist/css/bootstrap.min.css';
// import TopUsers from './components/TopUsers';
// import PostAnalytics from './components/PostAnalytics';

// function App() {
//   return (
//     <Router>
//       <nav className="navbar navbar-expand-lg navbar-dark bg-primary px-4">
//         <Link className="navbar-brand" to="/">Social Analytics</Link>
//         <div className="navbar-nav">
//           <Link className="nav-link" to="/top-users">Top Users</Link>
//           <Link className="nav-link" to="/posts">Popular Posts</Link>
//         </div>
//       </nav>
//       <Routes>
//         <Route path="/" element={<TopUsers />} />
//         <Route path="/top-users" element={<TopUsers />} />
//         <Route path="/posts" element={<PostAnalytics />} />
//       </Routes>
//     </Router>
//   );
// }



// export default App;
