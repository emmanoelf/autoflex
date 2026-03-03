import { BrowserRouter, Routes, Route } from "react-router-dom";
import { Toast } from "./components/Toast";
import { ProductsPage } from "./pages/products/ProductsPage";
import { Layout } from "./components/Layout/Layout";
import { RawMaterialsPage } from "./pages/rawMaterials/RawMaterialsPage";
import { SuggestedProductionPage } from "./pages/suggestedProduction/SuggestedProductionPage";
import { ProductsRawMaterialsPage } from "./pages/productsRawMaterials/ProductsRawMaterials";

function App() {
  return (
    <BrowserRouter>
      <Toast />
      <Layout>
        <Routes>
          <Route path="/" element={<ProductsPage />} />
          <Route path="/raw-materials/" element={<RawMaterialsPage />} />
          <Route path="/products-raw-materials/" element={<ProductsRawMaterialsPage />} /> 
          <Route path="/suggested-production-available/" element={<SuggestedProductionPage />} />
        </Routes>
      </Layout>
    </BrowserRouter>
  );
}

export default App;