// src/components/Readings/ReadingList.js
import React, { useEffect, useState } from 'react';
import { getAllReadings, deleteReading } from '../../services/readingService';
import { Link } from 'react-router-dom';
import {
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Button,
  IconButton,
  Typography,
  Container
} from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete';
import EditIcon from '@mui/icons-material/Edit';

function ReadingList() {
  const [readings, setReadings] = useState([]);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchReadings();
  }, []);

  const fetchReadings = async () => {
    console.log('Fetching all readings...');
    try {
      const response = await getAllReadings();
      console.log('Readings fetched successfully:', response.data);
      setReadings(response.data);
      setLoading(false);
    } catch (err) {
      console.error('Fehler beim Abrufen der Ablesungen:', err);
      setError('Fehler beim Abrufen der Ablesungen.');
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    console.log(`Attempting to delete reading with ID: ${id}`);
    if (window.confirm('Sind Sie sicher, dass Sie diese Ablesung löschen möchten?')) {
      try {
        await deleteReading(id);
        console.log(`Reading with ID: ${id} deleted successfully.`);
        fetchReadings();
      } catch (err) {
        console.error('Fehler beim Löschen der Ablesung:', err);
        setError('Fehler beim Löschen der Ablesung.');
      }
    }
  };

  if (loading) {
    return <Typography>Loading...</Typography>;
  }

  return (
    <Container maxWidth="lg" sx={{ marginTop: 4 }}>
      <Typography variant="h4" gutterBottom>
        Ablesungen
      </Typography>
      <Button
        variant="contained"
        color="primary"
        component={Link}
        to="/readings/new"
        sx={{ marginBottom: 2 }}
      >
        Neue Ablesung
      </Button>
      {error && (
        <Typography color="error" sx={{ marginBottom: 2 }}>
          {error}
        </Typography>
      )}
      <TableContainer component={Paper} sx={{ borderRadius: 3, overflow: 'hidden', boxShadow: 2 }}>
        <Table aria-label="Ablesungen Tabelle" sx={{ borderCollapse: 'separate', borderSpacing: '0 8px' }}>
          <TableHead>
            <TableRow>
              <TableCell>ID</TableCell>
              <TableCell>Meter-ID</TableCell>
              <TableCell>Meter-Stand</TableCell>
              <TableCell>Art des Messgeräts</TableCell>
              <TableCell>Datum der Ablesung</TableCell>
              <TableCell>Kunde</TableCell>
              <TableCell>Kommentar</TableCell>
              <TableCell>Aktionen</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {readings.map((reading) => (
              <TableRow key={reading.id}>
                <TableCell>{reading.id}</TableCell>
                <TableCell>{reading.meterId}</TableCell>
                <TableCell>{reading.meterCount}</TableCell>
                <TableCell>{reading.kindOfMeter}</TableCell>
                <TableCell>{reading.dateOfReading}</TableCell>
                <TableCell>
                  {reading.customer
                    ? `${reading.customer.firstName} ${reading.customer.lastName}`
                    : 'Kein Kunde'}
                </TableCell>
                <TableCell>{reading.comment}</TableCell>
                <TableCell>
                  <IconButton component={Link} to={`/readings/${reading.id}`} color="primary">
                    <EditIcon />
                  </IconButton>
                  <IconButton onClick={() => handleDelete(reading.id)} color="secondary">
                    <DeleteIcon />
                  </IconButton>
                </TableCell>
              </TableRow>
            ))}
            {readings.length === 0 && (
              <TableRow>
                <TableCell colSpan={8} align="center">
                  Keine Ablesungen gefunden.
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </TableContainer>
    </Container>
  );
}

export default ReadingList;
