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
import {signOut, useSession} from "next-auth/react";

export default function NavigationLoginButton(){
    const [isOpen, setIsOpen] = useState<boolean>(false);
    const router = useRouter();
    const { data: session, status }  = useSession();

    const onLogout = async () => {
        setIsOpen(false);

        try{
             await signOut({
                redirect: false,
                callbackUrl: "/"
            });

            toast.success("로그아웃되었습니다.");
            router.push("/");
            router.refresh();
        }catch (error){
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
                    status === 'authenticated' ? (
                        <div>
                            <div className={styles.userInfo}>
                                <span className={styles.userName}>
                                    <strong>{session?.user?.name}</strong>님
                                </span>
                            </div>
                            <div className="w-[100%] my-[1%] border-[1px] border-lightGray/30"></div>
                            <Link href={"/students/me"}>
                                내정보보기
                            </Link>
                            <Button
                                className={clsx(styles.iconWithText)}
                                onClick={onLogout}
                            >
                                <LuLogOut/> <span>로그아웃</span>
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