import {NextRequest, NextResponse} from "next/server";
import {clearTokens} from "@/src/utils/tokenUtils";

export async function POST(req: NextRequest) {
    await clearTokens();
    return new NextResponse(null, {status: 204});
}