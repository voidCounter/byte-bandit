// useSession.ts
import {useAuthStore} from "@/store/AuthStore";
import {AxiosInstance} from "@/utils/AxiosInstance";
import {useQuery} from "@tanstack/react-query";
import {AuthenticatedUser} from "@/types/User/AuthenticatedUser";
import {AxiosError} from "axios";
import {useEffect} from "react";
import {APISuccessResponse} from "@/types/APISuccessResponse";

export default function useSession() {
    const {setAuthenticatedUser, setHome, deleteAuthenticatedUser} =
        useAuthStore();

    const authQuery = useQuery<APISuccessResponse<AuthenticatedUser>, AxiosError>({
        queryKey: ["authenticatedUser"],
        queryFn: async () => {
            const response = await AxiosInstance.get("/api/v1/auth/me");
            return response.data;
        },
        retry: 1, // Retry once on failure
        staleTime: 1000 * 60 * 5, // Consider data fresh for 5 minutes
    });

    const homeFolderQuery = useQuery<APISuccessResponse<string>, AxiosError>({
        queryKey: ["homeFolder"],
        queryFn: async () => {
            const response = await AxiosInstance.get("/api/v1/file/view/me");
            return response.data;
        },
        enabled: authQuery.isSuccess,
    });

    useEffect(() => {
        if (authQuery.isSuccess) {
            setAuthenticatedUser(authQuery.data.data);
        } else if (authQuery.isError) {
            deleteAuthenticatedUser();
        }

        if (homeFolderQuery.isSuccess) {
            setHome(homeFolderQuery.data.data);
        } else {
            setHome("");
        }
    }, [
        authQuery.isSuccess,
        authQuery.isError,
        authQuery.data,
        homeFolderQuery.isSuccess,
        homeFolderQuery.data,
        setAuthenticatedUser,
        deleteAuthenticatedUser,
        setHome,
    ]);

    return authQuery;
}