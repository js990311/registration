import {httpException} from "@/src/lib/api/apiError";
import {getServerSession} from "next-auth";
import {authOptions} from "@/app/api/auth/[...nextauth]/route";

export function toQuery(params?: Record<string, any>): string {
    if(!params) {
        return '';
    }
    const usp = new URLSearchParams(params);
    for(const [key, value] of Object.entries(params)) {
        usp.append(key, value);
    }
    const s = usp.toString();
    return s ? `?${s}` : '';
}

type SetHeaderParams = {
    endpoint: string;
    headers?: Record<string, string>;
    withAuth?:boolean;
}

export async function setHeader({endpoint, headers, withAuth} : SetHeaderParams) : Promise<Record<string, string>>{
    const requestHeaders : Record<string, string>= {
        'Content-Type': 'application/json',
        ...headers,
    };

    if (withAuth) {
        const session = await getServerSession(authOptions);
        if (!session || !session.accessToken) {
            throw httpException(endpoint, 401);
        }
        requestHeaders['Authorization'] = `Bearer ${session.accessToken}`;
    }

    return requestHeaders;
}
