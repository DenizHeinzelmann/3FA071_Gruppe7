import React from 'react';
import AppBar from '@mui/material/AppBar';
import Box from '@mui/material/Box';
import Toolbar from '@mui/material/Toolbar';
import Typography from '@mui/material/Typography';
import Button from '@mui/material/Button';
import { Link } from 'react-router-dom';

function Navbar() {
  return (
    <Box sx={{ flexGrow: 1 }}>
      <AppBar
        position="fixed"
        elevation={3}
        sx={{
          backgroundColor: 'rgba(255, 255, 255, 0.9)',
          backdropFilter: 'blur(10px)',
          color: '#1a1a1a',
          borderBottom: '1px solid rgba(0, 0, 0, 0.1)',
        }}
      >
        <Toolbar>
          <Typography variant="h6" component="div" sx={{ flexGrow: 1, fontWeight: 600 }}>
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
        </Toolbar>
      </AppBar>
    </Box>
  );
}

export default Navbar;
