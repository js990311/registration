import {ExceptionDetail} from "@/src/type/error/exceptionDetail";

export class BaseError extends Error{
    readonly details: ExceptionDetail;

    constructor(details: ExceptionDetail) {
        super(`[${details.type} in (${details.instance})]} ${details.title} : ${details.detail}`);
        this.details = details;
    }
}

export class BaseApiError extends BaseError {}

export class BusinessError extends BaseApiError {
}

export class HttpError extends BaseApiError {
}

export class NetworkError extends BaseApiError {
}

export class UnexpectedException extends BaseError {}

export function businessError(details: ExceptionDetail): BusinessError {
    return new BusinessError(details);
}

export function networkException(instance: string, cause?: unknown): NetworkError {
    return new NetworkError({
        type: 'NETWORK_ERROR',
        title: 'Network Error',
        status: 0,
        instance,
        detail:
            cause instanceof Error
                ? cause.message
                : '네트워크 연결에 실패했습니다.',
    })
}

export function httpException(instance: string, status: number): HttpError {
    return new HttpError({
        type: 'HTTP_ERROR',
        title: 'HTTP Error',
        status: status,
        instance: instance,
        detail: `HTTP ${status}`,
    })
}

export function unexpectedException(instance: string, cause?: unknown): UnexpectedException {
    return new HttpError({
        type: 'UNKNOWN_ERROR',
        title: 'Unknown Error',
        status: 500,
        instance,
        detail:
            cause instanceof Error
                ? cause.message
                : '알 수 없는 오류가 발생했습니다.',
    })
}
