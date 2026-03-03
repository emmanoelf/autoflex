import { useEffect, useState } from "react";
import { productRawMaterialService } from "../../services/productRawMaterialService";
import type { SuggestedProduction } from "../../types/SuggestedProduction";
import { toast } from "react-toastify";
import { PageHeader, DataTable, ViewModal } from "../../components";
import type { Column } from "../../components";

const fmt = (v: number) => v.toLocaleString("pt-BR", { style: "currency", currency: "BRL" });

export const SuggestedProductionPage = () => {
  const [suggestions, setSuggestions] = useState<SuggestedProduction[]>([]);
  const [view, setView] = useState<SuggestedProduction | null>(null);
  const [loading, setLoading] = useState(false);

  const load = async () => {
    setLoading(true);
    try { setSuggestions((await productRawMaterialService.getSuggestedProduction()).data); }
    catch { toast.error("Erro ao carregar sugestões"); }
    finally { setLoading(false); }
  };

  useEffect(() => { load(); }, []);

  const totalValue = suggestions.reduce((acc, p) => acc + (p.totalValue ?? 0), 0);

  const columns: Column<SuggestedProduction>[] = [
    { header: "Código", accessor: (p) => <span className="badge badge-blue">{p.code}</span> },
    { header: "Nome", accessor: "name" },
    { header: "Preço Unit.", accessor: (p) => fmt(p.price) },
    {
      header: "Qtd. Produzível",
      accessor: (p) => <span className="badge badge-green">{p.quantityProductionAvailable}</span>,
    },
    {
      header: "Valor Total",
      accessor: (p) => <strong style={{ color: "var(--color-success)" }}>{fmt(p.totalValue)}</strong>,
    },
    {
      header: "Ações",
      accessor: (p) => (
        <button className="btn btn-dark btn-sm" onClick={() => setView(p)}>Visualizar</button>
      ),
    },
  ];

  return (
    <div className="page">
      <PageHeader
        title="Sugestão de Produção"
        subtitle="Produtos que podem ser produzidos com o estoque atual, ordenados por maior valor"
        actionLabel={loading ? "Carregando..." : "↻ Atualizar"}
        onAction={load}
      />

      {suggestions.length > 0 && (
        <div className="summary-bar">
          <div>
            <div className="summary-label">Valor Total de Produção</div>
            <div style={{ fontSize: 13, color: "var(--color-text-muted)", marginTop: 2 }}>
              Baseado no estoque disponível
            </div>
          </div>
          <div className="summary-value">{fmt(totalValue)}</div>
        </div>
      )}

      <DataTable columns={columns} data={suggestions} rowKey="productId"
        emptyIcon="⚡" emptyMessage="Nenhum produto pode ser produzido com o estoque atual." />

      <ViewModal isOpen={!!view} title="Detalhes da Sugestão" onClose={() => setView(null)}>
        <div className="form-grid">
          <div className="form-group"><label>Código</label><input value={view?.code ?? ""} disabled /></div>
          <div className="form-group"><label>Nome</label><input value={view?.name ?? ""} disabled /></div>
        </div>
        <div className="form-grid">
          <div className="form-group"><label>Preço Unitário</label><input value={view ? fmt(view.price) : ""} disabled /></div>
          <div className="form-group"><label>Qtd. Produzível</label><input value={view?.quantityProductionAvailable ?? 0} disabled /></div>
        </div>
        <div className="form-group"><label>Valor Total</label><input value={view ? fmt(view.totalValue) : ""} disabled /></div>
      </ViewModal>
    </div>
  );
};
