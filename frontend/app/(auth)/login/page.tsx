"use client"

import styles from './loginPage.module.css'
import {useState} from "react";
import {useRouter, useSearchParams} from "next/navigation";
import useLoginStore from "@/src/stores/useLoginStore";
import {Card} from "@/src/components/Card/Card";
import {Button} from "@/src/components/button/Button";
import {FloatingLabelInput} from "@/src/components/Input/FloatingLabelInput";
import toast from "react-hot-toast";
import {ActionErrorResponse} from "@/src/type/response/actionResponse";
import {errorToString} from "@/src/lib/api/apiError";

export default function LoginPage() {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const router = useRouter();
    const login = useLoginStore((state) => state.login);
    const searchParams = useSearchParams();
    const redirectUrl = searchParams.get('redirect') || '';

    const onSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();

        if(!username) {
            setError("Username is required");
            return;
        }

        if(!password) {
            setError("Username is required");
            return;
        }

        const resp = await fetch("/api/login", {
            method: "POST",
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                'username': username,
                'password': password
            })
        });

        if(resp.status !== 200){
            const {success, error}:ActionErrorResponse = await resp.json();
            toast.error(errorToString("",error));
            setError(errorToString("",error));
        }else{
            toast.success("Login successfully");
            login();
            router.push(redirectUrl);
        }
    }

    return (
        <div className={"flex justify-center align-middle p-[3rem] w-full"}>
            <Card>
                <h2 className={"text-center text-lg font-bold text-xl mb-2"}>
                    로그인
                </h2>
                <form onSubmit={onSubmit}>
                    <div>
                        <div>
                            <div>
                                <FloatingLabelInput
                                    value={username}
                                    onChange={(e) => setUsername(e.target.value)}
                                    name="username"
                                    label={"아이디"}
                                />
                            </div>
                            <div>
                                <FloatingLabelInput
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                    name="password"
                                    label={"비밀번호"}
                                />
                            </div>
                        </div>
                        <div>
                            <Button type={"submit"} className={styles.loginButton}>
                                로그인
                            </Button>
                        </div>
                    </div>
                </form>
                <div>
                    <Button
                        onClick={() => {
                            router.push("/signup");
                        }}
                    >
                        회원가입하기
                    </Button>
                    <Button>
                        비밀번호찾기
                    </Button>
                </div>
            </Card>
        </div>
    );
}