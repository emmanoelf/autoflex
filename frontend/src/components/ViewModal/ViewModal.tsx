import type { ReactNode } from "react";
import { Modal } from "../Modal/Modal";

interface ViewModalProps {
  isOpen: boolean;
  title: string;
  onClose: () => void;
  children: ReactNode;
}

export const ViewModal = ({ isOpen, title, onClose, children }: ViewModalProps) => (
  <Modal isOpen={isOpen} onClose={onClose}>
    <h3 className="modal-title">{title}</h3>
    {children}
  </Modal>
);
