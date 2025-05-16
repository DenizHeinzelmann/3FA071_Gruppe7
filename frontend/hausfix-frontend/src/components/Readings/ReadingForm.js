import React, { useState, useEffect } from 'react';
import { createReading, getReadingById, updateReading } from '../../services/readingService';
import { getAllCustomers } from '../../services/customerService';
import { useNavigate, useParams } from 'react-router-dom';
import { TextField, Button, MenuItem, Paper, Grid, Select, InputLabel, FormControl, FormControlLabel, Checkbox, Snackbar, Alert } from '@mui/material';

const kindsOfMeter = [
  { value: 'HEIZUNG', label: 'Heizung' },
  { value: 'WASSER', label: 'Wasser' },
  { value: 'STROM', label: 'Strom' },
];

function ReadingForm() {
  const [reading, setReading] = useState({
    substitute: false,
    meterId: '',
    meterCount: '',
    kindOfMeter: '',
    dateOfReading: '',
    customerId: '',
    comment: '',
  });
  const [customers, setCustomers] = useState([]);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(false);
  const navigate = useNavigate();
  const { id } = useParams();
  const isEditMode = Boolean(id);

  useEffect(() => {
    fetchCustomers();
    if (isEditMode) {
      fetchReading(id);
    }
  }, [id, isEditMode]);

  const fetchCustomers = async () => {
    console.log('Fetching all customers...');
    try {
      const response = await getAllCustomers();
      console.log('Customers fetched successfully:', response.data); // Debugging
      setCustomers(response.data);
    } catch (error) {
      console.error('Fehler beim Abrufen der Kunden:', error);
      setError('Fehler beim Abrufen der Kunden.');
    }
  };

  const fetchReading = async (id) => {
    console.log(`Fetching reading with ID: ${id}`);
    try {
      const response = await getReadingById(id);
      console.log('Reading fetched successfully:', response.data); // Debugging
      setReading({
        substitute: response.data.substitute,
        meterId: response.data.meterId,
        meterCount: response.data.meterCount,
        kindOfMeter: response.data.kindOfMeter,
        dateOfReading: response.data.dateOfReading,
        customerId: response.data.customer ? response.data.customer.id : '',
        comment: response.data.comment,
      });
    } catch (error) {
      console.error('Fehler beim Abrufen der Ablesung:', error);
      setError('Fehler beim Abrufen der Ablesung.');
    }
  };

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    console.log(`Field changed: ${name} = ${type === 'checkbox' ? checked : value}`); // Debugging
    setReading({ ...reading, [name]: type === 'checkbox' ? checked : value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    console.log('Submitting reading:', reading); // Debugging
    try {
      const payload = {
        substitute: reading.substitute,
        meterId: reading.meterId,
        meterCount: parseFloat(reading.meterCount),
        kindOfMeter: reading.kindOfMeter,
        dateOfReading: reading.dateOfReading,
        customer: reading.customerId ? { id: reading.customerId } : null,
        comment: reading.comment,
      };
      console.log('Payload:', payload); // Zum Debuggen
      if (isEditMode) {
        console.log(`Updating reading with ID: ${id}`);
        await updateReading(id, payload);
      } else {
        console.log('Creating new reading...');
        await createReading(payload);
      }
      setSuccess(true);
      navigate('/readings');
    } catch (error) {
      console.error('Fehler beim Speichern der Ablesung:', error);
      setError('Fehler beim Speichern der Ablesung.');
    }
  };

  return (
    <Paper style={{ padding: '20px' }}>
      <h2>{isEditMode ? 'Ablesung bearbeiten' : 'Neue Ablesung erstellen'}</h2>
      <form onSubmit={handleSubmit}>
        <Grid container spacing={2}>
          {/* Art des Messgeräts */}
          <Grid item xs={12} sm={6}>
            <FormControl fullWidth required>
              <InputLabel>Art des Messgeräts</InputLabel>
              <Select
                label="Art des Messgeräts"
                name="kindOfMeter"
                value={reading.kindOfMeter}
                onChange={handleChange}
              >
                {kindsOfMeter.map((option) => (
                  <MenuItem key={option.value} value={option.value}>
                    {option.label}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          </Grid>

          {/* Meter-ID */}
          <Grid item xs={12} sm={6}>
            <TextField
              label="Meter-ID"
              name="meterId"
              value={reading.meterId}
              onChange={handleChange}
              required
              fullWidth
            />
          </Grid>

          {/* Meter-Stand */}
          <Grid item xs={12} sm={6}>
            <TextField
              label="Meter-Stand"
              name="meterCount"
              type="number"
              value={reading.meterCount}
              onChange={handleChange}
              required
              fullWidth
              inputProps={{ step: "0.01" }}
            />
          </Grid>

          {/* Substitut */}
          <Grid item xs={12} sm={6}>
            <FormControlLabel
              control={
                <Checkbox
                  checked={reading.substitute}
                  onChange={handleChange}
                  name="substitute"
                  color="primary"
                />
              }
              label="Substitut"
            />
          </Grid>

          {/* Datum der Ablesung */}
          <Grid item xs={12} sm={6}>
            <TextField
              label="Datum der Ablesung"
              name="dateOfReading"
              type="date"
              value={reading.dateOfReading}
              onChange={handleChange}
              required
              fullWidth
              InputLabelProps={{
                shrink: true,
              }}
            />
          </Grid>

          {/* Kunde (optional) */}
          <Grid item xs={12} sm={6}>
            <FormControl fullWidth>
              <InputLabel>Kunde</InputLabel>
              <Select
                label="Kunde"
                name="customerId"
                value={reading.customerId}
                onChange={handleChange}
              >
                {/* Leere Option hinzufügen */}
                <MenuItem value="">
                  <em>Kein Kunde</em>
                </MenuItem>
                {customers.map((customer) => (
                  <MenuItem key={customer.id} value={customer.id}>
                    {`${customer.firstName} ${customer.lastName}`}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          </Grid>

          {/* Kommentar */}
          <Grid item xs={12}>
            <TextField
              label="Kommentar"
              name="comment"
              value={reading.comment}
              onChange={handleChange}
              multiline
              rows={4}
              fullWidth
            />
          </Grid>

          {/* Buttons */}
          <Grid item xs={12}>
            <Button variant="contained" color="primary" type="submit">
              {isEditMode ? 'Aktualisieren' : 'Erstellen'}
            </Button>
            <Button
              variant="outlined"
              color="secondary"
              onClick={() => navigate('/readings')}
              style={{ marginLeft: '10px' }}
            >
              Abbrechen
            </Button>
          </Grid>
        </Grid>
      </form>

      <Snackbar open={Boolean(error)} autoHideDuration={6000} onClose={() => setError(null)}>
        <Alert onClose={() => setError(null)} severity="error" sx={{ width: '100%' }}>
          {error}
        </Alert>
      </Snackbar>

      <Snackbar open={success} autoHideDuration={6000} onClose={() => setSuccess(false)}>
        <Alert onClose={() => setSuccess(false)} severity="success" sx={{ width: '100%' }}>
          Reading erfolgreich {isEditMode ? 'aktualisiert' : 'erstellt'}!
        </Alert>
      </Snackbar>
    </Paper>
  );
}

export default ReadingForm;
