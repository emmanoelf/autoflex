import { useEffect, useState } from "react";
import { productService } from "../../services/productService";
import type { Product } from "../../types/Product";
import { toast } from "react-toastify";
import { PageHeader, DataTable, FormModal, ViewModal } from "../../components";
import type { Column } from "../../components";

const empty = { code: "", name: "", price: 0 };
const fmt = (v: number) => v.toLocaleString("pt-BR", { style: "currency", currency: "BRL" });

export const ProductsPage = () => {
  const [products, setProducts] = useState<Product[]>([]);
  const [view, setView] = useState<Product | null>(null);
  const [edit, setEdit] = useState<Product | null>(null);
  const [form, setForm] = useState<Omit<Product, "id">>(empty);
  const [addOpen, setAddOpen] = useState(false);

  const load = async () => {
    try { setProducts((await productService.findAll()).data); }
    catch { toast.error("Erro ao carregar produtos"); }
  };

  useEffect(() => { load(); }, []);

  const handleAdd = async () => {
    try {
      await productService.create(form);
      toast.success("Produto adicionado");
      setAddOpen(false);
      setForm(empty);
      load();
    } catch { toast.error("Erro ao adicionar produto"); }
  };

  const handleUpdate = async () => {
    if (!edit?.id) return;
    try {
      await productService.update(edit.id, edit);
      toast.success("Produto atualizado");
      setEdit(null);
      load();
    } catch { toast.error("Erro ao atualizar produto"); }
  };

  const handleDelete = async (id: string) => {
    try {
      await productService.delete(id);
      toast.success("Produto deletado");
      load();
    } catch { toast.error("Erro ao deletar produto"); }
  };

  const openEdit = async (id: string) => {
    try { setEdit((await productService.findById(id)).data); }
    catch { toast.error("Erro ao buscar produto"); }
  };

  const openView = async (id: string) => {
    try { setView((await productService.findById(id)).data); }
    catch { toast.error("Erro ao buscar produto"); }
  };

  const columns: Column<Product>[] = [
    { header: "Código", accessor: (p) => <span className="badge badge-blue">{p.code}</span> },
    { header: "Nome", accessor: "name" },
    { header: "Preço", accessor: (p) => fmt(p.price) },
    {
      header: "Ações", accessor: (p) => (
        <div className="btn-group">
          <button className="btn btn-dark btn-sm" onClick={() => openView(p.id!)}>Visualizar</button>
          <button className="btn btn-outline btn-sm" onClick={() => openEdit(p.id!)}>Editar</button>
          <button className="btn btn-danger btn-sm" onClick={() => handleDelete(p.id!)}>Deletar</button>
        </div>
      ),
    },
  ];

  const fields = (
    data: Omit<Product, "id">,
    set: (v: Omit<Product, "id">) => void
  ) => (
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
        <label>Preço</label>
        <input type="number" step="0.01" min="0" value={data.price}
          onChange={(e) => set({ ...data, price: Number(e.target.value) })} />
      </div>
    </>
  );

  return (
    <div className="page">
      <PageHeader title="Produtos" subtitle="Gerencie o cadastro de produtos"
        actionLabel="+ Adicionar Produto" onAction={() => { setForm(empty); setAddOpen(true); }} />

      <DataTable columns={columns} data={products} rowKey="id"
        emptyIcon="📦" emptyMessage="Nenhum produto cadastrado ainda." />

      <FormModal isOpen={addOpen} title="Adicionar Produto"
        onClose={() => setAddOpen(false)} onSave={handleAdd}>
        {fields(form, setForm)}
      </FormModal>

      <FormModal isOpen={!!edit} title="Editar Produto"
        onClose={() => setEdit(null)} onSave={handleUpdate} saveLabel="Atualizar">
        {edit && fields(edit, (v) => setEdit({ ...edit, ...v }))}
      </FormModal>

      <ViewModal isOpen={!!view} title="Detalhes do Produto" onClose={() => setView(null)}>
        <div className="form-group"><label>Código</label><input value={view?.code ?? ""} disabled /></div>
        <div className="form-group"><label>Nome</label><input value={view?.name ?? ""} disabled /></div>
        <div className="form-group"><label>Preço</label><input value={view ? fmt(view.price) : ""} disabled /></div>
      </ViewModal>
    </div>
  );
};
