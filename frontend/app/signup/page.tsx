"use client"

import {useState} from "react";
import {useRouter} from "next/navigation";
import {ProblemResponse} from "@/src/type/error/error";
import useLoginStore from "@/src/stores/useLoginStore";

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
            setError(json.problem.detail);
        }else{
            login();
            router.push("/");
        }
    }

    return (
        <div>
            <form onSubmit={onSubmit}>
                <input
                    type="text"
                    value={username}
                    onChange={(e: React.ChangeEvent<HTMLInputElement>) => {
                        setUsername(e.target.value);
                    }}
                />
                <input
                    type="password"
                    value={password}
                    onChange={(e: React.ChangeEvent<HTMLInputElement>) => {
                        setPassword(e.target.value);
                    }}
                />
                <button type={"submit"}>
                    회원가입
                </button>
            </form>
            <div>
                <p>
                    {error}
                </p>
            </div>
        </div>
    );
}