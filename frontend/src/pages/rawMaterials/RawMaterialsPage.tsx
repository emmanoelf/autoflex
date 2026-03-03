import { useEffect, useState } from "react";
import { rawMaterialService } from "../../services/rawMaterialService";
import type { RawMaterial } from "../../types/RawMaterial";
import { toast } from "react-toastify";
import { PageHeader, DataTable, Pagination, FormModal, ViewModal, StockBadge } from "../../components";
import type { Column } from "../../components";

const empty = { code: "", name: "", stockQuantity: 0 };

export const RawMaterialsPage = () => {
  const [materials, setMaterials] = useState<RawMaterial[]>([]);
  const [view, setView] = useState<RawMaterial | null>(null);
  const [edit, setEdit] = useState<RawMaterial | null>(null);
  const [form, setForm] = useState<Omit<RawMaterial, "id">>(empty);
  const [addOpen, setAddOpen] = useState(false);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const pageSize = 10;

  const load = async (p = page) => {
    try {
      const { data } = await rawMaterialService.findAll(p, pageSize);
      setMaterials(data.content);
      setPage(data.pageNumber);
      setTotalPages(data.totalPages);
    } catch { toast.error("Erro ao carregar matérias-primas"); }
  };

  useEffect(() => { load(); }, [page]);

  const handleAdd = async () => {
    try {
      await rawMaterialService.create(form);
      toast.success("Matéria-prima adicionada");
      setAddOpen(false);
      setForm(empty);
      load(page);
    } catch { toast.error("Erro ao adicionar"); }
  };

  const handleUpdate = async () => {
    if (!edit?.id) return;
    try {
      await rawMaterialService.update(edit.id, edit);
      toast.success("Matéria-prima atualizada");
      setEdit(null);
      load(page);
    } catch { toast.error("Erro ao atualizar"); }
  };

  const handleDelete = async (id: string) => {
    try {
      await rawMaterialService.delete(id);
      toast.success("Matéria-prima deletada");
      load(page);
    } catch { toast.error("Erro ao deletar"); }
  };

  const openEdit = async (id: string) => {
    try { setEdit((await rawMaterialService.findById(id)).data); }
    catch { toast.error("Erro ao buscar"); }
  };

  const openView = async (id: string) => {
    try { setView((await rawMaterialService.findById(id)).data); }
    catch { toast.error("Erro ao buscar"); }
  };

  const columns: Column<RawMaterial>[] = [
    { header: "Código", accessor: (m) => <span className="badge badge-blue">{m.code}</span> },
    { header: "Nome", accessor: "name" },
    { header: "Estoque", accessor: (m) => <StockBadge value={m.stockQuantity} /> },
    {
      header: "Ações", accessor: (m) => (
        <div className="btn-group">
          <button className="btn btn-dark btn-sm" onClick={() => openView(m.id!)}>Visualizar</button>
          <button className="btn btn-outline btn-sm" onClick={() => openEdit(m.id!)}>Editar</button>
          <button className="btn btn-danger btn-sm" onClick={() => handleDelete(m.id!)}>Deletar</button>
        </div>
      ),
    },
  ];

  const fields = (data: Omit<RawMaterial, "id">, set: (v: Omit<RawMaterial, "id">) => void) => (
    <>
      <div className="form-group">
        <label>Código</label>
        <input value={data.code} onChange={(e) => set({ ...data, code: e.target.value })} />
      </div>
      <div className="form-group">
        <label>Nome</label>
        <input value={data.name} onChange={(e) => set({ ...data, name: e.target.value })} />
      </div>
      <div className="form-group">
        <label>Quantidade em Estoque</label>
        <input type="number" min="0" value={data.stockQuantity}
          onChange={(e) => set({ ...data, stockQuantity: Number(e.target.value) })} />
      </div>
    </>
  );

  return (
    <div className="page">
      <PageHeader title="Matérias-Primas" subtitle="Gerencie o estoque de matérias-primas"
        actionLabel="+ Adicionar Matéria-Prima" onAction={() => { setForm(empty); setAddOpen(true); }} />

      <DataTable columns={columns} data={materials} rowKey="id"
        emptyIcon="🧱" emptyMessage="Nenhuma matéria-prima cadastrada ainda." />

      <Pagination page={page} totalPages={totalPages} onPageChange={setPage} />

      <FormModal isOpen={addOpen} title="Adicionar Matéria-Prima"
        onClose={() => setAddOpen(false)} onSave={handleAdd}>
        {fields(form, setForm)}
      </FormModal>

      <FormModal isOpen={!!edit} title="Editar Matéria-Prima"
        onClose={() => setEdit(null)} onSave={handleUpdate} saveLabel="Atualizar">
        {edit && fields(edit, (v) => setEdit({ ...edit, ...v }))}
      </FormModal>

      <ViewModal isOpen={!!view} title="Detalhes da Matéria-Prima" onClose={() => setView(null)}>
        <div className="form-group"><label>Código</label><input value={view?.code ?? ""} disabled /></div>
        <div className="form-group"><label>Nome</label><input value={view?.name ?? ""} disabled /></div>
        <div className="form-group"><label>Estoque</label><input value={view?.stockQuantity ?? 0} disabled /></div>
      </ViewModal>
    </div>
  );
};
