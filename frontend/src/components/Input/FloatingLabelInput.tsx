import clsx from "clsx";
import styles from './FloatingLabelInput.module.css';

type FloatingLabelInputProps = {
    className?: string
    value?: string
    name?: string
    placeholder?: string
    label?: string
    onChange?: (e) => void
}

export function FloatingLabelInput({className, value, name, placeholder, label, onChange}: Readonly<FloatingLabelInputProps>) {
    return (
        <div className={clsx(styles.floatingBox)}>
            <input
                className={clsx(styles.floatingInput, className)}
                type="text"
                name={name}
                id={name}
                value={value}
                placeholder={placeholder?? " "}
                onChange={onChange}
            />
            <label
                className={clsx(styles.floatingLabel)}
                htmlFor={name}>{label}</label>
        </div>
    )
}