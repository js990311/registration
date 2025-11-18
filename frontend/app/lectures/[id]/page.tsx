import Lecture from "@/src/type/lecture/lecture";

const getLectureById = async (lectureId:string) => {
    const HOST = process.env.BACKEND_HOST ?? 'http://localhost:8080';

    const response = await fetch(`${HOST}/lectures/${lectureId}`, {
        method: "GET",
        credentials: "include",
    });
    return response.json();
}

type PageProps = {
    params : Promise<{id: string}>
}

export default async function lectureIdPage({params} : PageProps){
    const {id} = await params;
    const lecture: Lecture = await getLectureById(id);

    if(!lecture){
        return (
            <div>
                강의가 없습니다.
            </div>
        );
    }

    console.log(lecture);

    return (
        <div>
            <h1>
                강의 상세 페이지
            </h1>
            <p>
                강의명 : {lecture.name}
            </p>
            <p>
                정원 : {lecture.capacity}
            </p>
        </div>
    );
}