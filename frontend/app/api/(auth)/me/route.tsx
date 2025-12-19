import {NextRequest, NextResponse} from "next/server";
import {cookies} from "next/headers";
import {getAccessToken} from "@/src/lib/api/tokenUtils";

export async function GET(req : NextRequest){
    const access_token = await getAccessToken();
    if(access_token){
        return NextResponse.json({"success" : true}, {status: 200});
    }else {
        return NextResponse.json({"success" : false}, {status: 404});
    }
}