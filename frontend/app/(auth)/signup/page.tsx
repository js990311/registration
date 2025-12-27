"use client"

import {useState} from "react";
import {useRouter} from "next/navigation";
import {FloatingLabelInput} from "@/src/components/Input/FloatingLabelInput";
import {Button} from "@/src/components/button/Button";
import styles from "@/app/(auth)/login/loginPage.module.css";
import {Card} from "@/src/components/Card/Card";
import toast from "react-hot-toast";
import {ActionErrorResponse} from "@/src/type/response/actionResponse";
import {errorToString} from "@/src/lib/api/apiError";
import {signIn} from "next-auth/react";

export default function signUpPage() {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const router = useRouter();

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

        try {
            // STEP 1: Spring Boot 회원가입 API 호출
            const signupResp = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/signup`, {
                method: "POST",
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username, password })
            });

            if (!signupResp.ok) {
                // 실패 시 에러 처리 (기존 로직 활용)
                const { error }: ActionErrorResponse = await signupResp.json();
                toast.error(errorToString("", error));
                setError(errorToString("", error));
                return;
            }

            // STEP 2: 가입 성공 시 자동 로그인 시도 (NextAuth 세션 생성)
            toast.success("회원가입 성공! 로그인을 진행합니다.");

            const result = await signIn("credentials", {
                username,
                password,
                redirect: false, // 리다이렉트를 수동으로 제어
            });

            if (result?.error) {
                toast.error("자동 로그인에 실패했습니다. 로그인 페이지로 이동합니다.");
                router.push("/login");
            } else {
                toast.success("로그인 성공!");
                router.push("/");
                router.refresh(); // 세션 정보를 앱 전체에 반영
            }

        } catch (err) {
            console.error("Signup error:", err);
            toast.error("서버와의 통신에 실패했습니다.");
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