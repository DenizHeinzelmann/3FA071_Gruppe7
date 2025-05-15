// src/components/ImportExport/ImportExport.js
import React, { useState } from 'react';
import {
  Button, Paper, Grid, Select, MenuItem, InputLabel, FormControl
} from '@mui/material';
import Papa from 'papaparse';
import xmljs from 'xml-js';
import { saveAs } from 'file-saver';
import api from '../../services/api';

function ImportExport() {
  const [dataType, setDataType] = useState('customers');
  const [fileFormat, setFileFormat] = useState('json');

  const handleDataTypeChange = (e) => setDataType(e.target.value);
  const handleFileFormatChange = (e) => setFileFormat(e.target.value);

  const handleExport = async () => {
    try {
      const endpoint = dataType === 'customers' ? '/customers' : '/readings';
      const response = await api.get(endpoint);
      let data = response.data;
      let content, mimeType, extension;

      switch (fileFormat) {
        case 'json':
          content = JSON.stringify(data, null, 2);
          mimeType = 'application/json';
          extension = 'json';
          break;
        case 'xml':
          content = xmljs.js2xml({ [dataType]: data }, { compact: true, spaces: 4 });
          mimeType = 'application/xml';
          extension = 'xml';
          break;
        case 'csv':
          content = Papa.unparse(
            dataType === 'customers'
              ? data.map(customer => ({
                  id: customer.id,
                  firstName: customer.firstName,
                  lastName: customer.lastName,
                  birthdate: customer.birthdate,
                  gender: customer.gender,
                }))
              : data.map(reading => ({
                  id: reading.id,
                  substitute: reading.substitute,
                  meterId: reading.meterId,
                  meterCount: reading.meterCount,
                  kindOfMeter: reading.kindOfMeter,
                  dateOfReading: reading.dateOfReading,
                  customerId: reading.customer?.id,
                  comment: reading.comment,
                }))
          );
          mimeType = 'text/csv';
          extension = 'csv';
          break;
        default:
          return;
      }

      const blob = new Blob([content], { type: mimeType });
      saveAs(blob, `${dataType}.${extension}`);
    } catch (err) {
      console.error('Fehler beim Export:', err);
      alert('Export fehlgeschlagen.');
    }
  };

  const convertDate = (raw) => {
    if (!raw) return '';
    const parts = raw.trim().split(/[.,\/-]/);
    if (parts.length === 3) {
      const [day, month, year] = parts;
      if (year?.length === 4) return `${year}-${month.padStart(2, '0')}-${day.padStart(2, '0')}`;
      return `20${year}-${month.padStart(2, '0')}-${day.padStart(2, '0')}`;
    }
    return '';
  };

  const handleImport = (e) => {
    const file = e.target.files[0];
    if (!file) return;

    const reader = new FileReader();

    reader.onload = async (event) => {
      try {
        let data;
        const raw = event.target.result;

        if (fileFormat === 'json') {
          data = JSON.parse(raw);
        } else if (fileFormat === 'csv') {
          const parsed = Papa.parse(raw, { header: true, skipEmptyLines: true });
          data = parsed.data;

          // Spezialfall: nicht-standardisierte Reading CSV
          if (dataType === 'readings' && data[0]?.Kunde && data[1]?.Zählernummer) {
            const customerId = data[0]['Kunde'].replace(/"/g, '').trim();
            const meterId = data[1]['Zählernummer'].replace(/"/g, '').trim();

            // Finde Zeile mit "Datum" und ab da verarbeiten
            const datumIndex = parsed.data.findIndex(row => row['Datum']);
            const dataRows = parsed.data.slice(datumIndex);

            data = dataRows.map(row => ({
              meterId,
              meterCount: parseFloat(row['Zählerstand in m³']?.replace(',', '.') || ''),
              kindOfMeter: 'WASSER', // kann später dynamisch gemacht werden
              dateOfReading: convertDate(row['Datum']),
              customer: { id: customerId },
              comment: row['Kommentar'] || ''
            })).filter(r => r.dateOfReading && !isNaN(r.meterCount));
          }
        } else if (fileFormat === 'xml') {
          alert('XML-Import nicht unterstützt');
          return;
        }

        const endpoint = dataType === 'customers' ? '/customers' : '/readings';

        for (const item of data) {
          if (dataType === 'customers') {
            const { firstName, lastName, birthdate, gender } = item;
            if (!firstName || !lastName) continue;
            await api.post(endpoint, { firstName, lastName, birthdate, gender });
          } else {
            if (!item.meterId || !item.dateOfReading || !item.meterCount || !item.kindOfMeter || !item.customer?.id) continue;
            await api.post(endpoint, {
              substitute: item.substitute === 'true' || item.substitute === true,
              meterId: item.meterId,
              meterCount: item.meterCount,
              kindOfMeter: item.kindOfMeter,
              dateOfReading: item.dateOfReading,
              customer: { id: item.customer.id },
              comment: item.comment || ''
            });
          }
        }

        alert('Import erfolgreich!');
        window.location.reload();
      } catch (err) {
        console.error('Fehler beim Importieren der Daten:', err);
        alert('Fehler beim Importieren der Daten.');
      }
    };

    reader.readAsText(file);
  };

  return (
    <Paper style={{ padding: 20 }}>
      <h2>Import / Export</h2>
      <Grid container spacing={3}>
        <Grid item xs={12} sm={6}>
          <FormControl fullWidth>
            <InputLabel>Daten-Typ</InputLabel>
            <Select value={dataType} label="Daten-Typ" onChange={handleDataTypeChange}>
              <MenuItem value="customers">Kunden</MenuItem>
              <MenuItem value="readings">Ablesungen</MenuItem>
            </Select>
          </FormControl>
        </Grid>
        <Grid item xs={12} sm={6}>
          <FormControl fullWidth>
            <InputLabel>Dateiformat</InputLabel>
            <Select value={fileFormat} label="Dateiformat" onChange={handleFileFormatChange}>
              <MenuItem value="json">JSON</MenuItem>
              <MenuItem value="xml">XML</MenuItem>
              <MenuItem value="csv">CSV</MenuItem>
            </Select>
          </FormControl>
        </Grid>
        <Grid item xs={12} sm={6}>
          <Button variant="contained" color="primary" onClick={handleExport} fullWidth>
            Exportieren
          </Button>
        </Grid>
        <Grid item xs={12} sm={6}>
          <Button variant="contained" component="label" color="secondary" fullWidth>
            Importieren
            <input
              type="file"
              hidden
              onChange={handleImport}
              accept={
                fileFormat === 'json'
                  ? '.json'
                  : fileFormat === 'xml'
                  ? '.xml'
                  : '.csv'
              }
            />
          </Button>
        </Grid>
      </Grid>
    </Paper>
  );
}

export default ImportExport;
