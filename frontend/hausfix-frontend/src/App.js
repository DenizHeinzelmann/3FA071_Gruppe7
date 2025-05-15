import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { CssBaseline, Box, Container, Typography } from '@mui/material';
import Navbar from './components/Layout/Navbar';
import Home from './components/Home';
import CustomerList from './components/Customers/CustomerList';
import CustomerForm from './components/Customers/CustomerForm';
import CustomerDetail from './components/Customers/CustomerDetail';
import ReadingList from './components/Readings/ReadingList';
import ReadingForm from './components/Readings/ReadingForm';
import ReadingDetail from './components/Readings/ReadingDetail';
import ImportExport from './components/ImportExport/ImportExport';
import Login from './components/Auth/Login';
import PrivateRoute from './components/Auth/PrivateRoute';
import Analysis from './components/Analysis';
import { Divider } from '@mui/material';

function App() {
  return (
    <Router>
      <CssBaseline />
      <Navbar />
        <Divider />
      <Box component="main" sx={{ flexGrow: 1, p: 3, mt: 8 }}>
        <Container maxWidth="lg">
          <Routes>
            <Route path="/login" element={<Login />} />

            {/* 🟢 Geschützte Home-Seite */}
            <Route path="/" element={
              <PrivateRoute>
                <Home />
              </PrivateRoute>
            } />

            {/* 🟢 Alle anderen geschützten Routen */}
            <Route path="/customers" element={
              <PrivateRoute>
                <CustomerList />
              </PrivateRoute>
            } />
            <Route path="/customers/new" element={
              <PrivateRoute>
                <CustomerForm />
              </PrivateRoute>
            } />
            <Route path="/customers/:id/edit" element={
              <PrivateRoute>
                <CustomerForm />
              </PrivateRoute>
            } />
            <Route path="/customers/:id" element={
              <PrivateRoute>
                <CustomerDetail />
              </PrivateRoute>
            } />

            <Route path="/readings" element={
              <PrivateRoute>
                <ReadingList />
              </PrivateRoute>
            } />
            <Route path="/readings/new" element={
              <PrivateRoute>
                <ReadingForm />
              </PrivateRoute>
            } />
            <Route path="/readings/:id/edit" element={
              <PrivateRoute>
                <ReadingForm />
              </PrivateRoute>
            } />
            <Route path="/readings/:id" element={
              <PrivateRoute>
                <ReadingDetail />
              </PrivateRoute>
            } />

            <Route path="/import-export" element={
              <PrivateRoute>
                <ImportExport />
              </PrivateRoute>
            } />

            <Route path="/analysis" element={
              <PrivateRoute>
                <Analysis />
              </PrivateRoute>
            } />

            <Route path="*" element={<Typography variant="h4">404 - Seite nicht gefunden</Typography>} />
          </Routes>
        </Container>
      </Box>
    </Router>
  );
}

export default App;
