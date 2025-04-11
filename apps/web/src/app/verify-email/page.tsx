"use client"
import {Button} from "@/components/ui/button"
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card"
import React from 'react';
import {useMutation} from "@tanstack/react-query";
import {AxiosInstance} from "@/utils/AxiosInstance";
import {toast} from "sonner"
import {useAuthStore} from "@/store/AuthStore";
import {MailIcon} from "lucide-react";

export default function VerifyEmailPage() {
    const {pendingVerificationEmail, setPendingVerificationEmail} = useAuthStore();

    const resendMutation = useMutation({
        mutationFn: () => AxiosInstance.post(""),
        onSuccess: () => {
            toast.message("Verification email resent", {description: "Verification email resent"});
        },
        onError: () => {
            toast.error("Failed to resend verification email");
        }
    })


    return (
        <div className="flex min-h-screen items-center justify-center p-4">
            <Card className="w-full max-w-md shadow-xl border-primary/50">
                <CardHeader className={"flex justify-center items-center"}>
                    <div className={"p-4 rounded-full bg-primary/30"}>
                        <MailIcon className={"w-10 h-10 stroke-1 text-primary-foreground"}/>
                    </div>
                    <CardTitle className="text-2xl font-black text-center">Verify your email</CardTitle>
                </CardHeader>
                <CardContent className="space-y-4">
                    <p className="text-sm text-center">
                        We've sent a verification link to <span
                        className="font-semibold">{pendingVerificationEmail}</span>. Please check
                        your inbox and follow the link to activate your account.
                    </p>
                    <div className="flex items-center justify-center gap-2">
                        <Button onClick={() => resendMutation.mutate()} disabled={resendMutation.isPending}>
                            {resendMutation.isPending ? "Resending..." : "Resend Email"}
                        </Button>
                    </div>
                    <div className="text-sm text-center">
                        Already verified?{" "}
                        <a href="/login" className="text-primary hover:underline">Log in here</a>
                    </div>
                </CardContent>
            </Card>
        </div>
    )
}
