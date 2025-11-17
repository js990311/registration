import {NextRequest, NextResponse} from "next/server";
import {CreatePeriodRequest} from "@/src/type/registration/period/period";
import {ProblemResponse} from "@/src/type/error/error";
import {cookies} from "next/headers";


export async function POST(req: NextRequest){
    const HOST = process.env.BACKEND_HOST ?? 'http://localhost:8080';

    const cookieStore = await cookies();
    const accessToken = cookieStore.get('access_token');

    if(accessToken === null){
        return NextResponse.json({status: 401});
    }

    const createPeriodRequest: CreatePeriodRequest = await req.json();
    console.log(createPeriodRequest);

    const apiResponse = await fetch(`${HOST}/registrations/periods`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${accessToken}`,
        },
        body: JSON.stringify(createPeriodRequest),
        credentials: 'include',
    });

    if(apiResponse.status === 201){
        const data = await apiResponse.json();
        return NextResponse.json({...data}, {status: apiResponse.status});
    }else {
        const problemResponse: ProblemResponse = await apiResponse.json();
        return NextResponse.json({...problemResponse}, {status: apiResponse.status});
    }
}