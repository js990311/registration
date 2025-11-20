
import styles from "./button.module.css";
import clsx from "clsx";

type ButtonProps = {
    className?: string;
    children?: React.ReactNode;
    onClick?: () => void;
    type? : string;
}

export function Button({type, children, className, onClick}: Readonly<ButtonProps>) {
    return (
        <button
            onClick={onClick}
            type={type ?? "button"}
            className={clsx(styles.button,className)}
        >
            {children}
        </button>
    )
}