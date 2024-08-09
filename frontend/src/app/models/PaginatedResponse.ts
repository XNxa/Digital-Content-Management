export interface PaginatedResponse<T> {
    collection: T[],
    size: number
}