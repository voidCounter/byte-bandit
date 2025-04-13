import {AxiosInstance} from "@/utils/AxiosInstance";
import {useMutation} from "@tanstack/react-query";
import {AxiosError} from 'axios';

import {toast} from "sonner";
import {APIErrorResponse} from "@/types/APIErrorResponse";

export function useResendVerification({onSuccessRedirect, onErrorRedirect}: {
    onSuccessRedirect?: () => void,
    onErrorRedirect?: () => void
}) {
    return useMutation({
        mutationFn: (email: string) => AxiosInstance.post("/api/v1/user/resend-verification", {
            email: email
        }),
        onSuccess: () => {
            toast.success("Check your inbox", {description: "A Verification email is sent."});
            if (onSuccessRedirect) {
                onSuccessRedirect();
            }
        },
        onError: (error) => {
            if (error instanceof AxiosError && error.response) {
                const apiError = error.response.data as APIErrorResponse;
                toast.error(apiError.details || "Failed to resend verification email");
            } else {
                toast.error("Network error", {description: "Please try again."});
            }
            if (onErrorRedirect) {
                onErrorRedirect();
            }
        }
    })
}
