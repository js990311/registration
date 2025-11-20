"use client"
import styles from "./Navigation.module.css"
import clsx from "clsx";

import {useState} from "react";

type DrawerType = {
    children: React.ReactNode;
    isOpen: boolean;
}

export function Drawer({children, isOpen}: Readonly<DrawerType>) {
    return (
        <div className={clsx(
            styles.drawerContainer,
            {[styles.drawerOpen]: isOpen},
        )}>
            {children}
        </div>
    );
}
