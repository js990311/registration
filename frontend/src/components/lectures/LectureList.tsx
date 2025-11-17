"use client"

import Lecture from "@/src/type/lecture/lecture";
import EnrollButton from "@/src/components/lectures/EnrollButton";

export default function LectureList({lectures} : Readonly<{ lectures: Lecture[] }>) {
    return (
        <ul>
            {lectures.map((lecture, index) => (
                <li key={lecture.lectureId}>
                    <EnrollButton
                        lectureId={lecture.lectureId}
                    />
                    <a href={`${lecture.lectureId}`}>
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