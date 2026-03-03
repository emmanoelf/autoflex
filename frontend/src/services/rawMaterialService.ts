import { api } from "../api/axios";
import type { RawMaterial } from "../types/RawMaterial";
import type { PageResponse } from "../types/PageResponse";

export const rawMaterialService = {
  findAll: (page: number = 0, size: number = 5) =>
    api.get<PageResponse<RawMaterial>>(`/raw-materials/?page=${page}&size=${size}`),
  
  findById: (id: number) => api.get<RawMaterial>(`/raw-materials/${id}`),
  create: (data: RawMaterial) => api.post("/raw-materials", data),
  update: (id: number, data: RawMaterial) => api.put(`/raw-materials/${id}`, data),
  delete: (id: number) => api.delete(`/raw-material/${id}`),
};