import { createTheme } from '@mui/material/styles';

const theme = createTheme({
  palette: {
    primary:   { main: '#4F46E5' },
    secondary: { main: '#EC4899' },
    background:{ default: '#F3F4F6' },
    text:      { primary: '#111827' },
  },
  shape: {
    borderRadius: 12,
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
