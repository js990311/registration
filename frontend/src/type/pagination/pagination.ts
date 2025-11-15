export interface Pagination<T> {
    data: T[];
    count: number;
    totalElements: number;
    pageNumber: number;
    pageSize: number;
    hasNextPage: boolean;
}

export type pageParams = {
    page: number,
    size: number
};