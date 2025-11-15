"use client"

import {useState} from "react";
import {ProblemResponse} from "@/src/type/error/error";
import Lecture from "@/src/type/lecture/lecture";
import {useRouter} from "next/navigation";

export default function getLecturesPage(){
    const [name, setName] = useState('');
    const [capacity, setCapacity] = useState('');
    const [error, setError] = useState("");
    const router = useRouter();

    const onSubmit = async (e) => {
        e.preventDefault();

        const response = await fetch('/api/lectures', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                name: name,
                capacity: capacity,
            })
        });

        if(response.status === 201){
            const lecture:Lecture = await response.json();
            router.push(`/lectures/${lecture.lectureId}`);
        }else {
            const problem:ProblemResponse = await response.json();
            setError(problem.detail);
        }
    }

    return (
        <div>
            <form onSubmit={onSubmit}>
                <input type="text"
                       value={name}
                       onChange={(e) => setName(e.target.value)}
                />
                <input type="text"
                        value={capacity}
                       onChange={(e) => setCapacity(e.target.value)}
                />
                <button type="submit">
                    생성하기
                </button>
            </form>
            <div>
                {error && <p>{error}</p>}
            </div>
        </div>
    );
}