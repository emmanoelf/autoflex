import { useEffect, useState } from "react";
import { productService } from "../../services/productService";
import { rawMaterialService } from "../../services/rawMaterialService";
import { productRawMaterialService } from "../../services/productRawMaterialService";
import type { Product } from "../../types/Product";
import type { RawMaterial } from "../../types/RawMaterial";
import type { ProductRawMaterial } from "../../types/ProductRawMaterial";
import type { ProductRawMaterialCreatePayload } from "../../types/ProductRawMaterialCreatePayload";
import { Modal } from "../../components/Modal/Modal";
import { toast } from "react-toastify";
import { PageHeader, DataTable, Pagination, FormModal, ViewModal, StockBadge } from "../../components";
import type { Column } from "../../components";

type EditMap = Record<string, { rawMaterialId: string; requiredQuantity: number }>;
type CreateItem = { rawMaterialId: string; requiredQuantity: number };

export const ProductsRawMaterialsPage = () => {
  const [associations, setAssociations] = useState<ProductRawMaterial[]>([]);
  const [products, setProducts] = useState<Product[]>([]);
  const [rawMaterials, setRawMaterials] = useState<RawMaterial[]>([]);
  const [selected, setSelected] = useState<ProductRawMaterial | null>(null);

  const [createOpen, setCreateOpen] = useState(false);
  const [editOpen, setEditOpen] = useState(false);
  const [viewOpen, setViewOpen] = useState(false);

  const [selectedProductId, setSelectedProductId] = useState("");
  const [createItems, setCreateItems] = useState<CreateItem[]>([{ rawMaterialId: "", requiredQuantity: 1 }]);

  const [editMap, setEditMap] = useState<EditMap>({});
  const [savingId, setSavingId] = useState<string | null>(null);

  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const load = async (p = page) => {
    try {
      const [prodRes, rmRes, assocRes] = await Promise.all([
        productService.findAll(),
        rawMaterialService.findAll(0, 100),
        productRawMaterialService.findAll(p, 10),
      ]);
      setProducts(prodRes.data);
      setRawMaterials(rmRes.data.content);
      setAssociations(assocRes.data.content);
      setTotalPages(assocRes.data.totalPages);
    } catch { toast.error("Erro ao carregar dados"); }
  };

  useEffect(() => { load(); }, [page]);

  const resetCreate = () => {
    setSelectedProductId("");
    setCreateItems([{ rawMaterialId: "", requiredQuantity: 1 }]);
  };

  const addCreateItem = () =>
    setCreateItems((prev) => [...prev, { rawMaterialId: "", requiredQuantity: 1 }]);

  const removeCreateItem = (index: number) =>
    setCreateItems((prev) => prev.filter((_, i) => i !== index));

  const updateCreateItem = (index: number, field: keyof CreateItem, value: string | number) =>
    setCreateItems((prev) =>
      prev.map((item, i) => (i === index ? { ...item, [field]: value } : item))
    );

  const handleCreate = async () => {
    if (!selectedProductId || createItems.some((i) => !i.rawMaterialId)) {
      toast.error("Selecione produto e todas as matérias-primas");
      return;
    }
    const payload: ProductRawMaterialCreatePayload = {
      productId: selectedProductId,
      rawMaterials: createItems.map((i) => ({
        rawMaterialId: i.rawMaterialId,
        requiredQuantity: i.requiredQuantity,
      })),
    };
    try {
      await productRawMaterialService.create(payload);
      toast.success("Associação criada com sucesso");
      setCreateOpen(false);
      resetCreate();
      setPage(0);
      load(0);
    } catch { toast.error("Erro ao criar associação"); }
  };

  const openEditModal = (assoc: ProductRawMaterial) => {
    setSelected(assoc);
    const initial: EditMap = {};
    assoc.rawMaterials.forEach((rm) => {
      initial[rm.productRawMaterialId] = {
        rawMaterialId: rm.rawMaterialId,
        requiredQuantity: rm.requiredQuantity,
      };
    });
    setEditMap(initial);
    setEditOpen(true);
  };

  const updateEditRow = (id: string, field: "rawMaterialId" | "requiredQuantity", value: string | number) => {
    setEditMap((prev) => ({ ...prev, [id]: { ...prev[id], [field]: value } }));
  };

  const handleUpdateRow = async (productRawMaterialId: string) => {
    const row = editMap[productRawMaterialId];
    if (!row?.rawMaterialId || row.requiredQuantity < 1) {
      toast.error("Dados inválidos"); return;
    }
    setSavingId(productRawMaterialId);
    try {
      await productRawMaterialService.update(productRawMaterialId, row);
      toast.success("Linha atualizada");
      load();
    } catch { toast.error("Erro ao atualizar"); }
    finally { setSavingId(null); }
  };

  const handleDelete = async (id: string) => {
    try {
      await productRawMaterialService.delete(id);
      toast.success("Associação removida"); load();
    } catch { toast.error("Erro ao remover"); }
  };

  const columns: Column<ProductRawMaterial>[] = [
    { header: "Código", accessor: (a) => <span className="badge badge-blue">{a.code}</span> },
    { header: "Produto", accessor: "name" },
    { header: "Matérias-Primas", accessor: (a) => <span className="badge badge-gray">{a.rawMaterials.length} item(s)</span> },
    {
      header: "Ações", accessor: (a) => (
        <div className="btn-group">
          <button className="btn btn-dark btn-sm" onClick={() => { setSelected(a); setViewOpen(true); }}>Visualizar</button>
          <button className="btn btn-outline btn-sm" onClick={() => openEditModal(a)}>Editar</button>
          <button className="btn btn-danger btn-sm" onClick={() => handleDelete(a.rawMaterials[0]?.productRawMaterialId)}>Deletar</button>
        </div>
      ),
    },
  ];

  return (
    <div className="page">
      <PageHeader
        title="Associações"
        subtitle="Vincule matérias-primas aos produtos"
        actionLabel="+ Criar Associação"
        onAction={() => setCreateOpen(true)}
      />

      <DataTable
        columns={columns}
        data={associations}
        rowKey="id"
        emptyIcon="🔗"
        emptyMessage="Nenhuma associação cadastrada ainda."
      />

      <Pagination page={page} totalPages={totalPages} onPageChange={setPage} />

      <FormModal
        isOpen={createOpen}
        title="Criar Associação"
        onClose={() => { setCreateOpen(false); resetCreate(); }}
        onSave={handleCreate}
      >
        <div className="form-group">
          <label>Produto</label>
          <select value={selectedProductId} onChange={(e) => setSelectedProductId(e.target.value)}>
            <option value="">Selecione um produto</option>
            {products.map((p) => <option key={p.id} value={p.id}>{p.name} ({p.code})</option>)}
          </select>
        </div>

        {createItems.map((item, index) => (
          <div key={index} style={{ display: "flex", gap: 8, alignItems: "flex-end", marginBottom: 10 }}>
            <div className="form-group" style={{ flex: 1, marginBottom: 0 }}>
              <label>Matéria-Prima</label>
              <select
                value={item.rawMaterialId}
                onChange={(e) => updateCreateItem(index, "rawMaterialId", e.target.value)}
              >
                <option value="">Selecione uma matéria-prima</option>
                {rawMaterials.map((rm) => (
                  <option key={rm.id} value={rm.id}>{rm.name} ({rm.code})</option>
                ))}
              </select>
            </div>
            <div className="form-group" style={{ width: 110, marginBottom: 0 }}>
              <label>Quantidade</label>
              <input
                type="number"
                min="1"
                style={{ marginBottom: 0 }}
                value={item.requiredQuantity}
                onChange={(e) => updateCreateItem(index, "requiredQuantity", Number(e.target.value))}
              />
            </div>
            {createItems.length > 1 && (
              <div className="form-group" style={{ marginBottom: 0 }}>
                <label style={{ visibility: "hidden" }}>.</label>
                <button
                  className="btn btn-danger btn-sm"
                  style={{ display: "block" }}
                  onClick={() => removeCreateItem(index)}
                >
                  ✕
                </button>
              </div>
            )}
          </div>
        ))}

        <button
          className="btn btn-outline btn-sm"
          style={{ marginTop: 4, marginBottom: 8 }}
          onClick={addCreateItem}
        >
          + Adicionar matéria-prima
        </button>
      </FormModal>

      <Modal isOpen={editOpen} onClose={() => setEditOpen(false)}>
        <h3 className="modal-title">Editar Associações — {selected?.name}</h3>
        <p style={{ fontSize: 13, color: "var(--color-text-muted)", marginBottom: 16 }}>
          Altere a matéria-prima e/ou a quantidade de cada linha e clique em <strong>Salvar</strong>.
        </p>

        {!selected?.rawMaterials.length ? (
          <p style={{ color: "var(--color-text-muted)" }}>Nenhuma matéria-prima associada.</p>
        ) : (
          <table>
            <thead>
              <tr>
                <th>Matéria-Prima</th>
                <th>Estoque</th>
                <th style={{ width: 110 }}>Quantidade</th>
                <th style={{ width: 80 }}></th>
              </tr>
            </thead>
            <tbody>
              {selected.rawMaterials.map((rm) => {
                const row = editMap[rm.productRawMaterialId];
                const stock = rawMaterials.find((r) => r.id === row?.rawMaterialId)?.stockQuantity ?? rm.stockQuantity;
                return (
                  <tr key={rm.productRawMaterialId}>
                    <td>
                      <select
                        style={{ width: "100%", padding: "6px 8px" }}
                        value={row?.rawMaterialId ?? rm.rawMaterialId}
                        onChange={(e) => updateEditRow(rm.productRawMaterialId, "rawMaterialId", e.target.value)}
                      >
                        {rawMaterials.map((r) => (
                          <option key={r.id} value={r.id}>{r.name} ({r.code})</option>
                        ))}
                      </select>
                    </td>
                    <td><StockBadge value={stock} /></td>
                    <td>
                      <input
                        type="number"
                        min="1"
                        style={{ width: "100%", padding: "6px 8px", marginBottom: 0 }}
                        value={row?.requiredQuantity ?? rm.requiredQuantity}
                        onChange={(e) => updateEditRow(rm.productRawMaterialId, "requiredQuantity", Number(e.target.value))}
                      />
                    </td>
                    <td>
                      <button
                        className="btn btn-success btn-sm"
                        disabled={savingId === rm.productRawMaterialId}
                        onClick={() => handleUpdateRow(rm.productRawMaterialId)}
                      >
                        {savingId === rm.productRawMaterialId ? "..." : "Salvar"}
                      </button>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        )}

        <div style={{ marginTop: 20 }}>
          <button className="btn btn-outline" onClick={() => setEditOpen(false)}>Fechar</button>
        </div>
      </Modal>

      <ViewModal isOpen={viewOpen} title="Detalhes do Produto" onClose={() => setViewOpen(false)}>
        {selected && (
          <>
            <div className="form-grid" style={{ marginBottom: 16 }}>
              <div className="form-group"><label>Código</label><input value={selected.code} disabled /></div>
              <div className="form-group"><label>Nome</label><input value={selected.name} disabled /></div>
            </div>
            <table>
              <thead>
                <tr>
                  <th>Matéria-Prima</th>
                  <th>Qtd Necessária</th>
                  <th>Estoque</th>
                </tr>
              </thead>
              <tbody>
                {selected.rawMaterials.map((rm) => (
                  <tr key={rm.productRawMaterialId}>
                    <td>{rm.rawMaterialName}</td>
                    <td>{rm.requiredQuantity}</td>
                    <td><StockBadge value={rm.stockQuantity} /></td>
                  </tr>
                ))}
              </tbody>
            </table>
          </>
        )}
      </ViewModal>
    </div>
  );
};
