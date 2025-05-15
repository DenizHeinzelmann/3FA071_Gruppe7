import api from './api';

export const getAllReadings = () => api.get('/readings');
export const getReadingById = (id) => api.get(`/readings/${id}`);
export const createReading = (reading) => api.post('/readings', reading);
export const updateReading = (id, reading) => api.put(`/readings/${id}`, reading);
export const deleteReading = (id) => api.delete(`/readings/${id}`);
export const getReadingsByCustomer = (customerId) => api.get(`/readings/customer/${customerId}`);
