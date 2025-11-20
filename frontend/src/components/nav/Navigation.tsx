"use client"

import styles from "./Navigation.module.css"
import NavigationLoginButton from "@/src/components/nav/NavigationLoginButton";
import Link from "next/link";
import {Drawer} from "@/src/components/nav/Drawer";
import {useState} from "react";
import {Button} from "@/src/components/button/Button";
import { TfiMenu } from "react-icons/tfi";

export default function Navigation(){
    const [isOpen, setIsOpen] = useState<boolean>(false);

    return (
        <div className={styles.navBar}>
            <div>
                <Link href={"/"}>
                    로고
                </Link>
            </div>
            <div className={"flex justify-space-between"}>
                <NavigationLoginButton />
                <Button onClick={() => setIsOpen(!isOpen)}>
                    <TfiMenu />
                </Button>
                <Drawer isOpen={isOpen}>
                    <Link href="/lectures">
                        강의보기
                    </Link>
                    <Link href={"/students/me"}>
                        내 정보 보기
                    </Link>
                    <Link href={"/registrations"}>
                        수강신청하기
                    </Link>
                </Drawer>
            </div>
        </div>
    )
}