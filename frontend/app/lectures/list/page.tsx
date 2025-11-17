"use client"

import {useEffect, useState} from "react";
import Lecture from "@/src/type/lecture/lecture";
import LectureList from "@/src/components/lectures/LectureList";
import {Pagination} from "@/src/type/pagination/pagination";

export default function LecturesListPage(){
    const [page, setPage] = useState(1);
    const [size, setSize] = useState(20);
    const [lectures, setLectures] = useState<Lecture[]>([]);

    const fetchLectures = async () => {
        const response = await fetch(`/api/lectures?page=${page}&size=${size}`);
        if (response.status === 200) {
            const lectures: Pagination<Lecture> = await response.json();
            setLectures(lectures.data);
        }
    }

    useEffect(() => {
        fetchLectures();
    }, []);

    return (
        <div>
            <LectureList
                lectures={lectures}
            />
        </div>
    );
}