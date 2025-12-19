import {NextRequest, NextResponse} from "next/server";
import {ProblemResponse} from "@/src/type/error/error";
import {Tokens} from "@/src/type/auth/tokens";
import {cookies} from "next/headers";
import {setTokens} from "@/src/lib/api/tokenUtils";

export async function POST(req: NextRequest){
    const HOST = process.env.BACKEND_HOST || 'http://localhost:8080';
    const cookieStore = await cookies();

    try {
        const body = await req.json();
        const apiResponse = await fetch(`${HOST}/signup`, {
            method: "POST",
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(body),
        });

        if(apiResponse.status !== 201){
            const problemResponse: ProblemResponse = await apiResponse.json();
            return NextResponse.json({success: false, 'problem' : problemResponse}, {status: apiResponse.status});
        }

        const tokens : Tokens = await apiResponse.json();
        setTokens(tokens);
        return NextResponse.json({success: true}, {status: 201});
    }catch (error){
        return NextResponse.json({success: false}, {status: 500});
    }
}