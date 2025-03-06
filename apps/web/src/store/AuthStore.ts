import {User} from "@/types/User";
import {create} from "zustand";
import {persist} from "zustand/middleware";

interface AuthStore {
    authenticatedUser: User | null,
    setAuthenticatedUser: (user: User) => void,
    deleteAuthenticatedUser: () => void
}

export const useAuthStore = create<AuthStore>()(
    persist(
        (set) => ({
            authenticatedUser: null,
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
