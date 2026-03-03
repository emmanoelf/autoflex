import { api } from "../api/axios";
import type { Product } from "../types/Product";

export const productService = {
  findAll: () => api.get<Product[]>("/products/"),
  findById: (id: number) => api.get<Product>(`/products/${id}`),
  create: (data: Product) => api.post("/products", data),
  update: (id: number, data: Product) => api.put(`/products/${id}`, data),
  delete: (id: number) => api.delete(`/products/${id}`),
};