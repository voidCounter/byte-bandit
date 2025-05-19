import {create} from "zustand";
import {persist} from "zustand/middleware";
import {AuthenticatedUser} from "@/types/User/AuthenticatedUser";

interface AuthStore {
    pendingVerificationEmail: string | null,
    setPendingVerificationEmail: (email: string | null) => void,
    authenticatedUser: AuthenticatedUser | null,
    home: string | null,
    setHome: (home: string | null) => void,
    setAuthenticatedUser: (authenticatedUser: AuthenticatedUser) => void,
    deleteAuthenticatedUser: () => void
}

export const useAuthStore = create<AuthStore>()(
    persist(
        (set) => ({
            pendingVerificationEmail: null,
            home: null,
            authenticatedUser: null,
            setPendingVerificationEmail: (email: string | null) => {
                set({pendingVerificationEmail: email});
            },
            setHome: (home: string | null) => {
                set({home: home});
            },
            setAuthenticatedUser: (authenticatedUser: AuthenticatedUser) => {
                set({authenticatedUser: authenticatedUser});
            },
            deleteAuthenticatedUser: () => {
                set({authenticatedUser: null});
            }
        }),
        {
            name: "auth-storage"
        }
    ));
