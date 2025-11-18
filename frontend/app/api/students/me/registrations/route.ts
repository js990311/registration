import {cookies} from "next/headers";
import {NextResponse} from "next/server";
import {ProblemResponse} from "@/src/type/error/error";
import {ProxyRequestBuilder} from "@/src/services/api/client";

export async function GET(){
    return new ProxyRequestBuilder(`/students/me/registrations`)
        .withAuth()
        .execute();
}