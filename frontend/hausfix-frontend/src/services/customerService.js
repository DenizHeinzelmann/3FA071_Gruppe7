import api from './api';

export const getAllCustomers = () => api.get('/customers');
export const getCustomerById = (id) => api.get(`/customers/${id}`);
export const createCustomer = (customer) => api.post('/customers', customer);
export const updateCustomer = (id, customer) => api.put(`/customers/${id}`, customer);
export const deleteCustomer = (id) => api.delete(`/customers/${id}`);
