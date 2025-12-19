import {NextRequest, NextResponse} from "next/server";
import {clearTokens} from "@/src/lib/api/tokenUtils";

export async function POST(req: NextRequest) {
    await clearTokens();
    return new NextResponse(null, {status: 204});
}