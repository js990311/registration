import {setHeader, toQuery} from "@/src/lib/api/fetchUtils";
import {ApiFailResponse, ApiOneResponse, ApiPageResponse} from "@/src/type/response/apiResponse";
import {businessError, BusinessError, HttpError, httpException, networkException} from "@/src/lib/api/apiError";

const BACKEND_HOST = process.env.BACKEND_HOST ?? 'http://localhost:8080/api';

type FetchParams = {
    endpoint: string;
    method?: "GET" | "POST" | "PUT" | "DELETE";
    params?: Record<string, any>;
    headers?: Record<string, string>;
    body?: unknown;
    withAuth?:boolean;
}


export async function fetchOne<T>(parameter : FetchParams) : Promise<ApiOneResponse<T>>{
    const respones: ApiOneResponse<T> = await fetchWrapper<ApiOneResponse<T>>(parameter)
    return respones;
}

export async function fetchPage<T>(parameter : FetchParams) : Promise<ApiPageResponse<T>>{
    const respones: ApiPageResponse<T> = await fetchWrapper<ApiPageResponse<T>>(parameter)
    return respones;
}

export async function fetchVoid(parameter : FetchParams) : Promise<ApiOneResponse<void>> {
    const respones: ApiOneResponse<void> = await fetchWrapper<ApiOneResponse<void>>(parameter)
    return respones;
}

async function fetchWrapper<T>({endpoint, method='GET', params, headers, withAuth, body} : FetchParams): Promise<T>{
    try {
        const requestHeaders = await setHeader({endpoint, headers, withAuth});
        const queryParams = toQuery(params);
        const url = `${BACKEND_HOST}${endpoint}${queryParams}`;

        const response = await fetch(url, {
            method: method,
            headers: requestHeaders,
            body: (method !== 'GET' && body) ? JSON.stringify(body) : undefined
        });

        if(!response.ok){
            const exceptions:ApiFailResponse = await response.json().catch(() => null);
            if(exceptions){
                // 서버 차원에서 상정한 예외가 발생함
                throw businessError(exceptions.error);
            }
            // 서버와 클라이언트 사이 어딘가에서 예외가 발생함
            throw httpException(endpoint, response.status);
        }

        if(response.status === 204){
            return undefined as T;
        }

        const responseBody: T = await response.json();
        return responseBody;
    }catch (error){
        if(error instanceof BusinessError || error instanceof HttpError){
            throw error;
        }
        throw networkException(endpoint, error);
    }
}