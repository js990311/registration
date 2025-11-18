import {cookies} from "next/headers";
import {Tokens} from "@/src/type/auth/tokens";

export async function getAccessToken() : Promise<string | null>{
    const cookieStore = await cookies();
    const accessToken = cookieStore.get('access_token');
    return accessToken?.value ?? null;
}

export async function getRefreshToken(): Promise<string | null>{
    const cookieStore = await cookies();
    const refreshToken = cookieStore.get('refresh_token');
    return refreshToken?.value ?? null;
}

export async function setTokens(tokens: Tokens) {
    const cookieStore = await cookies();
    cookieStore.set('access_token', tokens.accessToken, {
        httpOnly: true
    });
    cookieStore.set('refresh_token', tokens.refreshToken, {
        httpOnly: true
    });
}

export async function clearTokens(): Promise<void> {
    const cookieStore = await cookies();
    cookieStore.set('access_token', '', {
        httpOnly: true,
        maxAge: 0
    });

    cookieStore.set('refresh_token', '', {
        httpOnly: true,
        maxAge: 0
    });
}