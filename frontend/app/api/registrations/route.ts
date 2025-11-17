import {NextRequest, NextResponse} from "next/server";
import {cookies} from "next/headers";
import {CreateRegistrationRequest} from "@/src/type/registration/registration";
import {ProblemResponse} from "@/src/type/error/error";

export async function POST(req:NextRequest){
    const HOST = process.env.BACKEND_HOST ?? 'http://localhost:8080';

    const cookieStore = await cookies();
    const accessToken = cookieStore.get('access_token');

    if(accessToken === null){
        return NextResponse.json({status: 401});
    }
    console.log(accessToken);

    const createRegistrationRequest: CreateRegistrationRequest = await req.json();

    const apiResponse = await fetch(`${HOST}/registrations`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${accessToken?.value}`,
        },
        body: JSON.stringify(createRegistrationRequest),
        credentials: 'include',
    });

    if(apiResponse.status === 201){
        const data = await apiResponse.json();
        return NextResponse.json({...data}, {status: apiResponse.status});
    }else {
        const problemResponse: ProblemResponse = await apiResponse.json();
        console.log(problemResponse);
        return NextResponse.json({...problemResponse}, {status: apiResponse.status});
    }
}