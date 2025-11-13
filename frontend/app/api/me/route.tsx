import {NextRequest, NextResponse} from "next/server";
import {cookies} from "next/headers";

export async function GET(req : NextRequest){
    const cookieStore = await cookies();

    const access_token = cookieStore.get('access_token');
    if(access_token){
        return NextResponse.json({"success" : true}, {status: 200});
    }else {
        return NextResponse.json({"success" : false}, {status: 404});
    }
}