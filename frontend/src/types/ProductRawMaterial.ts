import type { ProductRawMaterialItem } from "./ProductRawMaterialItem";

export interface ProductRawMaterial {
  id: string;
  code: string;
  name: string;
  rawMaterials: ProductRawMaterialItem[];
}