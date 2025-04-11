import {User} from "@/types/User/User";
import {create} from "zustand";
import {persist} from "zustand/middleware";

interface AuthStore {
    pendingVerificationEmail: string | null,
    setPendingVerificationEmail: (email: string) => void,
    authenticatedUser: User | null,
    setAuthenticatedUser: (user: User) => void,
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
            setAuthenticatedUser: (user: User) => {
                set({authenticatedUser: user});
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
