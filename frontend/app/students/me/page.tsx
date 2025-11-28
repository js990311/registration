"use client"

import RegistrationLectureList from "@/src/components/lectures/RegistrationLectureList";
import {useEffect, useRef, useState} from "react";
import {Pagination} from "@/src/type/pagination/pagination";
import {RegistrationLecture} from "@/src/type/registration/registration";
import {Student} from "@/src/type/student/student";
import styles from './StudentMe.module.css';
import clsx from "clsx";

export default function StudentsMyPage(){
    const [lectures, setLectures] = useState<RegistrationLecture[]>([]);
    const [student, setStudent] = useState<Student>();
    const credits = useRef<number>(0);
    const remainingCredits = useRef<number>(0);

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

    const fetchStudent = async () => {
        const response = await fetch(`/api/students/me`);
        if (response.status === 200) {
            const student: Student = await response.json();
            setStudent(student);
        }
    }


    useEffect(() => {
        fetchLectures();
        fetchStudent();
    }, []);

    useEffect(() => {
        credits.current = lectures.reduce((sum,lecture) => sum + (lecture.credit ?? 0),0);
        if (student?.creditLimit){
            remainingCredits.current = student.creditLimit - credits.current;
        }
    }, [lectures, student]);

    return (
        <div>
            <div className={styles.creditCard}>
                <div className={styles.cardTitle}>
                    수강신청 학점 근황
                </div>
                <div className={styles.creditLine}>
                    <span className={styles.label}>현재 수강한 학점 : </span>
                    <span className={styles.value}>{credits.current}</span>
                </div>
                <div className={styles.creditLine}>
                    <span className={styles.label}>최대 수강신청학점 : </span>
                    <span className={styles.value}>{student?.creditLimit}</span>
                </div>
                <div className={styles.creditLine}>
                    <span className={clsx(styles.label, styles.remainingCredit)}>남은 학점 : </span>
                    <span className={clsx(styles.value, {
                        [styles.statusPositive]: remainingCredits.current !== 0,
                        [styles.statusNegative]: remainingCredits.current === 0
                    })}>{remainingCredits.current}</span>
                </div>

            </div>
            <RegistrationLectureList
                lectures={lectures}
                onUnenroll={onUnenroll}
            />
        </div>
    );
}