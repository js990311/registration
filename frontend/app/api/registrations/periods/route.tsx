import {NextRequest, NextResponse} from "next/server";
import {CreatePeriodRequest} from "@/src/type/registration/period/period";
import {ProblemResponse} from "@/src/type/error/error";
import {cookies} from "next/headers";
import {ProxyRequestBuilder} from "@/src/services/api/client";


export async function POST(req: NextRequest){
    const createPeriodRequest: CreatePeriodRequest = await req.json();
    return new ProxyRequestBuilder(`/registrations/periods`)
        .withMethod('POST')
        .withBody(createPeriodRequest)
        .withAuth()
        .execute();
}