"use server"

import {ActionOneResponse, ActionPageResponse} from "@/src/type/response/actionResponse";
import Lecture, {CreateLectureRequest} from "@/src/type/lecture/lecture";
import {actionCatch, actionOneWrapper, actionPageWrapper} from "@/src/lib/api/actionUtils";
import {fetchOne, fetchPage} from "@/src/lib/api/fetchWrapper";

export async function getLectures(page: number, size: number): Promise<ActionPageResponse<Lecture>> {
    try {
        const response = await fetchPage<Lecture>({
            endpoint: '/lectures',
            method: "GET",
            params: {
                page: page,
                size: size,
            }
        });
        return actionPageWrapper(response);
    }catch (error) {
        return actionCatch('getLectures()', error);
    }
}

export async function createLecture(request: CreateLectureRequest): Promise<ActionOneResponse<Lecture>>{
    try {
        const response = await fetchOne<Lecture>({
            endpoint: '/lectures',
            method: "POST",
            body: request,
            withAuth: true
        });
        return actionOneWrapper(response);
    }catch (error) {
        return actionCatch('createLecture()', error);
    }
}