import {useAuthStore} from "@/store/AuthStore";
import {AxiosInstance} from "@/utils/AxiosInstance";
import {useQuery} from "@tanstack/react-query";
import {AuthenticatedUser} from "@/types/User/AuthenticatedUser";
import {AxiosError} from "axios";
import {useEffect} from "react";
import {APISuccessResponse} from "@/types/APISuccessResponse";

export default function useSession() {
    const {
        setAuthenticatedUser,
        setHome,
        deleteAuthenticatedUser
    } = useAuthStore();

    const authQuery = useQuery<APISuccessResponse<AuthenticatedUser>, AxiosError>({
        queryKey: ["authenticatedUser"],
        queryFn: async () => {
            const response = await AxiosInstance.get("/api/v1/auth/me");
            return response.data;
        },
        enabled: true,
        staleTime: 5 * 60 * 1000,
    });

    const homeFolderQuery = useQuery<APISuccessResponse<string>, AxiosError>({
        queryKey: ["homeFolder"],
        queryFn: async () => {
            const response = await AxiosInstance.get("/api/v1/file/view/me");
            return response.data;
        },
        enabled: authQuery.isSuccess, // Only run if authQuery succeeds
        staleTime: 5 * 60 * 1000,
    });

    useEffect(() => {
        if (authQuery.isSuccess && homeFolderQuery.isSuccess) {
            setAuthenticatedUser(authQuery.data.data);
            setHome(homeFolderQuery.data.data)
        } else if (authQuery.isError) {
            deleteAuthenticatedUser();
        }
    }, [authQuery.status, homeFolderQuery.status]);

    return authQuery;
}
