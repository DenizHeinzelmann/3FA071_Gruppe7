import api from './api';

export const login = async (username, password) => {
  try {
    const response = await api.post('/users/login', { username, password });
    const { token } = response.data;
    // Token im Local Storage speichern
    localStorage.setItem('jwtToken', token);
    return token;
  } catch (error) {
    throw error;
  }
};

export const logout = () => {
  localStorage.removeItem('jwtToken');
};

export const getToken = () => {
  return localStorage.getItem('jwtToken');
};
