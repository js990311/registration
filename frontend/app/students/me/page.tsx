"use client"

import RegistrationLectureList from "@/src/components/lectures/RegistrationLectureList";
import {useEffect, useState} from "react";
import {Pagination} from "@/src/type/pagination/pagination";
import {RegistrationLecture} from "@/src/type/registration/registration";

export default function StudentsMyPage(){
    const [lectures, setLectures] = useState<RegistrationLecture[]>([]);

    const onUnenroll = (registrationId: number) => {
        setLectures(pre => pre.filter(
            lecture => lecture.registrationId !== registrationId
        ));
    }

    const fetchLectures = async () => {
        const response = await fetch(`/api/students/me/registrations`);
        if (response.status === 200) {
            const lectures: Pagination<RegistrationLecture> = await response.json();
            setLectures(lectures.data);
        }
    }

    useEffect(() => {
        fetchLectures();
    }, []);


    return (
        <div>
            <RegistrationLectureList
                lectures={lectures}
                onUnenroll={onUnenroll}
            />
        </div>
    );
}