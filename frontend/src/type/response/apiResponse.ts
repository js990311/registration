import {ExceptionDetail} from "@/src/type/error/exceptionDetail";
import {PaginationInfo} from "@/src/type/response/pagination";

export interface ApiOneResponse<T>{
    data: T;
}

export interface ApiPageResponse<T>{
    data: T[];
    pagination:PaginationInfo;
}

export interface ApiFailResponse{
    error: ExceptionDetail;
}