"use server"

import {ApiOneResponse, ApiPageResponse} from "@/src/type/response/apiResponse";
import {Student} from "@/src/type/student/student";
import {fetchOne, fetchPage} from "@/src/lib/api/fetchWrapper";
import {actionCatch, actionOneWrapper, actionPageWrapper} from "@/src/lib/api/actionUtils";
import {RegistrationLecture} from "@/src/type/registration/registration";

export async function studentMeAction() {
    try {
        const response: ApiOneResponse<Student> = await fetchOne<Student>({
            endpoint: "/students/me",
            method: "GET",
            withAuth: true,
        });
        return actionOneWrapper(response);
    }catch (error){
        const exception = await actionCatch('/students/me', error);
        return exception;
    }
}

export async function studentMeRegistrationAction() {
    try {
        const response: ApiPageResponse<RegistrationLecture> = await fetchPage<RegistrationLecture>({
            endpoint: "/students/me/registrations",
            method: "GET",
            withAuth: true,
        });
        return actionPageWrapper(response);
    }catch (error){
        const exception = await actionCatch('/students/me', error);
        return exception;
    }
}
