import {NextRequest, NextResponse} from "next/server";
import * as sea from "node:sea";
import {pageParams, Pagination} from "@/src/type/pagination/pagination";
import {ProblemResponse} from "@/src/type/error/error";
import Lecture from "@/src/type/lecture/lecture";

export async function GET(req: NextRequest){
    const HOST = process.env.BACKEND_HOST ?? 'http://localhost:8080';

    const searchParams = req.nextUrl.searchParams;
    const page = searchParams.get("page") ?? '1';
    const size = searchParams.get("size") ?? '20';

    const parmas = {
        page: page,
        size: size
    }
    const pageParams = new URLSearchParams(parmas).toString();

    const apiResponse = await fetch(`${HOST}/lectures?${pageParams}`, {
        method: "GET",
        headers: {
            "Content-Type": "application/json",
        },
        credentials: "include",
    });

    if(apiResponse.status === 200){
        const lectures: Pagination<Lecture> = await apiResponse.json();
        return NextResponse.json({...lectures});
    }else {
        const problemResponse: ProblemResponse = await apiResponse.json();
        console.log(problemResponse);
        return NextResponse.json({...problemResponse}, {status: apiResponse.status});
    }
}

export async function POST(req: NextRequest){
    const HOST = process.env.BACKEND_HOST ?? 'http://localhost:8080';

    const lectureCreateRequest = await req.json();

    const apiResponse = await fetch(`${HOST}/lectures`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(lectureCreateRequest),
        credentials: "include",
    });

    console.log(apiResponse.status);

    if(apiResponse.status === 201){
        const data = await apiResponse.json();
        return NextResponse.json({...data}, {status: apiResponse.status});
    }else {
        const problemResponse: ProblemResponse = await apiResponse.json();
        return NextResponse.json({...problemResponse}, {status: apiResponse.status});
    }
}