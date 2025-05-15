// src/components/Home.js
import React from 'react';
import { Container, Typography, Paper, Grid, Button } from '@mui/material';
import { Link } from 'react-router-dom';

export default function Home() {
  return (
    <Container maxWidth="md" sx={{ mt: 8, mb: 4 }}>
      <Paper sx={{ p: 4, textAlign: 'center' }}>
        <Typography variant="h3" gutterBottom>Willkommen zur Digitalen Hausverwaltung</Typography>
        <Typography variant="body1" sx={{ mb: 3 }}>
          Wähle im Menü Links einen Bereich aus, um loszulegen.
        </Typography>
        <Grid container spacing={2} justifyContent="center">
          <Grid item>
            <Button component={Link} to="/customers" variant="contained" color="primary">
              Kunden verwalten
            </Button>
          </Grid>
          <Grid item>
            <Button component={Link} to="/readings" variant="contained" color="primary">
              Ablesungen verwalten
            </Button>
          </Grid>
          <Grid item>
            <Button component={Link} to="/import-export" variant="contained" color="secondary">
              Import/Export
            </Button>
          </Grid>
          <Grid item>
            <Button component={Link} to="/analysis" variant="contained" color="secondary">
              Analyse
            </Button>
          </Grid>
        </Grid>
      </Paper>
    </Container>
  );
}
