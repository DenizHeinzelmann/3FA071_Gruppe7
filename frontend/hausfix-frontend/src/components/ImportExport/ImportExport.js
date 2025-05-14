// src/components/ImportExport/ImportExport.js
    import React, { useState } from 'react';
    import { Button, Paper, Grid, Select, MenuItem, InputLabel, FormControl } from '@mui/material';
    import Papa from 'papaparse';
    import xmljs from 'xml-js';
    import { saveAs } from 'file-saver';
    import api from '../../services/api';

    function ImportExport() {
      const [dataType, setDataType] = useState('customers'); // 'customers' oder 'readings'
      const [fileFormat, setFileFormat] = useState('json');

      const handleDataTypeChange = (e) => {
        setDataType(e.target.value);
      };

      const handleFileFormatChange = (e) => {
        setFileFormat(e.target.value);
      };

      const handleExport = async () => {
        try {
          const endpoint = dataType === 'customers' ? '/customers' : '/readings';
          const response = await api.get(endpoint);
          let data = response.data;
          let content;
          let mimeType;
          let extension;

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
              if (dataType === 'customers') {
                content = Papa.unparse(data.map(customer => ({
                  id: customer.id,
                  firstName: customer.firstName,
                  lastName: customer.lastName,
                  birthdate: customer.birthdate,
                  gender: customer.gender,
                })));
              } else {
                content = Papa.unparse(data.map(reading => ({
                  id: reading.id,
                  substitute: reading.substitute,
                  meterId: reading.meterId,
                  meterCount: reading.meterCount,
                  kindOfMeter: reading.kindOfMeter,
                  dateOfReading: reading.dateOfReading,
                  customerId: reading.customer.id,
                  comment: reading.comment,
                })));
              }
              mimeType = 'text/csv';
              extension = 'csv';
              break;
            default:
              return;
          }

          const blob = new Blob([content], { type: mimeType });
          saveAs(blob, `${dataType}.${extension}`);
        } catch (error) {
          console.error('Fehler beim Exportieren der Daten:', error);
          alert('Export fehlgeschlagen.');
        }
      };

      const handleImport = (e) => {
        const file = e.target.files[0];
        if (!file) return;

        const reader = new FileReader();

        reader.onload = async (event) => {
          try {
            let data;
            switch (fileFormat) {
              case 'json':
                data = JSON.parse(event.target.result);
                break;
              case 'xml':
                const result = xmljs.xml2js(event.target.result, { compact: true });
                data = dataType === 'customers'
                  ? result.customers.customer
                  : result.readings.reading;
                break;
              case 'csv':
                const parsed = Papa.parse(event.target.result, {
                  header: true,
                  delimiter: '\t',
                  skipEmptyLines: true
                });
                data = parsed.data;
                break;
              default:
                return;
            }

            const endpoint = dataType === 'customers' ? '/customers' : '/readings';
            if (fileFormat === 'xml') {
              console.warn('XML-Import wird derzeit nicht unterstützt.');
              return;
            }

            for (const item of data) {
              // Pflichtfelder prüfen und Zeilen ohne diese überspringen:
              if (dataType === 'readings') {
                if (!item.meterId || !item.meterCount || !item.kindOfMeter || !item.customerId) {
                  console.warn('Überspringe unvollständige Zeile:', item);
                  continue;
                }
                await api.post(endpoint, {
                  substitute: item.substitute === 'true' || item.substitute === true,
                  meterId: item.meterId,
                  meterCount: parseFloat(item.meterCount),
                  kindOfMeter: item.kindOfMeter,
                  dateOfReading: item.dateOfReading,
                  customer: { id: item.customerId },
                  comment: item.comment || ''
                });
              } else {
                // customers
                if (!item.firstName || !item.lastName) {
                  console.warn('Überspringe unvollständige Kundendaten:', item);
                  continue;
                }
                await api.post(endpoint, {
                  firstName: item.firstName,
                  lastName: item.lastName,
                  birthdate: item.birthdate,
                  gender: item.gender
                });
              }
            }

            alert('Import erfolgreich!');
            window.location.reload();
          } catch (error) {
            console.error('Fehler beim Importieren der Daten:', error);
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