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
  Container,
  TextField
} from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete';
import EditIcon from '@mui/icons-material/Edit';

function ReadingList() {
  const [readings, setReadings] = useState([]);
  const [filteredReadings, setFilteredReadings] = useState([]);
  const [filter, setFilter] = useState('');
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchReadings();
  }, []);

  const fetchReadings = async () => {
    try {
      const response = await getAllReadings();
      setReadings(response.data);
      setFilteredReadings(response.data);
      setLoading(false);
    } catch (err) {
      console.error('Fehler beim Abrufen der Ablesungen:', err);
      setError('Fehler beim Abrufen der Ablesungen.');
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Sind Sie sicher, dass Sie diese Ablesung löschen möchten?')) {
      try {
        await deleteReading(id);
        fetchReadings();
      } catch (err) {
        console.error('Fehler beim Löschen der Ablesung:', err);
        setError('Fehler beim Löschen der Ablesung.');
      }
    }
  };

  const handleFilter = (e) => {
    const val = e.target.value.toLowerCase();
    setFilter(val);
    setFilteredReadings(
      readings.filter(r =>
        r.id.toLowerCase().includes(val) ||
        r.meterId.toLowerCase().includes(val) ||
        r.kindOfMeter.toLowerCase().includes(val) ||
        (r.customer && (
          r.customer.firstName.toLowerCase().includes(val) ||
          r.customer.lastName.toLowerCase().includes(val)
        ))
      )
    );
  };

  if (loading) {
    return <Typography>Lade Daten...</Typography>;
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
      <TextField
        label="Filtern nach ID, Zähler-ID, Kunde, Typ"
        variant="outlined"
        fullWidth
        sx={{ marginBottom: 2 }}
        value={filter}
        onChange={handleFilter}
      />
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
            {filteredReadings.map((reading) => (
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
            {filteredReadings.length === 0 && (
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
