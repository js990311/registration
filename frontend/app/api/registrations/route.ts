import {NextRequest, NextResponse} from "next/server";
import {cookies} from "next/headers";
import {CreateRegistrationRequest} from "@/src/type/registration/registration";
import {ProblemResponse} from "@/src/type/error/error";
import {ProxyRequestBuilder} from "@/src/services/api/client";

export async function POST(req:NextRequest){
    const createRegistrationRequest: CreateRegistrationRequest = await req.json();
    return new ProxyRequestBuilder(`/registrations`)
        .withMethod('POST')
        .withBody(createRegistrationRequest)
        .withAuth()
        .execute()
    ;
}