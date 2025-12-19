export interface PaginationInfo{
    count:number,
    requestNumber:number,
    requestSize:number,
    hasNextPage:boolean,
    totalPage: number,
    totalElements:number,
    blockLeft:number,
    blockRight:number,
}

export function defaultPaginationInfo(requestNumber:number, requestSize:number){
    return {
        count: 0,
        requestNumber: requestNumber+1,
        requestSize: requestSize,
        hasNextPage: false,
        totalPage: 0,
        totalElements: 0,
        blockLeft: 1,
        blockRight: 1,
    }
}