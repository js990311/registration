import Lecture from "@/src/type/lecture/lecture";
import EnrollButton from "@/src/components/lectures/button/EnrollButton";
import styles from "./LectureTable.module.css";
import clsx from "clsx";

export default function LectureTable({lectures} : Readonly<{ lectures: Lecture[] }>) {
    return (
        <table className={clsx(styles.tableWrapper)}>
            <thead>
                <tr>
                    <th>
                        수강신청
                    </th>
                    <th>
                        강의명
                    </th>
                    <th>
                        정원
                    </th>
                </tr>
            </thead>
            <tbody>
            {lectures.map((lecture: Lecture, index:number) => (
                <tr key={index}>
                    <td>
                        <EnrollButton
                            lectureId={lecture.lectureId}
                        />
                    </td>
                    <td>
                        <a href={`${lecture.lectureId}`}>
                            {lecture.name}
                        </a>
                    </td>
                    <td>
                        {lecture.capacity}
                    </td>
                </tr>
            ))}
            </tbody>
        </table>
    );
}