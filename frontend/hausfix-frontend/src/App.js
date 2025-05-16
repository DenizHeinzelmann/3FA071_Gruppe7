// src/App.js
import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { CssBaseline, Box, Container, Typography, ThemeProvider, createTheme } from '@mui/material';
import Navbar from './components/Layout/Navbar';
import Home from './components/Home';
import CustomerList from './components/Customers/CustomerList';
import CustomerForm from './components/Customers/CustomerForm';
import CustomerDetail from './components/Customers/CustomerDetail';
import ReadingList from './components/Readings/ReadingList';
import ReadingForm from './components/Readings/ReadingForm';
import ReadingDetail from './components/Readings/ReadingDetail';
import ImportExport from './components/ImportExport/ImportExport';
import Login from './components/Auth/Login';
import PrivateRoute from './components/Auth/PrivateRoute';
import Analysis from './components/Analysis';
import { Divider } from '@mui/material';

function App() {
  const [darkMode, setDarkMode] = useState(false);

  useEffect(() => {
    const stored = localStorage.getItem('darkMode');
    if (stored) setDarkMode(stored === 'true');
  }, []);

  const toggleDarkMode = () => {
    setDarkMode((prev) => {
      localStorage.setItem('darkMode', !prev);
      return !prev;
    });
  };

  const theme = createTheme({
    palette: {
      mode: darkMode ? 'dark' : 'light',
      primary: { main: '#4F46E5' },
      secondary: { main: '#EC4899' },
    },
  });

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <Router>
        <Navbar darkMode={darkMode} toggleDarkMode={toggleDarkMode} />
        <Divider />
        <Box component="main" sx={{ flexGrow: 1, p: 3, mt: 8 }}>
          <Container maxWidth="lg">
            <Routes>
              <Route path="/login" element={<Login />} />
              <Route path="/" element={<PrivateRoute><Home /></PrivateRoute>} />
              <Route path="/customers" element={<PrivateRoute><CustomerList /></PrivateRoute>} />
              <Route path="/customers/new" element={<PrivateRoute><CustomerForm /></PrivateRoute>} />
              <Route path="/customers/:id/edit" element={<PrivateRoute><CustomerForm /></PrivateRoute>} />
              <Route path="/customers/:id" element={<PrivateRoute><CustomerDetail /></PrivateRoute>} />
              <Route path="/readings" element={<PrivateRoute><ReadingList /></PrivateRoute>} />
              <Route path="/readings/new" element={<PrivateRoute><ReadingForm /></PrivateRoute>} />
              <Route path="/readings/:id/edit" element={<PrivateRoute><ReadingForm /></PrivateRoute>} />
              <Route path="/readings/:id" element={<PrivateRoute><ReadingDetail /></PrivateRoute>} />
              <Route path="/import-export" element={<PrivateRoute><ImportExport /></PrivateRoute>} />
              <Route path="/analysis" element={<PrivateRoute><Analysis /></PrivateRoute>} />
              <Route path="*" element={<Typography variant="h4">404 - Seite nicht gefunden</Typography>} />
            </Routes>
          </Container>
        </Box>
      </Router>
    </ThemeProvider>
  );
}

export default App;
