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