"use client"

import {useEffect, useState} from "react";
import Lecture from "@/src/type/lecture/lecture";
import LectureList from "@/src/components/lectures/LectureList";
import {Pagination} from "@/src/type/pagination/pagination";
import {useLectureList} from "@/src/hooks/lectureHook";
import PaginationController from "@/src/components/pagination/PaginationController";
import {useRouter, useSearchParams} from "next/navigation";
import toast from "react-hot-toast";
import LectureTable from "@/src/components/lectures/LectureTable";

export default function LecturesListPage(){
    const searchParams = useSearchParams();
    const router = useRouter();

    const page = Number(searchParams.get("page")) || 1;
    const size = Number(searchParams.get("size")) || 15;

    const {
        lectures, loading, error, pagination
    } = useLectureList(page, size);

    useEffect(()=>{
        if(error){
            toast.error(error);
        }
    },[error]);

    return (
        <div>
            <LectureTable
                lectures={lectures}
            />
            <PaginationController
                pagination = {pagination}
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