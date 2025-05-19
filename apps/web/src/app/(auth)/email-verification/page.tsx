import {Suspense} from 'react';
import EmailVerificationClient from './EmailVerificationClient';

export default function EmailVerificationPage() {
    return (
        <Suspense fallback={<div>Loading...</div>}>
            <EmailVerificationClient/>
        </Suspense>
    );
}
