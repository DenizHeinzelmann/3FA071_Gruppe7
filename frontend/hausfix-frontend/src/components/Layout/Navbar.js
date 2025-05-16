// src/components/Layout/Navbar.js
import React from 'react';
import { AppBar, Toolbar, Typography, Button, Box, Switch } from '@mui/material';
import { Link } from 'react-router-dom';

function Navbar({ darkMode, toggleDarkMode }) {
  return (
    <Box sx={{ flexGrow: 1 }}>
      <AppBar
        position="fixed"
        elevation={3}
        sx={{
          backgroundColor: darkMode ? 'rgba(18,18,18,0.95)' : 'rgba(255, 255, 255, 0.9)',
          backdropFilter: 'blur(10px)',
          color: darkMode ? '#f1f1f1' : '#1a1a1a',
          borderBottom: '1px solid rgba(0, 0, 0, 0.1)',
        }}
      >
        <Toolbar>
          <Typography variant="h6" sx={{ flexGrow: 1, fontWeight: 600 }}>
            Digitale Hausverwaltung
          </Typography>
          {[
            { label: 'Home', path: '/' },
            { label: 'Kunden', path: '/customers' },
            { label: 'Ablesungen', path: '/readings' },
            { label: 'Import/Export', path: '/import-export' },
            { label: 'Analyse', path: '/analysis' },
          ].map(({ label, path }) => (
            <Button
              key={label}
              color="inherit"
              component={Link}
              to={path}
              sx={{
                mx: 0.5,
                textTransform: 'none',
                fontWeight: 500,
                '&:hover': { backgroundColor: 'rgba(0,0,0,0.04)' },
              }}
            >
              {label}
            </Button>
          ))}
          <Box sx={{ ml: 2, display: 'flex', alignItems: 'center' }}>
            <Typography variant="body2" sx={{ mr: 1 }}></Typography>
            <Switch checked={darkMode} onChange={toggleDarkMode} />
          </Box>
        </Toolbar>
      </AppBar>
    </Box>
  );
}

export default Navbar;
