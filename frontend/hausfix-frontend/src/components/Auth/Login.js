import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { TextField, Button, Container, Paper, Typography, Snackbar, Alert } from '@mui/material';
import { login } from '../../services/authService';

const Login = () => {
  const [credentials, setCredentials] = useState({ username: '', password: '' });
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(false);
  const navigate = useNavigate();

  const handleChange = (e) => {
    setCredentials({ ...credentials, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await login(credentials.username, credentials.password);
      setSuccess(true);
      navigate('/'); // Nach dem Login zur Kundenübersicht
    } catch (error) {
      console.error('Login error:', error);
      setError('Login fehlgeschlagen. Bitte überprüfen Sie Ihre Anmeldedaten.');
    }
  };

  return (
    <Container maxWidth="sm" sx={{ marginTop: 4 }}>
      <Paper elevation={3} sx={{ padding: 4 }}>
        <Typography variant="h5" gutterBottom>Login</Typography>
        <form onSubmit={handleSubmit}>
          <TextField
            label="Benutzername"
            name="username"
            value={credentials.username}
            onChange={handleChange}
            required
            fullWidth
            margin="normal"
          />
          <TextField
            label="Passwort"
            name="password"
            type="password"
            value={credentials.password}
            onChange={handleChange}
            required
            fullWidth
            margin="normal"
          />
          <Button variant="contained" color="primary" type="submit" fullWidth sx={{ marginTop: 2 }}>
            Login
          </Button>
        </form>
      </Paper>

      {/* Snackbar für Fehler */}
      <Snackbar open={Boolean(error)} autoHideDuration={6000} onClose={() => setError(null)}>
        <Alert onClose={() => setError(null)} severity="error" sx={{ width: '100%' }}>
          {error}
        </Alert>
      </Snackbar>

      {/* Snackbar für Erfolg */}
      <Snackbar open={success} autoHideDuration={6000} onClose={() => setSuccess(false)}>
        <Alert onClose={() => setSuccess(false)} severity="success" sx={{ width: '100%' }}>
          Login erfolgreich!
        </Alert>
      </Snackbar>
    </Container>
  );
};

export default Login;
