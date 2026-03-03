interface PageHeaderProps {
  title: string;
  subtitle?: string;
  actionLabel?: string;
  onAction?: () => void;
}

export const PageHeader = ({ title, subtitle, actionLabel, onAction }: PageHeaderProps) => (
  <div className="page-header">
    <div>
      <h2 className="page-title">{title}</h2>
      {subtitle && <p className="page-subtitle">{subtitle}</p>}
    </div>
    {actionLabel && onAction && (
      <button className="btn btn-success" onClick={onAction}>
        {actionLabel}
      </button>
    )}
  </div>
);
