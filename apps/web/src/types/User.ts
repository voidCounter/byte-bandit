/**
 * Interface representing a user in the system.
 *
 * @interface User
 * @property {string} id - The unique identifier of the user.
 * @property {string} name - The name of the user.
 * @property {string} email - The email address of the user.
 * @property {string} [avatarUrl] - The URL of the user's avatar image (optional).
 * @property {boolean} verified - Indicates whether the user's email is verified.
 * @property {string} createdAt - The timestamp when the user was created.
 * @property {string} updatedAt - The timestamp when the user was last updated.
 */
export interface User {
    id: string,
    name: string,
    email: string,
    avatarUrl?: string,
    verified: boolean,
    createdAt: string,
    updatedAt: string,
}