import {NextResponse} from "next/server";
import {cookies} from "next/headers";
import {ProblemResponse} from "@/src/type/error/error";

type HttpMethod = 'GET' | 'POST' | 'PUT' | 'DELETE';

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
            const response = await fetch(
                `${HOST}${this.endpoint}`,
                {
                    method: this.method,
                    headers: headers,
                    body: this.body ? JSON.stringify(this.body) : undefined,
                }
            );

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
}

export async function getAccessToken() : Promise<string | null>{
    const cookieStore = await cookies();
    const accessToken = cookieStore.get('access_token');
    return accessToken?.value || null;
}