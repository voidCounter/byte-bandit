export interface User {
    id: string,
    name: string,
    email: string,
    avatarUrl?: string,
    verified: boolean,
    createdAt: Date,
    updatedAt: Date
}