import React, { useEffect, useState } from 'react';
import { getReadingById } from '../../services/readingService';
import { useParams, Link } from 'react-router-dom';
import { Paper, Typography, Button, Container } from '@mui/material';

function ReadingDetail() {
  const { id } = useParams();
  const [reading, setReading] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchReading(id);
  }, [id]);

  const fetchReading = async (id) => {
    console.log(`Fetching reading with ID: ${id}`); // Debugging
    try {
      const response = await getReadingById(id);
      console.log('Reading fetched successfully:', response.data); // Debugging
      setReading(response.data);
      setLoading(false);
    } catch (error) {
      console.error('Fehler beim Abrufen der Ablesung:', error);
      setError('Fehler beim Abrufen der Ablesung.');
      setLoading(false);
    }
  };

  if (loading) {
    return <Typography>Loading...</Typography>;
  }

  if (error) {
    return <Typography color="error">{error}</Typography>;
  }

  if (!reading) {
    return <Typography>Keine Ablesung gefunden.</Typography>;
  }

  return (
    <Container maxWidth="sm" sx={{ marginTop: 4 }}>
      <Paper elevation={3} sx={{ padding: 4 }}>
        <Typography variant="h4" gutterBottom>Ablesung Details</Typography>
        <Typography variant="body1"><strong>ID:</strong> {reading.id}</Typography>
        <Typography variant="body1"><strong>Meter-ID:</strong> {reading.meterId}</Typography>
        <Typography variant="body1"><strong>Meter-Stand:</strong> {reading.meterCount}</Typography>
        <Typography variant="body1"><strong>Art des Messgeräts:</strong> {reading.kindOfMeter}</Typography>
        <Typography variant="body1"><strong>Datum der Ablesung:</strong> {reading.dateOfReading}</Typography>
        <Typography variant="body1"><strong>Kunde:</strong> {reading.customer ? `${reading.customer.firstName} ${reading.customer.lastName}` : 'Kein Kunde'}</Typography>
        <Typography variant="body1"><strong>Kommentar:</strong> {reading.comment}</Typography>
        <Button variant="contained" color="primary" component={Link} to={`/readings/${id}/edit`} sx={{ marginTop: 2 }}>
          Bearbeiten
        </Button>
      </Paper>
    </Container>
  );
}

export default ReadingDetail;
