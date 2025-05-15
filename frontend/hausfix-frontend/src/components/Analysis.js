// src/components/Analysis.js
import React, { useState, useEffect } from 'react';
import { Container, Paper, Typography, FormControl, InputLabel, Select, MenuItem, CircularProgress, Box } from '@mui/material';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import api from '../services/api';

const Analysis = () => {
  const [period, setPeriod] = useState(1); // Default 1 Jahr
  const [analysisData, setAnalysisData] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchAnalysisData();
  }, [period]);

  const fetchAnalysisData = async () => {
    setLoading(true);
    try {
      const response = await api.get(`/readings/analysis?period=${period}`);
      setAnalysisData(response.data);
    } catch (error) {
      console.error("Fehler beim Abrufen der Analyse-Daten:", error);
    } finally {
      setLoading(false);
    }
  };

  const handlePeriodChange = (e) => {
    setPeriod(Number(e.target.value));
  };

  const types = ["HEIZUNG", "WASSER", "STROM"];
  const charts = types.map((type) => {
    const dataForType = analysisData.filter(item => item.meterType === type);
    return (
      <Paper key={type} sx={{ padding: 2, marginBottom: 4 }}>
        <Typography variant="h6" gutterBottom>{type} (Durchschnitt)</Typography>
        {dataForType.length === 0 ? (
          <Typography variant="body2">Keine Daten vorhanden.</Typography>
        ) : (
          <ResponsiveContainer width="100%" height={300}>
            <LineChart data={dataForType}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="periodLabel" />
              <YAxis />
              <Tooltip />
              <Line type="monotone" dataKey="avgValue" stroke="#8884d8" activeDot={{ r: 8 }} />
            </LineChart>
          </ResponsiveContainer>
        )}
      </Paper>
    );
  });

  return (
    <Container maxWidth="md" sx={{ marginTop: 4 }}>
      <Paper sx={{ padding: 3, marginBottom: 4 }}>
        <Typography variant="h5" gutterBottom>Messdaten-Auswertung</Typography>
        <FormControl fullWidth>
          <InputLabel id="period-label">Zeitraum</InputLabel>
          <Select
            labelId="period-label"
            value={period}
            label="Zeitraum"
            onChange={handlePeriodChange}
          >
            <MenuItem value={1}>1 Jahr (monatliche Durchschnittswerte)</MenuItem>
            <MenuItem value={5}>5 Jahre (jährliche Durchschnittswerte)</MenuItem>
            <MenuItem value={10}>10 Jahre (jährliche Durchschnittswerte)</MenuItem>
          </Select>
        </FormControl>
      </Paper>
      {loading ? (
        <Box sx={{ textAlign: 'center' }}>
          <CircularProgress />
        </Box>
      ) : (
        charts
      )}
    </Container>
  );
};

export default Analysis;
