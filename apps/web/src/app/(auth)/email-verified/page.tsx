'use client';

import {Button} from '@/components/ui/button';
import {CheckCircle2} from 'lucide-react';
import {useRouter} from 'next/navigation';
import {motion} from 'framer-motion';

export default function EmailVerifiedPage() {
    const router = useRouter();

    return (
        <div className="flex flex-col items-center justify-center min-h-[60vh] space-y-4">
            <motion.div
                initial={{scale: 0, opacity: 0}}
                animate={{scale: 1, opacity: 1}}
                transition={{
                    type: 'spring',
                    stiffness: 260,
                    damping: 20
                }}
            >
                <CheckCircle2 className="text-primary w-16 h-16" strokeWidth={2}
                              aria-label="Email verification successful"/>
            </motion.div>

            <h2 className="text-xl font-semibold">Your email has been verified</h2>
            <p className="text-muted-foreground text-center max-w-sm">
                You can now sign in and start using your account.
            </p>
            <Button onClick={() => router.push('/login')}>
                Continue to Login
            </Button>
        </div>
    );
}
