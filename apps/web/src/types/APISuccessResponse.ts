/**
 * This file contains the type definition for the API success response.
 * @property {string} status - The status of the response.
 * @property {string} message - The message of the response.
 * @property {T} data - The data of the response.
 * @property {string} timestamp - The timestamp of the response.
 * @property {string} path - The path of the response.
 */
export interface APISuccessResponse<T> {
    status: string,
    message: string,
    data: T,
    timestamp: string,
    path: string
}