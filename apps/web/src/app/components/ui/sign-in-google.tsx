"use client";
import {Button} from "@/components/ui/button";
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome'
import {AxiosResponse} from "axios";
import {faGoogle} from '@fortawesome/free-brands-svg-icons'
import {AxiosInstance} from "@/utils/AxiosInstance";
import {useMutation} from "@tanstack/react-query";
import {APISuccessResponse} from "@/types/APISuccessResponse";
import {useAuthStore} from "@/store/AuthStore";

function SignInWithGoogle() {
    const {deleteAuthenticatedUser} = useAuthStore();
    const {mutate: fetchGoogleSignInUrl, isPending} = useMutation({
        mutationFn: async () => {
            const response: AxiosResponse = await AxiosInstance.get("/api/v1/auth/google");
            return response.data; // Assuming the URL is in the response body
        },
        onSuccess: (data: APISuccessResponse<string>) => {
            deleteAuthenticatedUser();
            window.location.href = data.data;
        },
        onError: (error) => {
            console.error("Failed to fetch Google Sign-In URL:", error);
        },
    });
    return (
        <Button variant={"outline"} className={"w-full"} onClick={() => {
            fetchGoogleSignInUrl()
        }} disabled={isPending} aria-label="Sign in with Google">
            <FontAwesomeIcon icon={faGoogle}/>
            {isPending ? "Connecting..." : "Continue with Google"}
        </Button>
    );
}

export default SignInWithGoogle;