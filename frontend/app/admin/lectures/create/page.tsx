"use client"

import {useEffect, useState} from "react";
import {ProblemResponse} from "@/src/type/error/error";
import Lecture from "@/src/type/lecture/lecture";
import {useRouter} from "next/navigation";
import {useCreateLecture} from "@/src/hooks/lectureHook";
import toast from "react-hot-toast";
import {Card} from "@/src/components/Card/Card";
import {FloatingLabelInput} from "@/src/components/Input/FloatingLabelInput";
import {Button} from "@/src/components/button/Button";

export default function getLecturesPage(){
    const {
        loading, error, name, setName, capacity, setCapacity, createLecture, credit, setCredit
    } = useCreateLecture();

    useEffect(()=>{
        if(error){
            toast.error(error);
        }
    },[error]);

    return (
        <div>
            <Card>
                <h2 className={"text-center text-lg font-bold text-xl mb-2"}>
                    강의생성 페이지
                </h2>
                <form onSubmit={createLecture}>
                    <FloatingLabelInput
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                        name="name"
                        label={'강의명'}
                    />
                    <FloatingLabelInput
                        value={capacity}
                        onChange={(e) => setCapacity(e.target.value)}
                        name="capacity"
                        label={"정원"}
                    />
                    <FloatingLabelInput
                        value={credit}
                        onChange={(e) => setCredit(e.target.value)}
                        name="credit"
                        label={"학점"}
                    />

                    <Button type="submit">
                        생성하기
                    </Button>
                </form>

            </Card>
        </div>
    );
}