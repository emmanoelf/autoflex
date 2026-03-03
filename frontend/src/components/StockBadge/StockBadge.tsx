interface StockBadgeProps {
  value: number;
}

export const StockBadge = ({ value }: StockBadgeProps) => {
  const cls = value === 0 ? "stock-zero" : value < 10 ? "stock-low" : "stock-ok";
  return <span className={cls}>{value}</span>;
};
