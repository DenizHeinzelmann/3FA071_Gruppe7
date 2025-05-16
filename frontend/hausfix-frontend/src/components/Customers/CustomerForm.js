import React, { useState, useEffect } from 'react';
import { createCustomer, getCustomerById, updateCustomer } from '../../services/customerService';
import { useNavigate, useParams } from 'react-router-dom';
import { TextField, Button, MenuItem, Paper, Grid, Container, Typography, Snackbar, Alert } from '@mui/material';

const genders = [
  { value: 'M', label: 'Männlich' },
  { value: 'W', label: 'Weiblich' },
  { value: 'D', label: 'Divers' },
];

function CustomerForm() {
  const [customer, setCustomer] = useState({
    firstName: '',
    lastName: '',
    birthDate: '',
    gender: '',
  });
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(false);
  const navigate = useNavigate();
  const { id } = useParams();
  const isEditMode = Boolean(id);

  useEffect(() => {
    if (isEditMode) {
      fetchCustomer(id);
    }
  }, [id, isEditMode]);

  const fetchCustomer = async (id) => {
    try {
      const response = await getCustomerById(id);
      setCustomer({
        firstName: response.data.firstName,
        lastName: response.data.lastName,
        birthDate: response.data.birthdate,
        gender: response.data.gender,
      });
    } catch (error) {
      console.error('Fehler beim Abrufen des Kunden:', error);
      setError('Fehler beim Abrufen des Kunden.');
    }
  };

  const handleChange = (e) => {
    setCustomer({ ...customer, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const customerData = {
      firstName: customer.firstName,
      lastName: customer.lastName,
      birthDate: customer.birthDate, // Sende nur 'birthDate'
      gender: customer.gender,
    };
    try {
      if (isEditMode) {
        await updateCustomer(id, customerData);
      } else {
        await createCustomer(customerData);
      }
      setSuccess(true);
      navigate('/customers');
    } catch (error) {
      console.error('Fehler beim Speichern des Kunden:', error);
      setError('Fehler beim Speichern des Kunden.');
    }
  };

  return (
    <Container maxWidth="sm" sx={{ marginTop: 4 }}>
      <Paper elevation={3} sx={{ padding: 4 }}>
        <Typography variant="h5" gutterBottom>
          {isEditMode ? 'Kunden bearbeiten' : 'Neuen Kunden erstellen'}
        </Typography>
        <form onSubmit={handleSubmit}>
          <Grid container spacing={3}>
            <Grid item xs={12} sm={6}>
              <TextField
                label="Vorname"
                name="firstName"
                value={customer.firstName}
                onChange={handleChange}
                required
                fullWidth
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                label="Nachname"
                name="lastName"
                value={customer.lastName}
                onChange={handleChange}
                required
                fullWidth
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                label="Geburtsdatum"
                name="birthDate"
                type="date"
                value={customer.birthDate}
                onChange={handleChange}
                required
                fullWidth
                InputLabelProps={{
                  shrink: true,
                }}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                select
                label="Geschlecht"
                name="gender"
                value={customer.gender}
                onChange={handleChange}
                required
                fullWidth
              >
                {genders.map((option) => (
                  <MenuItem key={option.value} value={option.value}>
                    {option.label}
                  </MenuItem>
                ))}
              </TextField>
            </Grid>
            <Grid item xs={12}>
              <Button variant="contained" color="primary" type="submit">
                {isEditMode ? 'Aktualisieren' : 'Erstellen'}
              </Button>
              <Button
                variant="outlined"
                color="secondary"
                onClick={() => navigate('/customers')}
                sx={{ marginLeft: 2 }}
              >
                Abbrechen
              </Button>
            </Grid>
          </Grid>
        </form>
      </Paper>

      <Snackbar open={Boolean(error)} autoHideDuration={6000} onClose={() => setError(null)}>
        <Alert onClose={() => setError(null)} severity="error" sx={{ width: '100%' }}>
          {error}
        </Alert>
      </Snackbar>

      <Snackbar open={success} autoHideDuration={6000} onClose={() => setSuccess(false)}>
        <Alert onClose={() => setSuccess(false)} severity="success" sx={{ width: '100%' }}>
          Kunde erfolgreich {isEditMode ? 'aktualisiert' : 'erstellt'}!
        </Alert>
      </Snackbar>
    </Container>
  );
}

export default CustomerForm;
