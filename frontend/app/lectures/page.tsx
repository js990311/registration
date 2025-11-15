import Link from "next/link";

export default function createLecturesPage(){

    return (
        <div>
            <Link href={"lectures/create"}>
                강의 생성
            </Link>
            <Link href={"lectures/list"}>
                강의 목록 보기
            </Link>
        </div>
    );
}