"use client"

import {RegistrationLecture} from "@/src/type/registration/registration";
import UnenrollButton from "@/src/components/lectures/UnenrollButton";

type RegistrationLectureListProps = {
    lectures: RegistrationLecture[],
    onUnenroll: (registrationId: number) => void,
}

export default function RegistrationLectureList({lectures, onUnenroll} : Readonly<{ RegistrationLectureListProps }>) {
    return (
        <ul>
            {lectures.map((lecture, index) => (
                <li key={lecture.lectureId}>
                    <UnenrollButton
                        registrationId={lecture.registrationId}
                        onUnenroll={onUnenroll}
                    />
                    <a href={`/lectures/${lecture.lectureId}`}>
                        <p>
                            {lecture.name}
                        </p>
                        <p>
                            {lecture.capacity}
                        </p>
                    </a>
                </li>
            ))}
        </ul>
    );
}