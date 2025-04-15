import {User} from "@/types/User/User";
import {create} from "zustand";
import {persist} from "zustand/middleware";
import {AuthenticatedUser} from "@/types/User/AuthenticatedUser";

interface AuthStore {
    pendingVerificationEmail: string | null,
    setPendingVerificationEmail: (email: string) => void,
    authenticatedUser: AuthenticatedUser | null,
    setAuthenticatedUser: (authenticatedUser: AuthenticatedUser) => void,
    deleteAuthenticatedUser: () => void
}

export const useAuthStore = create<AuthStore>()(
    persist(
        (set) => ({
            pendingVerificationEmail: null,
            authenticatedUser: null,
            setPendingVerificationEmail: (email: string) => {
                set({pendingVerificationEmail: email});
            },
            setAuthenticatedUser: (authenticatedUser: AuthenticatedUser) => {
                set({authenticatedUser: authenticatedUser});
            },
            deleteAuthenticatedUser: () => {
                set({authenticatedUser: null});
            }
        }),
        // TODO: implement a encrypted storage option
        {
            name: "auth-storage"
        }
    ));
