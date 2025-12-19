import {ExceptionDetail} from "@/src/type/error/exceptionDetail";
import {PaginationInfo} from "@/src/type/response/pagination";

export type ActionOneResponse<T> = {
    success: true;
    data: T
} | ActionErrorResponse;

export type ActionPageResponse<T> = {
    success: true;
    data:T[];
    pagination:PaginationInfo;
} | ActionErrorResponse;

export type ActionErrorResponse = {
    success: false,
    error: ExceptionDetail;
}