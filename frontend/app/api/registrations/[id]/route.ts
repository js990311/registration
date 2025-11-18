import {NextRequest, NextResponse} from "next/server";
import {cookies} from "next/headers";
import {ProblemResponse} from "@/src/type/error/error";
import {ProxyRequestBuilder} from "@/src/services/api/client";

export async function DELETE(req:NextRequest, {params} : {params: {id: string}}){
    const {id} = await params;

    return new ProxyRequestBuilder(`/registrations/${id}`)
        .withMethod('DELETE')
        .withAuth()
        .execute();
}