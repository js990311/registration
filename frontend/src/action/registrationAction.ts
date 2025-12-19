"use server"

import {ActionOneResponse} from "@/src/type/response/actionResponse";
import {CreateRegistrationRequest, CreateRegistrationResponse} from "@/src/type/registration/registration";
import {fetchOne} from "@/src/lib/api/fetchWrapper";
import {actionCatch, actionOneWrapper} from "@/src/lib/api/actionUtils";

export async function registration(request: CreateRegistrationRequest): Promise<ActionOneResponse<CreateRegistrationResponse>>{
    try{
        const response = await fetchOne<CreateRegistrationResponse>({
            endpoint: '/registrations',
            method: 'POST',
            body:request,
            withAuth: true,
        });
        return actionOneWrapper(response);
    }catch(error){
        return await actionCatch("registration()", error);
    }
}