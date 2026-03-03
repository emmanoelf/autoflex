import type { ReactNode } from "react";
import { useState } from "react";
import { Sidebar } from "../Sidebar/Sidebar";
import styles from "./Layout.module.css";

interface Props {
  children: ReactNode;
}

export const Layout = ({ children }: Props) => {
  const [isOpen, setIsOpen] = useState(false);

  return (
    <div className={styles.layout}>
      <Sidebar isOpen={isOpen} setIsOpen={setIsOpen} />
      <div className={styles.main}>
        <button
          className={styles.menuButton}
          onClick={() => setIsOpen(true)}
        >
          ☰
        </button>

        {children}
      </div>
    </div>
  );
};