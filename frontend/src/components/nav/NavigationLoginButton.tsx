"use client"

import useLoginStore from "@/src/stores/useLoginStore";
import {useRouter} from "next/navigation";
import {useEffect} from "react";

export default function NavigationLoginButton(){
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
        const resp = await fetch("/api/logout", {method: "POST"});
        if(resp.status === 204){
            logout();
        }
    }

    if(isLogin){
        return (
            <div>
                <p>로그인 완료</p>
                <button onClick={onLogout}>로그아웃</button>
            </div>
        );
    }else {
        return (
            <div>
                <button onClick={() => router.push("/login")}>로그인하기</button>
            </div>
        );
    }
}