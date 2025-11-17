import {cookies} from "next/headers";
import {NextResponse} from "next/server";
import {ProblemResponse} from "@/src/type/error/error";

export async function GET(){
    const HOST = process.env.BACKEND_HOST ?? 'http://localhost:8080';

    const cookieStore = await cookies();
    const accessToken = cookieStore.get('access_token');

    if(accessToken === null){
        return NextResponse.json({status: 401});
    }

    const apiResponse = await fetch(`${HOST}/students/me/registrations`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${accessToken?.value}`,
        },
    });

    if(apiResponse.status === 200){
        const data = await apiResponse.json();
        return NextResponse.json({...data}, {status: apiResponse.status});
    }else {
        const problemResponse: ProblemResponse = await apiResponse.json();
        console.log(problemResponse);
        return NextResponse.json({...problemResponse}, {status: apiResponse.status});
    }

}