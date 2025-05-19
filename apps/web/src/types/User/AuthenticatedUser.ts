/**
 * Interface representing an authenticated user in the system.
 * @interface AuthenticatedUser
 * @property {string} fullName - The full name of the authenticated user.
 * @property {string} email - The email address of the authenticated user.
 *
 */
export interface AuthenticatedUser {
    fullName: string,
    email: string,
    avatarUrl: ""
}
