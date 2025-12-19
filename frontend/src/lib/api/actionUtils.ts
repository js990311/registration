import {BaseApiError, unexpectedException} from "@/src/lib/api/apiError";
import {ApiOneResponse, ApiPageResponse} from "@/src/type/response/apiResponse";
import {ActionErrorResponse, ActionOneResponse, ActionPageResponse} from "@/src/type/response/actionResponse";

export async function actionCatch(instance:string, error: unknown): Promise<ActionErrorResponse>{
    if(error instanceof BaseApiError){
        console.error(`[Action Error in ${error.details.instance}] ${error.details.title} : ${error.details.detail}`);
        return {
            success: false,
            error: error.details
        }
    }else {
        const exception = unexpectedException(instance, error);
        console.error(`[Unexpected Error in ${exception.details.instance}] ${exception.details.title} : `, error);
        return {
            success: false,
            error: exception.details
        }
    }
}

export async function actionOneWrapper<T>(response: ApiOneResponse<T>): Promise<ActionOneResponse<T>>{
    return {
        success: true,
        data: response.data
    };
}

export async function actionPageWrapper<T>(response: ApiPageResponse<T>): Promise<ActionPageResponse<T>>{
    return {
        success: true,
        data: response.data,
        pagination: response.pagination
    };
}

export async function actionVoidWrapper(response: ApiOneResponse<void>): Promise<ActionOneResponse<void>> {
    return {success: true, data: response.data};
}