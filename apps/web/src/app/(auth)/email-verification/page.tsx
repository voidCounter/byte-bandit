'use client';

import {Button} from '@/components/ui/button';
import {AlertTriangle, CheckCircle2, XCircle} from 'lucide-react';
import {useRouter, useSearchParams} from 'next/navigation';
import {motion} from 'framer-motion';
import {useResendVerification} from "@/hooks/useResendVerification";
import {useAuthStore} from "@/store/AuthStore";
import Loading from "@/components/ui/loading";

export default function EmailVerification() {
    const router = useRouter();
    const {pendingVerificationEmail} = useAuthStore();
    const searchParams = useSearchParams();
    const status = searchParams.get('status');
    const {mutate: resendVerificationEmail, isPending} = useResendVerification({
        onSuccessRedirect: () => router.push('/verify-email'),
    });

    const handleResendVerificationEmail = async () => {
        if (pendingVerificationEmail != null) {
            resendVerificationEmail(pendingVerificationEmail);
        }
    }

    const statusConfig = {
        success: {
            icon: <CheckCircle2 className="text-primary w-16 h-16" strokeWidth={2}/>,
            heading: 'Your email has been verified',
            description: 'You can now sign in and start using your account.',
            buttonText: 'Continue to Login',
            buttonAction: () => {
                router.push('/login')
            }
        },
        expired: {
            icon: <AlertTriangle className="text-primary w-16 h-16" strokeWidth={2}/>,
            heading: 'Verification link expired',
            description: 'Your verification link is no longer valid. Please request a new one.',
            buttonText: 'Request New Link',
            buttonAction: handleResendVerificationEmail
        },
        invalid: {
            icon: <XCircle className="text-destructive w-16 h-16" strokeWidth={2}/>,
            heading: 'Invalid verification link',
            description: 'The link appears to be broken or already used. Please request a new one.',
            buttonText: 'Request New Link',
            buttonAction: handleResendVerificationEmail
        },
        error: {
            icon: <XCircle className="text-destructive w-16 h-16" strokeWidth={2}/>,
            heading: 'Something went wrong',
            description: 'An unexpected error occurred. Please try again later.',
            buttonText: 'Go to Home',
            buttonAction: () => {
                router.push('/')
            }
        }
    };

    const {
        icon,
        heading,
        description,
        buttonText,
        buttonAction
    } = statusConfig[status as keyof typeof statusConfig] || statusConfig.error;

    return (
        <div className="flex flex-col items-center justify-center min-h-[60vh] space-y-4 text-center">
            <motion.div
                initial={{scale: 0, opacity: 0}}
                animate={{scale: 1, opacity: 1}}
                transition={{type: 'spring', stiffness: 260, damping: 20}}
            >
                {icon}
            </motion.div>

            <h2 className="text-xl font-semibold">{heading}</h2>
            <p className="text-muted-foreground max-w-sm">{description}</p>
            <Button onClick={buttonAction} variant={"link"} disabled={isPending}>
                {isPending ? <Loading
                    text={""}/> : buttonText}
            </Button>
        </div>
    );
}
