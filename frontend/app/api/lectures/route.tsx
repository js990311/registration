import {NextRequest, NextResponse} from "next/server";
import * as sea from "node:sea";
import {pageParams, Pagination} from "@/src/type/pagination/pagination";
import {ProblemResponse} from "@/src/type/error/error";
import Lecture from "@/src/type/lecture/lecture";
import {ProxyRequestBuilder} from "@/src/services/api/client";

export async function GET(req: NextRequest){
    const searchParams = req.nextUrl.searchParams;
    const page = searchParams.get("page") ?? '1';
    const size = searchParams.get("size") ?? '20';
    const parmas = {
        page: page,
        size: size
    }
    const pageParams = new URLSearchParams(parmas).toString();
    return new ProxyRequestBuilder(`/lectures?${pageParams}`)
        .withMethod('GET')
        .withAuth()
        .execute()
    ;
}

export async function POST(req: NextRequest){
    const lectureCreateRequest = await req.json();
    return new ProxyRequestBuilder(`/lectures`)
        .withMethod('POST')
        .withAuth()
        .withBody(lectureCreateRequest)
        .execute()
    ;
}