import {NextRequest, NextResponse} from "next/server";
import {fetchOne} from "@/src/lib/api/fetchWrapper";
import {Tokens} from "@/src/type/auth/tokens";
import {actionCatch} from "@/src/lib/api/actionUtils";
import {setTokens} from "@/src/lib/api/tokenUtils";
import {ActionOneResponse} from "@/src/type/response/actionResponse";

export async function POST(req: NextRequest):Promise<NextResponse<ActionOneResponse<null>>>{
    try {
        const body = await req.json();

        const response = await fetchOne<Tokens>({
            endpoint: '/login',
            method: "POST",
            body: body,
        });
        await setTokens(response.data);
        return NextResponse.json({success: true, data: null}, {status: 200});
    }catch (error){
        const failResponse = await actionCatch("/api/login", error)
        return NextResponse.json(failResponse, {status: failResponse.error.status|| 500});
    }
}