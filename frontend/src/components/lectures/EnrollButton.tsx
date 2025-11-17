"use client"

import {CreateRegistrationRequest} from "@/src/type/registration/registration";
import {useState} from "react";
import {ProblemResponse} from "@/src/type/error/error";

type EnrollButtonPageProps = {
    lectureId: number
}

export default function EnrollButton({lectureId} : Readonly<EnrollButtonPageProps>) {
    const [message, setMessage] = useState<string>();

    const onEnroll =  async () => {
        const request: CreateRegistrationRequest = {
            lectureId: lectureId,
        };

        const response = await fetch(`/api/registrations`, {
            method: "POST",
            body: JSON.stringify(request)
        });

        if(response.status === 201) {
            setMessage('success');
        }else{
            const problem : ProblemResponse = await response.json();
            setMessage(problem.detail);
        }
    }

    return (
        <div>
            <button onClick={onEnroll}>
                수강신청
            </button>
            <div>
                {message}
            </div>
        </div>
    )
}