"use client"

import { useState} from "react";
import {useRouter} from "next/navigation";
import toast from "react-hot-toast";
import {Card} from "@/src/components/Card/Card";
import {FloatingLabelInput} from "@/src/components/Input/FloatingLabelInput";
import {Button} from "@/src/components/button/Button";
import {createLecture} from "@/src/action/lectureAction";
import {errorToString} from "@/src/lib/api/apiError";

export default function createLecturePage(){
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string>('');
    const [name, setName] = useState('');
    const [credit, setCredit] = useState('');
    const [capacity, setCapacity] = useState('');
    const router = useRouter();

    const onCreate = (e) => {
        e.preventDefault();
        if(loading){
            return;
        }
        setLoading(true);

        const capacityNumber = parseInt(capacity);
        if(isNaN(capacityNumber)) {
            setError(`capccity must be a number`);
            return;
        }

        const creditNumber = parseInt(credit);
        if(isNaN(creditNumber)) {
            setError(`credit must be a number`);
            return;
        }

        createLecture({
            name: name,
            credit: creditNumber,
            capacity: capacityNumber
        }).then((resp) => {
            setLoading(false);
            if(!resp.success){
                toast.error(errorToString("",resp.error));
            }else {
                router.push(`/lectures/${resp.data.lectureId}`);
            }
        });
    };

    return (
        <div>
            <Card>
                <h2 className={"text-center text-lg font-bold text-xl mb-2"}>
                    강의생성 페이지
                </h2>
                <form onSubmit={onCreate}>
                    <p>{error}</p>
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