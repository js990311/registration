import {NextRequest, NextResponse} from "next/server";
import {clearTokens} from "@/src/lib/api/tokenUtils";
import {actionCatch} from "@/src/lib/api/actionUtils";

export async function POST(req: NextRequest) {
    try {
        await clearTokens();
        return new NextResponse(null, {status: 204});
    }catch (error) {
        const failResponse = await actionCatch("/api/logout", error)
        return NextResponse.json(failResponse, {status: failResponse.error.status|| 500});
    }
}