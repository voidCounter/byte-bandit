import React from 'react';
import {dmMono} from "@/app/fonts";

const TermsOfService = () => {
    return (
        <div className={`max-w-4xl mx-auto p-8 shadow-lg text-foreground/90 rounded-lg ${dmMono.className}`}>
            <h1 className="text-4xl font-bold text-center mb-6">Terms of Service</h1>
            <p className="text-center text-primary mb-8">Effective Date: March 7, 2025</p>

            <div className="space-y-8">
                <section>
                    <h2 className="text-2xl font-semibold mb-4">1. Introduction</h2>
                    <p className="text-lg">
                        Welcome to oakcan. These Terms of Service govern your use of our platform. By using our
                        services, you agree to comply with and be bound by these terms. If you disagree with any part of
                        these terms, you may not use our services.
                    </p>
                </section>

                <section>
                    <h2 className="text-2xl font-semibold mb-4">2. Accounts and Registration</h2>
                    <p className="text-lg">
                        To access certain features, you must create an account. You agree to provide accurate, current,
                        and complete information during the registration process and to keep this information up to
                        date.
                    </p>
                </section>

                <section>
                    <h2 className="text-2xl font-semibold mb-4">3. Use of Service</h2>
                    <p className="text-lg">
                        You agree to use the services only for lawful purposes. You are responsible for all content you
                        upload, share, or store on the platform, and you must not violate any applicable laws, including
                        copyright laws.
                    </p>
                </section>

                <section>
                    <h2 className="text-2xl font-semibold mb-4">4. User Content</h2>
                    <p className="text-lg">
                        You retain ownership of the content you upload. By uploading or sharing content, you grant us a
                        non-exclusive, worldwide, royalty-free license to store, display, and share your content to
                        provide our services.
                    </p>
                </section>

                <section>
                    <h2 className="text-2xl font-semibold mb-4">5. Privacy</h2>
                    <p className="text-lg">
                        Your privacy is important to us. Our Privacy Policy outlines how we collect, use, and protect
                        your personal data. By using our services, you consent to our data practices as described in the
                        Privacy Policy.
                    </p>
                </section>

                <section>
                    <h2 className="text-2xl font-semibold mb-4">6. Termination</h2>
                    <p className="text-lg">
                        We may suspend or terminate your access to the platform at our discretion if you violate these
                        terms or if your use of the services is harmful to us or other users. You can also terminate
                        your account at any time by following the account deletion process.
                    </p>
                </section>

                <section>
                    <h2 className="text-2xl font-semibold mb-4">7. Liability</h2>
                    <p className="text-lg">
                        We are not liable for any indirect, incidental, or consequential damages arising from your use
                        of the service. Our total liability is limited to the amount you have paid for the service, if
                        any, during the 12 months prior to the incident.
                    </p>
                </section>

                <section>
                    <h2 className="text-2xl font-semibold mb-4">8. Indemnification</h2>
                    <p className="text-lg">
                        You agree to indemnify, defend, and hold harmless oakcan and its affiliates from any
                        and all claims, liabilities, damages, and expenses (including reasonable attorneys' fees)
                        arising from your use of the services or violation of these Terms.
                    </p>
                </section>

                <section>
                    <h2 className="text-2xl font-semibold mb-4">9. Changes to the Terms</h2>
                    <p className="text-lg">
                        We reserve the right to update these Terms of Service at any time. Any changes will be posted on
                        this page with the updated effective date. By continuing to use the service after changes are
                        posted, you accept those changes.
                    </p>
                </section>

                <section>
                    <h2 className="text-2xl font-semibold mb-4">10. Governing Law</h2>
                    <p className="text-lg">
                        These Terms of Service are governed by and construed in accordance with the laws of Bangladesh.
                        Any disputes relating to these terms will be resolved in the courts located in Dhaka.
                    </p>
                </section>
            </div>

            <div className="mt-8 text-center">
                <p className="text-lg">
                    Thank you for using oakcan. If you have any questions or concerns, please contact us at <a
                    href={"."}>oakcan</a>.
                </p>
            </div>
        </div>
    );
};

export default TermsOfService;

