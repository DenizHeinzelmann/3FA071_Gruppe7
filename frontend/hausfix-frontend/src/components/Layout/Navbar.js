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
      <AppBar position="fixed">
        <Toolbar>
          <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
            Digitale Hausverwaltung
          </Typography>
          <Button color="inherit" component={Link} to="/customers">Kunden</Button>
          <Button color="inherit" component={Link} to="/readings">Ablesungen</Button>
          <Button color="inherit" component={Link} to="/import-export">Import/Export</Button>
        </Toolbar>
      </AppBar>
    </Box>
  );
}

export default Navbar;
