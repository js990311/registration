import NextAuth, { NextAuthOptions } from "next-auth"
import CredentialsProvider from "next-auth/providers/credentials"

export const authOptions: NextAuthOptions = {
    // 1. 세션 전략을 JWT로 설정 (Spring Security와 연동 시 필수)
    session: {
        strategy: "jwt",
    },
    providers: [
        CredentialsProvider({
            name: "Credentials",
            credentials: {
                username: { label: "Username", type: "text" },
                password: { label: "Password", type: "password" }
            },
            // 2. 로그인 실행: 사용자가 ID/PW를 입력하고 로그인 버튼을 누를 때 실행됨
            async authorize(credentials) {
                try {
                    const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/login`, {
                        method: 'POST',
                        body: JSON.stringify(credentials),
                        headers: { "Content-Type": "application/json" }
                    });

                    if (!res.ok) return null;

                    // 전체 응답 객체 ( { data: { ... } } )
                    const response = await res.json();

                    // 실제 알맹이 데이터 ( { tokens: { ... }, username: "..." } )
                    const actualData = response.data;

                    if (actualData && actualData.tokens) {
                        return {
                            id: actualData.username,
                            name: actualData.username,
                            accessToken: actualData.tokens.accessToken,
                            accessTokenExpiresAt: actualData.tokens.accessTokenExpiresAt
                        };
                    }

                    console.error("응답 데이터 형식이 올바르지 않습니다:", response);
                    return null;
                } catch (e) {
                    console.error("로그인 중 서버 에러:", e);
                    return null;
                }
            }
        })
    ],
    callbacks: {
        // 3. JWT 생성 시 호출: authorize에서 반환한 유저 정보를 토큰에 담음
        async jwt({ token, user }) {
            if (user) {
                token.accessToken = user.accessToken
                token.accessTokenExpiresAt = user.accessTokenExpiresAt
                token.username = user.name
            }
            return token
        },

        // 4. 세션 조회 시 호출: 토큰의 정보를 클라이언트(useSession)에서 쓸 수 있게 공개
        async session({ session, token }) {
            session.accessToken = token.accessToken as string
            if (session.user) {
                session.user.name = token.username as string
            }
            return session
        }
    },
    // 보안을 위해 설정한 Secret 값 적용
    secret: process.env.NEXTAUTH_SECRET,
    pages: {
        signIn: '/login', // 커스텀 로그인 페이지를 쓸 경우 경로 지정
    }
}

const handler = NextAuth(authOptions)
export { handler as GET, handler as POST }
