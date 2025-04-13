import {AxiosInstance} from "@/utils/AxiosInstance";
import {useMutation} from "@tanstack/react-query";

import {toast} from "sonner";

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
        onError: () => {
            toast.error("Failed to resend verification email");
            if (onErrorRedirect) {
                onErrorRedirect();
            }
        }
    })
}
