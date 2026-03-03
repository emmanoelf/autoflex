import { api } from "../api/axios";
import type { PageResponse } from "../types/PageResponse";
import type { ProductRawMaterial } from "../types/ProductRawMaterial";
import type { ProductRawMaterialCreatePayload } from "../types/ProductRawMaterialCreatePayload";
import type { SuggestedProduction } from "../types/SuggestedProduction";

export interface ProductRawMaterialUpdatePayload {
  rawMaterialId: string;
  requiredQuantity: number;
}

export const productRawMaterialService = {
  getSuggestedProduction: () => api.get<SuggestedProduction[]>("/products-raw-materials/suggested-production-available"),

  findAll: (page = 0, size = 10) =>
    api.get<PageResponse<ProductRawMaterial>>("/products-raw-materials/", {
      params: { page, size },
  }),

  findById: (productId: string) => api.get<ProductRawMaterial>(`/products-raw-materials/${productId}`),
  create: (payload: ProductRawMaterialCreatePayload) => api.post("/products-raw-materials/associate", payload),

  update: (productRawMaterialId: string, payload: ProductRawMaterialUpdatePayload) =>
    api.put(`/products-raw-materials/${productRawMaterialId}`, payload),

  delete: (productRawMaterialId: string) => api.delete(`/products-raw-materials/${productRawMaterialId}`),
};
