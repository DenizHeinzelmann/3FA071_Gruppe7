import { createTheme } from '@mui/material/styles';

const theme = createTheme({
  palette: {
    primary:   { main: '#4F46E5' },   // Indigo 600
    secondary: { main: '#EC4899' },   // Pink 500
    background:{ default: '#F3F4F6' }, // Grau-50
    text:      { primary: '#111827' }, // Grau-900
  },
  shape: {
    borderRadius: 12,  // Globale Eck-Abrundung
  },
  typography: {
    button: {
      textTransform: 'none',
      fontWeight: 600,
    },
  },
  components: {
    MuiPaper: {
      styleOverrides: {
        root: {
          boxShadow: '0px 4px 12px rgba(0,0,0,0.08)',
          borderRadius: '16px',
          backgroundColor: '#FFFFFF',
        },
      },
    },
    MuiButton: {
      styleOverrides: {
        containedPrimary: {
          boxShadow: '0px 3px 8px rgba(0,0,0,0.12)',
          transition: 'all 200ms ease-in-out',
          '&:hover': {
            boxShadow: '0px 5px 15px rgba(0,0,0,0.16)',
            transform: 'translateY(-2px)',
          },
        },
      },
    },
    MuiAppBar: {
      styleOverrides: {
        root: {
          backdropFilter: 'blur(8px)',
          backgroundColor: 'rgba(255,255,255,0.72)',
          color: '#111827',
          boxShadow: 'none',
        },
      },
    },
  },
});

export default theme;
