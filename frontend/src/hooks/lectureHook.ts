import {useEffect, useState} from "react";
import Lecture from "@/src/type/lecture/lecture";
import {Pagination} from "@/src/type/pagination/pagination";
import {ProblemResponse} from "@/src/type/error/error";
import {useRouter} from "next/navigation";

export const useCreateLecture = () => {
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string>('');
    const [name, setName] = useState('');
    const [credit, setCredit] = useState('');
    const [capacity, setCapacity] = useState('');
    const router = useRouter();

    const createLecture = async (e) => {
        e.preventDefault();
        if(loading){
            return;
        }
        const capacityNumber = parseInt(capacity);
        if(isNaN(capacityNumber)) {
            setError(`capccity must be a number`);
            return;
        }

        try {
            setError('');
            setLoading(true);
            const response = await fetch('/api/lectures', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    name: name,
                    capacity: capacityNumber,
                    credit: credit,
                })
            });

            if(response.status === 201){
                const lecture:Lecture = await response.json();
                router.push(`/lectures/${lecture.lectureId}`);
            }else {
                const problem:ProblemResponse = await response.json();
                setError(problem.detail);
            }
        }catch (error) {
            setError(`FRONTEND ERROR: ${error instanceof Error ? error.message : String(error)}`);
        }finally {
            setLoading(false);
        }
    }

    return{
        error, loading,
        name, setName,
        credit, setCredit,
        capacity, setCapacity,
        createLecture,
    }
}

export const useLectureList = (page: number, size: number) => {
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string>('');
    const [lectures, setLectures] = useState<Lecture[]>([]);
    const [pagination, setPagination] = useState<Pagination<Lecture>>(
        {
            data: [],
            count: 0,
            totalElements: 0,
            pageNumber: page,
            pageSize: size,
            hasNextPage: false,
            totalPage: 1,
        }
    );

    const fetchLectures = async () => {
        if(loading){
            return;
        }

        try {
            setLoading(true);
            setLectures([]);
            setError('');
            setPagination({
                    data: [],
                    count: 0,
                    totalElements: 0,
                    pageNumber: page,
                    pageSize: size,
                    hasNextPage: false,
                    totalPage: 1,
                }
            );
            const response = await fetch(`/api/lectures?page=${page}&size=${size}`);
            setLoading(false);
            if (response.ok) {
                const lectures: Pagination<Lecture> = await response.json();
                setLectures(lectures.data);
                setPagination(lectures);
            }else {
                const problem:ProblemResponse = await response.json();
                setError(`${problem.type} : ${problem.detail}`);
            }
        }catch (error) {
            setError(`FRONTEND ERROR: ${error instanceof Error ? error.message : String(error)}`);
        }finally {
            setLoading(false);
        }
    }

    useEffect(() => {
        fetchLectures();
    }, [page, size]);

    return {
        loading, error, lectures, pagination
    };
}
