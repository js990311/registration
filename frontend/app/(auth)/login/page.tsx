"use client"

import styles from './loginPage.module.css'
import {useState} from "react";
import {useRouter, useSearchParams} from "next/navigation";
import {Card} from "@/src/components/Card/Card";
import {Button} from "@/src/components/button/Button";
import {FloatingLabelInput} from "@/src/components/Input/FloatingLabelInput";
import toast from "react-hot-toast";
import {signIn} from "next-auth/react";

export default function LoginPage() {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const router = useRouter();
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

        const result = await signIn("credentials", {
            username: username,
            password: password,
            redirect: false,
        });

        if (result?.error) {
            // 3. 로그인 실패 처리
            // authorize 콜백에서 null을 리턴하거나 에러가 발생하면 이쪽으로 들어옵니다.
            const errorMessage = "아이디 또는 비밀번호가 일치하지 않습니다.";
            toast.error(errorMessage);
            setError(errorMessage);
        } else {
            // 4. 로그인 성공 처리
            toast.success("로그인에 성공했습니다.");

            // NextAuth가 세션 쿠키를 생성하는 동안 잠시 기다리거나
            // 변경된 세션 상태를 모든 컴포넌트에 알리기 위해 refresh를 호출합니다.
            router.push(redirectUrl || "/"); // redirectUrl이 없으면 메인으로
            router.refresh();
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