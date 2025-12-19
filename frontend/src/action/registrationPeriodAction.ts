"use server"

import {ActionOneResponse} from "@/src/type/response/actionResponse";
import {fetchOne} from "@/src/lib/api/fetchWrapper";
import {actionCatch, actionOneWrapper} from "@/src/lib/api/actionUtils";
import {CreatePeriodRequest, RegistrationPreiod} from "@/src/type/registration/period/period";

export async function createRegistrationPeriods(request: CreatePeriodRequest): Promise<ActionOneResponse<RegistrationPreiod>>{
    try {
        const response = await fetchOne<RegistrationPreiod>({
            endpoint: '/registrations/periods',
            method: "POST",
            body: request,
            withAuth: true
        });
        return actionOneWrapper(response);
    }catch (error) {
        return actionCatch('createLecture()', error);
    }
}