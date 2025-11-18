import {NextResponse} from "next/server";
import {ProblemResponse} from "@/src/type/error/error";
import {Tokens} from "@/src/type/auth/tokens";
import {getAccessToken, getRefreshToken, setTokens} from "@/src/utils/tokenUtils";

type HttpMethod = 'GET' | 'POST' | 'PUT' | 'DELETE';

class RefreshFailError extends Error {
};

export class ProxyRequestBuilder{
    private endpoint: string;
    private method: HttpMethod = 'GET';
    private body?: any;
    private auth?: boolean = false;

    constructor(endpoint: string) {
        this.endpoint = endpoint;
    }

    withMethod(method: HttpMethod): this {
        this.method = method;
        return this;
    }

    withBody(body: any): this{
        this.body = body;
        return this;
    }

    withAuth(): this{
        this.auth = true;
        return this;
    }

    async execute() : Promise<NextResponse> {
        try{
            // host 값 가져옴
            const HOST = process.env.BACKEND_HOST ?? 'http://localhost:8080';
            const url = `${HOST}${this.endpoint}`;

            // 헤더 설정
            const headers: Record<string, string>= {
                'Content-Type': 'application/json',
            }

            // 보안 헤더 설정
            if(this.auth){
                const accessToken = await getAccessToken();
                if(accessToken === null){
                    return new NextResponse(null,{status: 401});
                }
                headers['Authorization'] = `Bearer ${accessToken}`;
            }

            // 응답 받기
            let response = await this.fetch(url, headers);

            // 토큰실패시 리프레시
            if(this.auth && response.status === 401){
                try {
                    const newAccessToken = await this.refresh();
                    headers['Authorization'] = `Bearer ${newAccessToken}`;
                    response = await this.fetch(url, headers);
                }catch (error){
                    if(error instanceof RefreshFailError){
                        return new NextResponse(await response.json(), {status: 401});
                    }else{
                        throw error;
                    }
                }
            }

            if(response.status < 400){ // 정상 응답
                if(response.status === 204){
                    return new NextResponse(null, {status: 204});
                }
                const responseData = await response.json();
                return NextResponse.json(responseData, {
                    status: response.status
                });
            }else { // 비정상응답
                const problemResponse: ProblemResponse = await response.json();
                console.log(`${this.endpoint} (${response.status}) : ${problemResponse.type}, ${problemResponse.detail}`);
                return NextResponse.json({...problemResponse}, {status: response.status});
            }
        }catch (error) { // 서버 예외 발생
            console.log(`${this.endpoint} : ${error}`);
            return new NextResponse(null,{status: 500});
        }
    };

    async fetch(endpoint: string, headers: Record<string, string>){
        return fetch(
            endpoint,
            {
                method: this.method,
                headers: headers,
                body: this.body ? JSON.stringify(this.body) : undefined,
            }
        );
    }

    async refresh(){
        const HOST = process.env.BACKEND_HOST ?? 'http://localhost:8080';
        const refreshToken = await getRefreshToken();
        if(refreshToken === null){
            throw new RefreshFailError();
        }

        const headers: Record<string, string>= {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${refreshToken}`
        }

        const response = await fetch(
            `${HOST}/refresh`,{
                headers: headers,
            }
        );
        if(response.status < 400){
            const tokens: Tokens = await response.json();
            await setTokens(tokens);
            return tokens.accessToken;
        }else {
            throw new RefreshFailError();
        }
    }
}

