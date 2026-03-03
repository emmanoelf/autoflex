interface PaginationProps {
  page: number;
  totalPages: number;
  onPageChange: (page: number) => void;
}

export const Pagination = ({ page, totalPages, onPageChange }: PaginationProps) => {
  if (totalPages <= 1) return null;
  return (
    <div className="pagination">
      <button
        className="btn btn-primary btn-sm"
        disabled={page === 0}
        onClick={() => onPageChange(page - 1)}
      >
        ← Anterior
      </button>
      <span>Página {page + 1} de {totalPages}</span>
      <button
        className="btn btn-primary btn-sm"
        disabled={page + 1 >= totalPages}
        onClick={() => onPageChange(page + 1)}
      >
        Próxima →
      </button>
    </div>
  );
};
