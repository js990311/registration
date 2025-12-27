import { NextResponse} from "next/server";
import {withAuth} from "next-auth/middleware";


export default withAuth(
    function middleware(req) {
        const { pathname } = req.nextUrl;
        console.log(`[Middleware] 접근 허용: ${pathname}`);
        return NextResponse.next();
    },
    {
        callbacks: {
            // 여기서 true를 반환하면 접근 허용, false면 로그인 페이지로 리다이렉트됩니다.
            authorized: ({ token }) => !!token,
        },
        pages: {
            // 로그인이 안 되었을 때 보낼 커스텀 페이지 주소
            signIn: "/login",
        },
    }
);

export const config = {
    matcher: [
        "/students/:path*",
        "/students",
        "/lectures/:path*",
        "/lectures",
        "/admin/:path*",
        "/admin",
    ],
};