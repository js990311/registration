"use client"

import {useEffect, useState} from "react";
import {ProblemResponse} from "@/src/type/error/error";
import Lecture from "@/src/type/lecture/lecture";
import {useRouter} from "next/navigation";
import {useCreateLecture} from "@/src/hooks/lectureHook";
import toast from "react-hot-toast";

export default function getLecturesPage(){
    const {
        loading, error, name, setName, capacity, setCapacity, createLecture
    } = useCreateLecture();

    useEffect(()=>{
        if(error){
            toast.error(error);
        }
    },[error]);

    return (
        <div>
            <form onSubmit={createLecture}>
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
        </div>
    );
}