import {NextRequest, NextResponse} from "next/server";
import {cookies} from "next/headers";
import {ProblemResponse} from "@/src/type/error/error";

export async function DELETE(req:NextRequest, {params} : {params: {id: string}}){
    const HOST = process.env.BACKEND_HOST ?? 'http://localhost:8080';

    const {id} = await params;
    console.log(`${HOST}/registrations/${id}`);
    const cookieStore = await cookies();
    const accessToken = cookieStore.get('access_token');

    if(accessToken === null){
        return NextResponse.json({status: 401});
    }

    const apiResponse = await fetch(`${HOST}/registrations/${id}`, {
        method: "DELETE",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${accessToken?.value}`
        }
    });

    if(apiResponse.status == 204){
        return new Response(null, {status: 204});
    }else {
        const problemResponse: ProblemResponse = await apiResponse.json();
        console.log(problemResponse);
        return NextResponse.json({...problemResponse}, {status: apiResponse.status});
    }

}