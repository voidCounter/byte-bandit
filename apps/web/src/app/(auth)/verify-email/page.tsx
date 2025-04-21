"use client";
import {Button} from "@/components/ui/button"
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card"
import React from 'react';
import {useAuthStore} from "@/store/AuthStore";
import {MailIcon} from "lucide-react";
import Link from 'next/link'
import {toast} from 'sonner';
import Loading from "@/components/ui/loading";
import {useResendVerification} from "@/hooks/useResendVerification";

export default function VerifyEmailPage() {
    const {pendingVerificationEmail, setPendingVerificationEmail} = useAuthStore();
    const {mutate: resendVerificationEmail, isPending} = useResendVerification({});

    const handleResendVerificationEmail = async () => {
        if (!pendingVerificationEmail) {
            toast.error("No email to verify", {description: "Please sign up first"});
            return;
        }
        resendVerificationEmail(pendingVerificationEmail);
    }

    return (
        <div className="flex min-h-screen items-center justify-center p-4">
            <Card className="w-full max-w-md shadow-xl border-primary/60 border">
                <div className={"w-full flex justify-center items-center"}>
                    <div className={"absolute -mt-0 p-4 rounded-full bg-background border-2 border-primary/60"}>
                        <MailIcon className={"w-10 h-10 stroke-1 text-primary"}/>
                    </div>
                </div>
                <CardHeader className={"flex justify-center items-center "}>
                    <CardTitle className="text-2xl font-black text-center mt-8">Verify your email</CardTitle>
                </CardHeader>
                <CardContent className="space-y-4">
                    <p className="text-sm text-center">
                        We&apos;ve sent a verification link to <span
                        className="font-semibold">{pendingVerificationEmail == null ? "No email" : pendingVerificationEmail}</span>.
                        Please check
                        your inbox and follow the link to activate your account.
                    </p>
                    <div className="flex flex-col items-center justify-center gap-2">
                        <Button onClick={handleResendVerificationEmail}
                                disabled={isPending || !pendingVerificationEmail}>
                            {isPending ? <Loading
                                text={"Resending..."}/> : "Resend"}
                        </Button>
                        <div className="text-sm text-center">
                            Already verified?{" "}
                            <Link href="/login" className="text-primary hover:underline">Log in here</Link>
                        </div>
                    </div>
                </CardContent>
            </Card>
        </div>
    )
}
