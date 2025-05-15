import React, { useEffect, useState } from 'react';
import { getCustomerById } from '../../services/customerService';
import { useParams, Link } from 'react-router-dom';
import { Paper, Typography, Button, Container } from '@mui/material';

function CustomerDetail() {
  const { id } = useParams();
  const [customer, setCustomer] = useState(null);

  useEffect(() => {
    fetchCustomer(id);
  }, [id]);

  const fetchCustomer = async (id) => {
    try {
      const response = await getCustomerById(id);
      setCustomer(response.data);
    } catch (error) {
      console.error('Fehler beim Abrufen des Kunden:', error);
    }
  };

  if (!customer) {
    return <Typography>Loading...</Typography>;
  }

  return (
    <Container maxWidth="sm" sx={{ marginTop: 4 }}>
      <Paper elevation={3} sx={{ padding: 4 }}>
        <Typography variant="h4" gutterBottom>{customer.firstName} {customer.lastName}</Typography>
        <Typography variant="body1"><strong>ID:</strong> {customer.id}</Typography>
        <Typography variant="body1"><strong>Geburtsdatum:</strong> {customer.birthdate}</Typography>
        <Typography variant="body1"><strong>Geschlecht:</strong> {customer.gender}</Typography>
        <Button variant="contained" color="primary" component={Link} to={`/customers/${id}/edit`} sx={{ marginTop: 2 }}>
          Bearbeiten
        </Button>
      </Paper>
    </Container>
  );
}

export default CustomerDetail;
