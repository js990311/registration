type Lecture = {
    lectureId: number;
    name: string;
    capacity: number;
    studentCount: number;
    credit: number;
}

export type CreateLectureRequest = {
    name: string;
    capacity: number;
    credit: number;
}

export default Lecture;