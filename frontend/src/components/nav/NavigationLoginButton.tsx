"use client"

import styles from "./Navigation.module.css"
import useLoginStore from "@/src/stores/useLoginStore";
import {useRouter} from "next/navigation";
import {useEffect, useState} from "react";
import {Button} from "@/src/components/button/Button";
import { FaUserAlt } from "react-icons/fa";
import clsx from "clsx";
import { LuLogIn, LuLogOut  } from "react-icons/lu";
import Link from "next/link";
import toast from "react-hot-toast";

export default function NavigationLoginButton(){
    const [isOpen, setIsOpen] = useState<boolean>(false);
    const isLogin = useLoginStore((state) => state.isLogin);
    const login = useLoginStore((state) => state.login);
    const logout = useLoginStore((state) => state.logout);
    const router = useRouter();

    const fetchLoginInfo = async () => {
        const resp = await fetch("/api/me");
        if(resp.ok){
            login();
        }
    }

    useEffect(() => {
        fetchLoginInfo();
    }, []);

    const onLogout = async () => {
        setIsOpen(false);

        const resp = await fetch("/api/logout", {method: "POST"});
        if(resp.status === 204){
            logout();
            toast.success("로그아웃 성공");
        }else {
            toast.error("로그아웃 실패");
        }
    }

    return (
        <div className={clsx(styles.dropdownParent)}>
            <Button
                onClick={() => setIsOpen(!isOpen)}
            >
                <FaUserAlt />
            </Button>
            <div
                className={clsx(
                    styles.dropdown,
                    {[styles.dropdownOpen]: isOpen}
                )}
            >
                {/*    드롭다운     */}
                {
                    isLogin ? (
                        <div>
                            <Link href={"/students/me"}>
                                내정보보기
                            </Link>
                            <Button
                                className={clsx(styles.iconWithText)}
                                onClick={onLogout}
                            >
                                <LuLogOut /> <span>로그아웃</span>
                            </Button>
                        </div>
                    ) : (
                        <div>
                            <Button
                                className={clsx(styles.iconWithText)}
                                onClick={() => {
                                        setIsOpen(false);
                                        router.push("/login")
                                    }
                                }
                            >
                                <LuLogIn /> <span>로그인</span>
                            </Button>
                        </div>
                    )
                }
            </div>
        </div>
    );
}