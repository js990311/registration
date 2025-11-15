import {NextRequest, NextResponse} from "next/server";
import {ProblemResponse} from "@/src/type/error/error";

export async function GET(req: NextRequest, {params} : {params: {id: string}}) {
    const HOST = process.env.BACKEND_HOST ?? 'http://localhost:8080';

    const {id} = params;

    const apiResponse = await fetch(`${HOST}/lectures?id=${id}`, {
        method: "GET",
        credentials: "include",
    });

    if(apiResponse.ok){
        const lecture = await apiResponse.json();
        return NextResponse.json(lecture);
    }else {
        const problemResponse: ProblemResponse = await apiResponse.json();
        console.log(problemResponse);
        return NextResponse.json({...problemResponse}, {status: apiResponse.status});
    }
}