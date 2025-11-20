
import styles from './Card.module.css';
import {ReactNode} from "react";
import clsx from "clsx";

type CardProps = {
    children: ReactNode;
    className?: string;
};

export function Card({children, className}: CardProps) {
    return (
        <div className={clsx(styles.card, className)}>
            {children}
        </div>
    )
}