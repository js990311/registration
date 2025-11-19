"use client"

import {useEnroll} from "@/src/hooks/registrationHook";
import toast from "react-hot-toast";
import {useEffect} from "react";

type EnrollButtonPageProps = {
    lectureId: number
}

export default function EnrollButton({lectureId} : Readonly<EnrollButtonPageProps>) {
    const {
        error, success, loading,
        onEnroll
    } = useEnroll();

    useEffect(() => {
        if(!success && error) {
            toast.error(error);
        }else if(success){
            toast.success('수강 신청 성공');
        }
    }, [success, loading]);

    return (
        <div>
            <button
                onClick={() => onEnroll(lectureId)}
                disabled={loading}
            >
                {loading ? '신청중' : '수강신청'}
            </button>
        </div>
    )
}