import axios from 'axios';

const BASE_URL = "http://localhost:8080/api";

export const fetchPosts = () => axios.get(`${BASE_URL}/posts`);
export const fetchUser = () => axios.get(`${BASE_URL}/users`);

