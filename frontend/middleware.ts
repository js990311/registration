import {NextRequest, NextResponse} from "next/server";
import {ACCESS_TOKEN_KEY} from "@/src/lib/api/tokenUtils";


export async function middleware(req:NextRequest){
    const token = req.cookies.get(ACCESS_TOKEN_KEY);
    const {pathname, origin} = req.nextUrl;
    console.log(`Middleware running for: ${pathname}, Token exists: ${!!token}`);
    if(!token || !token.value){
        const loginUrl = new URL("/login", origin)
        loginUrl.searchParams.set('redirect', pathname);
        return NextResponse.redirect(loginUrl);
    }

    return NextResponse.next();
}

export const config = {
    matcher: [
        '/students/:path*',
        '/students',
        '/lectures/:path*',
        '/lectures',
        '/admin/:path*',
        '/admin'
    ],
}