export interface ProductRawMaterialCreatePayload {
  productId: string;
  rawMaterials: {
    rawMaterialId: string;
    requiredQuantity: number;
  }[];
}