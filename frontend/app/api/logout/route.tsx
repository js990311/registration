import {NextRequest, NextResponse} from "next/server";
import {cookies} from "next/headers";

export async function POST(req: NextRequest) {
    const cookieStore = await cookies();

    cookieStore.set('access_token', '', {
        httpOnly: true,
        maxAge: 0
    });

    cookieStore.set('refresh_token', '', {
        httpOnly: true,
        maxAge: 0
    });

    return new NextResponse(null, {status: 204});
}