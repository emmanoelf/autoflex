import { NavLink } from "react-router-dom";
import styles from "./Sidebar.module.css";

interface Props {
  isOpen: boolean;
  setIsOpen: (value: boolean) => void;
}

export const Sidebar = ({ isOpen, setIsOpen }: Props) => {
  const getNavLinkClass = ({ isActive }: { isActive: boolean }) =>
    `${styles.navLink} ${isActive ? styles.active : ""}`;

  return (
    <>
      <div
        className={`${styles.overlay} ${isOpen ? styles.show : ""}`}
        onClick={() => setIsOpen(false)}
      />

      <aside className={`${styles.sidebar} ${isOpen ? styles.open : ""}`}>
        <h2 className={styles.logo}>Autoflex</h2>

        <nav className={styles.nav}>
          <NavLink
            to="/"
            onClick={() => setIsOpen(false)}
            className={getNavLinkClass}
          >
            Produtos
          </NavLink>

          <NavLink
            to="/raw-materials"
            onClick={() => setIsOpen(false)}
            className={getNavLinkClass}
          >
            Materias-primas
          </NavLink>

          <NavLink
            to="/products-raw-materials"
            onClick={() => setIsOpen(false)}
            className={getNavLinkClass}
          >
            Associações
          </NavLink>

          <NavLink
            to="/suggested-production-available"
            onClick={() => setIsOpen(false)}
            className={getNavLinkClass}
          >
            Sugestão de produção
          </NavLink>
        </nav>
      </aside>
    </>
  );
};