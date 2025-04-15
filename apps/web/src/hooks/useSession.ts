import {useAuthStore} from "@/store/AuthStore";
import {AxiosInstance} from "@/utils/AxiosInstance";
import {useQuery} from "@tanstack/react-query";
import {AuthenticatedUser} from "@/types/User/AuthenticatedUser";
import {AxiosError} from "axios";
import {useEffect} from "react";
import {APISuccessResponse} from "@/types/APISuccessResponse";

export default function useSession() {
    const {
        authenticatedUser,
        setAuthenticatedUser,
        deleteAuthenticatedUser
    } = useAuthStore();

    const query = useQuery<APISuccessResponse<AuthenticatedUser>, AxiosError>({
        queryKey: ["authenticatedUser"],
        queryFn: async () => {
            const response = await AxiosInstance.get("/api/v1/auth/me");
            return response.data;
        },
        enabled: true,
        staleTime: 5 * 60 * 1000,
    });

    useEffect(() => {
        if (query.isSuccess) {
            setAuthenticatedUser(query.data.data);
        } else if (query.isError) {
            deleteAuthenticatedUser();
        }
    }, [query.status]);

    return query;
}
