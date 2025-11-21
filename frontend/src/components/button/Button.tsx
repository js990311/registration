
import styles from "./button.module.css";
import clsx from "clsx";

type ButtonProps = {
    className?: string;
    children?: React.ReactNode;
    onClick?: () => void;
    type? : string;
    disabled?: boolean;
}

export function Button({type, children, className, onClick, disabled}: Readonly<ButtonProps>) {
    return (
        <button
            onClick={onClick}
            type={type ?? "button"}
            className={clsx(styles.button,className)}
            disabled={disabled}
        >
            {children}
        </button>
    )
}