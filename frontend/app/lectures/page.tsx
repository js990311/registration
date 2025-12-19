"use client"

import {useEffect, useState} from "react";
import PaginationController from "@/src/components/pagination/PaginationController";
import {useRouter, useSearchParams} from "next/navigation";
import LectureTable from "@/src/components/lectures/LectureTable";
import {getLectures} from "@/src/action/lectureAction";
import ErrorDisplay from "@/src/components/exception/exceptionDisplay";
import Lecture from "@/src/type/lecture/lecture";
import {defaultPaginationInfo, PaginationInfo} from "@/src/type/response/pagination";
import {ExceptionDetail} from "@/src/type/error/exceptionDetail";

export default function LecturesListPage(){
    const searchParams = useSearchParams();
    const router = useRouter();
    const page = Number(searchParams.get("page")) || 1;
    const size = Number(searchParams.get("size")) || 15;

    const [lectures, setLectures] = useState<Lecture[]>([]);
    const [pagination, setPagination] = useState<PaginationInfo>();
    const [error, setError] = useState<ExceptionDetail | null>(null);

    useEffect(()=>{
        getLectures(page, size)
            .then((resp) => {
                if(!resp.success){
                    setError(resp.error);
                }else{
                    setError(null);
                    setLectures(resp.data);
                    setPagination(resp.pagination);
                }
            })
    },[page, size]);

    if(error){
        return <ErrorDisplay error={error} />;
    }

    return (
        <div>
            <LectureTable
                lectures={lectures}
            />
            <PaginationController
                pagination = {pagination ?? defaultPaginationInfo(page, size)}
                onHandlePage={(newPage, newSize) => {
                    const params = new URLSearchParams();
                    params.set('page', String(newPage));
                    params.set('size', String(newSize));

                    router.push(`/lectures/list?${params.toString()}`);
                }}
            />
        </div>
    );
}