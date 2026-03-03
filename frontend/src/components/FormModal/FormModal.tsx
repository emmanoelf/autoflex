import type { ReactNode } from "react";
import { Modal } from "../Modal/Modal";

interface FormModalProps {
  isOpen: boolean;
  title: string;
  onClose: () => void;
  onSave: () => void;
  saveLabel?: string;
  children: ReactNode;
}

export const FormModal = ({
  isOpen,
  title,
  onClose,
  onSave,
  saveLabel = "Salvar",
  children,
}: FormModalProps) => (
  <Modal isOpen={isOpen} onClose={onClose}>
    <h3 className="modal-title">{title}</h3>
    {children}
    <div className="btn-group" style={{ marginTop: 8 }}>
      <button className="btn btn-success" onClick={onSave}>{saveLabel}</button>
      <button className="btn btn-outline" onClick={onClose}>Cancelar</button>
    </div>
  </Modal>
);
