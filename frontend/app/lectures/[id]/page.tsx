import Lecture from "@/src/type/lecture/lecture";
import {fetchOne} from "@/src/lib/api/fetchWrapper";
import {actionCatch, actionOneWrapper} from "@/src/lib/api/actionUtils";
import {ActionOneResponse} from "@/src/type/response/actionResponse";
import ErrorDisplay from "@/src/components/exception/exceptionDisplay";
import Link from "next/link";

const getLectureById = async (lectureId:string) => {
    try {
        const response = await fetchOne<Lecture>({
            method: "GET",
            endpoint: `/lectures/${lectureId}`,
            withAuth: true,
        });
        return actionOneWrapper(response);
    }catch (error){
        return actionCatch(`/lectures/${lectureId}`, error);
    }

}

type PageProps = {
    params : Promise<{id: string}>
}

export default async function lectureIdPage({params} : PageProps){
    const {id} = await params;
    const response:ActionOneResponse<Lecture> = await getLectureById(id);

    if(!response.success){
        return (
            <ErrorDisplay
                exception={response.error}
            />
        );
    }

    const lecture = response.data;
    const isFull = lecture.capacity === lecture.studentCount;

    return (
        <div className="max-w-3xl mx-auto py-12 px-4">
            {/* 상단 헤더 및 뒤로가기 */}
            <div className="flex justify-between items-end mb-8 border-b-2 border-gray-900 pb-4">
                <h1 className="text-3xl font-bold text-gray-900">강의 상세 정보</h1>
                <Link href="/lectures" className="text-sm text-blue-600 hover:underline">
                    ← 전체 목록으로
                </Link>
            </div>

            {/* 메인 정보 카드 */}
            <div className="bg-white rounded-xl shadow-lg border border-gray-100 overflow-hidden">
                <div className="p-8">
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
                        {/* 강의명 (Full Width) */}
                        <div className="md:col-span-2 space-y-2">
                            <label className="text-xs font-semibold uppercase tracking-wider text-gray-400">강의명</label>
                            <p className="text-2xl font-extrabold text-gray-800">{lecture.name}</p>
                        </div>

                        {/* 학점 */}
                        <div className="space-y-2">
                            <label className="text-xs font-semibold uppercase tracking-wider text-gray-400">이수
                                학점</label>
                            <p className="text-lg font-semibold text-gray-700">{lecture.credit} 학점</p>
                        </div>

                        {/* 수강 현황 */}
                        <div className="space-y-2">
                            <label className="text-xs font-semibold uppercase tracking-wider text-gray-400">수강
                                현황</label>
                            <div className="flex items-center gap-2">
                                <span className={`text-lg font-bold ${isFull ? "text-red-500" : "text-blue-600"}`}>
                                    {lecture.studentCount} / {lecture.capacity}
                                </span>
                                {isFull && (
                                    <span
                                        className="bg-red-100 text-red-600 text-[10px] px-2 py-0.5 rounded-full font-bold">
                                        정원 초과
                                    </span>
                                )}
                            </div>
                        </div>

                        {/* 강의 코드 */}
                        <div className="space-y-2">
                            <label className="text-xs font-semibold uppercase tracking-wider text-gray-400">강의 고유
                                코드</label>
                            <p className="text-lg font-mono text-gray-600">LCT-{lecture.lectureId.toString().padStart(4, '0')}</p>
                        </div>
                    </div>
                </div>

                {/* 하단 액션 바 (선택 사항) */}
                <div className="bg-gray-50 px-8 py-4 border-t border-gray-100 flex justify-end">
                    <p className="text-sm text-gray-500">
                        본 강의 정보는 실시간 데이터를 바탕으로 제공됩니다.
                    </p>
                </div>
            </div>
        </div>);
}