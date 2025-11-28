import {ProxyRequestBuilder} from "@/src/services/api/client";

export async function GET(){
    return new ProxyRequestBuilder(`/students/me`)
        .withAuth()
        .execute();
}