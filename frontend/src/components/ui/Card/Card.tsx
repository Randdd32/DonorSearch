import type { ReactNode, HTMLAttributes } from 'react';
import { clsx } from 'clsx';
import styles from './Card.module.css';

interface CardProps extends HTMLAttributes<HTMLDivElement> {
  children: ReactNode;
  isHoverable?: boolean;
}

export const Card = ({ children, className, isHoverable = false, ...props }: CardProps) => {
  return (
    <div 
      className={clsx(styles.card, { [styles.hoverable]: isHoverable }, className)} 
      {...props}
    >
      {children}
    </div>
  );
};