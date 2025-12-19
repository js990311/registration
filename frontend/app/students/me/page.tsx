"use client"

import RegistrationLectureList from "@/src/components/lectures/RegistrationLectureList";
import {useEffect, useMemo, useState} from "react";
import {RegistrationLecture} from "@/src/type/registration/registration";
import {Student} from "@/src/type/student/student";
import styles from './StudentMe.module.css';
import clsx from "clsx";
import toast from "react-hot-toast";
import {errorToString} from "@/src/lib/api/apiError";
import {studentMeAction, studentMeRegistrationAction} from "@/src/action/myPageAction";

export default function StudentsMyPage(){
    const [lectures, setLectures] = useState<RegistrationLecture[]>([]);
    const [student, setStudent] = useState<Student>();

    const onUnenroll = (registrationId: number) => {
        setLectures(pre => pre.filter(
            lecture => lecture.registrationId !== registrationId
        ));
    }

    const fetchLectures = async () => {
        studentMeRegistrationAction()
            .then(resp => {
                if(resp.success){
                    setLectures(resp.data);
                }else {
                    toast.error(errorToString("", resp.error));
                }
            })
    }

    const fetchStudent = async () => {
        studentMeAction().then(
            resp => {
                if(resp.success){
                    setStudent(resp.data);
                }else {
                    toast.error(errorToString("", resp.error));
                }
            }
        )
    }

    useEffect(() => {
        fetchLectures();
        fetchStudent();
    }, []);

    const credits = useMemo(()=>{
        return lectures.reduce((sum,lecture) => sum + (lecture.credit ?? 0),0);;
    }, [lectures]);
    const remainingCredits = useMemo(() => {
        if (student?.creditLimit){
            return student.creditLimit - credits;
        }return 0;
    }, [student, credits]);

    return (
        <div>
            <div className={styles.creditCard}>
                <div className={styles.cardTitle}>
                    수강신청 학점 근황
                </div>
                <div className={styles.creditLine}>
                    <span className={styles.label}>현재 수강한 학점 : </span>
                    <span className={styles.value}>{credits}</span>
                </div>
                <div className={styles.creditLine}>
                    <span className={styles.label}>최대 수강신청학점 : </span>
                    <span className={styles.value}>{student?.creditLimit}</span>
                </div>
                <div className={styles.creditLine}>
                    <span className={clsx(styles.label, styles.remainingCredit)}>남은 학점 : </span>
                    <span className={clsx(styles.value, {
                        [styles.statusPositive]: remainingCredits !== 0,
                        [styles.statusNegative]: remainingCredits === 0
                    })}>{remainingCredits}</span>
                </div>

            </div>
            <RegistrationLectureList
                lectures={lectures}
                onUnenroll={onUnenroll}
            />
        </div>
    );
}