import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { CssBaseline, Box, Container, Typography } from '@mui/material';
import Navbar from './components/Layout/Navbar';
import CustomerList from './components/Customers/CustomerList';
import CustomerForm from './components/Customers/CustomerForm';
import CustomerDetail from './components/Customers/CustomerDetail';
import ReadingList from './components/Readings/ReadingList';
import ReadingForm from './components/Readings/ReadingForm';
import ReadingDetail from './components/Readings/ReadingDetail';
import ImportExport from './components/ImportExport/ImportExport';

function App() {
  return (
    <Router>
      <CssBaseline />
      <Navbar />
      <Box component="main" sx={{ flexGrow: 1, p: 3, mt: 8 }}>
        <Container maxWidth="lg">
          <Routes>
            <Route path="/" element={<Navigate to="/customers" replace />} />

            <Route path="/customers" element={<CustomerList />} />
            <Route path="/customers/new" element={<CustomerForm />} />
            <Route path="/customers/:id/edit" element={<CustomerForm />} />
            <Route path="/customers/:id" element={<CustomerDetail />} />

            <Route path="/readings" element={<ReadingList />} />
            <Route path="/readings/new" element={<ReadingForm />} />
            <Route path="/readings/:id/edit" element={<ReadingForm />} />
            <Route path="/readings/:id" element={<ReadingDetail />} />

            <Route path="/import-export" element={<ImportExport />} />

            {/* Wenn Pfad net definiert --> */}
            <Route path="*" element={<Typography variant="h4">404 - Seite nicht gefunden</Typography>} />
          </Routes>
        </Container>
      </Box>
    </Router>
  );
}

export default App;
