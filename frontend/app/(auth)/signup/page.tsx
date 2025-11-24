"use client"

import {useState} from "react";
import {useRouter} from "next/navigation";
import {ProblemResponse} from "@/src/type/error/error";
import useLoginStore from "@/src/stores/useLoginStore";
import {FloatingLabelInput} from "@/src/components/Input/FloatingLabelInput";
import {Button} from "@/src/components/button/Button";
import styles from "@/app/(auth)/login/loginPage.module.css";
import {Card} from "@/src/components/Card/Card";
import toast from "react-hot-toast";

export default function signUpPage() {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const router = useRouter();
    const login = useLoginStore((state) => state.login);

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

        const resp = await fetch("/api/signup", {
            method: "POST",
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                'username': username,
                'password': password
            })
        });

        if(resp.status !== 201){
            const json:{success:boolean, problem: ProblemResponse} = await resp.json();
            toast.error(json.problem.detail);
        }else{
            login();
            router.push("/");
        }
    }

    return (
        <div className={"flex justify-center align-middle p-[3rem] w-full"}>
            <Card>
                <h2 className={"text-center text-lg font-bold text-xl mb-2"}>
                    회원가입
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
            </Card>
        </div>
    );
}