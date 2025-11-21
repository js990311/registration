"use client"

import {RegistrationLecture} from "@/src/type/registration/registration";
import UnenrollButton from "@/src/components/lectures/button/UnenrollButton";
import styles from './RegistrationLectureList.module.css';
import {Card} from "@/src/components/Card/Card";
import clsx from "clsx";

type RegistrationLectureListProps = {
    lectures: RegistrationLecture[],
    onUnenroll: (registrationId: number) => void,
}

export default function RegistrationLectureList({lectures, onUnenroll} : Readonly<RegistrationLectureListProps>) {
    return (
        <div className={"w-full"}>
            <ul className={clsx(styles.registrationLectureList)}>
                {lectures.map((lecture, index: number) => (
                    <li key={lecture.lectureId}>
                        <Card className={clsx(styles.registrationCard)}>
                            <div>
                                <a href={`/lectures/${lecture.lectureId}`}>
                                    <p className={clsx(styles.lectureName)}>
                                        {lecture.name}
                                    </p>
                                </a>
                                <p>
                                    정원 : {lecture.capacity}
                                </p>
                            </div>
                            <div>
                                <UnenrollButton
                                    registrationId={lecture.registrationId}
                                    onUnenroll={onUnenroll}
                                />
                            </div>
                        </Card>
                    </li>
                ))}
            </ul>
        </div>
    );
}