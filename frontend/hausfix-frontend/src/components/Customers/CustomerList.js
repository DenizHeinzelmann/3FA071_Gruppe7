import React, { useEffect, useState } from 'react';
import { getAllCustomers, deleteCustomer } from '../../services/customerService';
import { Link } from 'react-router-dom';
import {
  Table, TableBody, TableCell, TableContainer, TableHead, TableRow,
  Paper, Button, IconButton, Typography, TextField, Box
} from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete';
import EditIcon from '@mui/icons-material/Edit';

function CustomerList() {
  const [customers, setCustomers] = useState([]);
  const [filtered, setFiltered] = useState([]);
  const [filter, setFilter] = useState('');

  useEffect(() => {
    fetchCustomers();
  }, []);

  const fetchCustomers = async () => {
    try {
      const response = await getAllCustomers();
      setCustomers(response.data);
      setFiltered(response.data);
    } catch (error) {
      console.error('Fehler beim Abrufen der Kunden:', error);
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Sind Sie sicher, dass Sie diesen Kunden löschen möchten?')) {
      try {
        await deleteCustomer(id);
        fetchCustomers();
      } catch (error) {
        console.error('Fehler beim Löschen des Kunden:', error);
      }
    }
  };

  const handleFilter = (e) => {
    const val = e.target.value.toLowerCase();
    setFilter(val);
    setFiltered(
      customers.filter(c =>
        c.id.toLowerCase().includes(val) ||
        c.firstName.toLowerCase().includes(val) ||
        c.lastName.toLowerCase().includes(val)
      )
    );
  };

  return (
    <div>
      <Typography variant="h4" gutterBottom>Kunden</Typography>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', marginBottom: 2 }}>
        <Button variant="contained" color="primary" component={Link} to="/customers/new">
          Neuer Kunde
        </Button>
        <TextField
          label="Filtern nach ID, Vorname, Nachname"
          variant="outlined"
          value={filter}
          onChange={handleFilter}
        />
      </Box>
      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>ID</TableCell>
              <TableCell>Vorname</TableCell>
              <TableCell>Nachname</TableCell>
              <TableCell>Geburtsdatum</TableCell>
              <TableCell>Geschlecht</TableCell>
              <TableCell>Aktionen</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {filtered.map(customer => (
              <TableRow key={customer.id}>
                <TableCell>{customer.id}</TableCell>
                <TableCell>{customer.firstName}</TableCell>
                <TableCell>{customer.lastName}</TableCell>
                <TableCell>{customer.birthdate}</TableCell>
                <TableCell>{customer.gender}</TableCell>
                <TableCell>
                  <IconButton component={Link} to={`/customers/${customer.id}`} color="primary">
                    <EditIcon />
                  </IconButton>
                  <IconButton onClick={() => handleDelete(customer.id)} color="secondary">
                    <DeleteIcon />
                  </IconButton>
                </TableCell>
              </TableRow>
            ))}
            {filtered.length === 0 && (
              <TableRow>
                <TableCell colSpan={6} align="center">Keine Kunden gefunden.</TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </TableContainer>
    </div>
  );
}

export default CustomerList;
