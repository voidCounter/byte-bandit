import React from 'react';
import { dmMono } from "@/app/fonts";

const PrivacyPolicy = () => {
    return (
        <div className={`max-w-4xl mx-auto p-8 shadow-lg text-foreground/90 rounded-lg ${dmMono.className}`}>
            <h1 className="text-4xl font-bold text-center mb-6">Privacy Policy</h1>
            <p className="text-center text-primary mb-8">Effective Date: March 7, 2025</p>

            <div className="space-y-8">
                <section>
                    <h2 className="text-2xl font-semibold mb-4">1. Introduction</h2>
                    <p className="text-lg">
                        At oakcan, we value your privacy and are committed to protecting your personal data. This Privacy
                        Policy explains how we collect, use, and share your information when you use our services.
                    </p>
                </section>

                <section>
                    <h2 className="text-2xl font-semibold mb-4">2. Information We Collect</h2>
                    <p className="text-lg">
                        We collect information that you provide when you register for an account, use our services, or
                        communicate with us. This includes your personal details, usage data, and any content you upload
                        or share on the platform.
                    </p>
                </section>

                <section>
                    <h2 className="text-2xl font-semibold mb-4">3. How We Use Your Information</h2>
                    <p className="text-lg">
                        We use your information to provide, improve, and personalize our services, and to communicate
                        with you. This includes sending you updates, notifications, and other relevant information
                        regarding our platform.
                    </p>
                </section>

                <section>
                    <h2 className="text-2xl font-semibold mb-4">4. Sharing Your Information</h2>
                    <p className="text-lg">
                        We do not sell or rent your personal information to third parties. However, we may share your
                        information with trusted partners who assist us in providing our services, or if required by
                        law.
                    </p>
                </section>

                <section>
                    <h2 className="text-2xl font-semibold mb-4">5. Data Retention</h2>
                    <p className="text-lg">
                        We retain your personal information as long as necessary to provide our services or to comply
                        with legal obligations. You may request the deletion of your personal data by following the
                        account deletion process.
                    </p>
                </section>

                <section>
                    <h2 className="text-2xl font-semibold mb-4">6. Your Rights</h2>
                    <p className="text-lg">
                        You have the right to access, correct, or delete your personal data at any time. You can also
                        request that we restrict the processing of your data or object to certain uses of your
                        information.
                    </p>
                </section>

                <section>
                    <h2 className="text-2xl font-semibold mb-4">7. Security</h2>
                    <p className="text-lg">
                        We take reasonable measures to protect your personal information from unauthorized access,
                        disclosure, alteration, or destruction. However, no method of transmission over the internet
                        or electronic storage is 100% secure, and we cannot guarantee the absolute security of your
                        data.
                    </p>
                </section>

                <section>
                    <h2 className="text-2xl font-semibold mb-4">8. Changes to the Privacy Policy</h2>
                    <p className="text-lg">
                        We may update this Privacy Policy from time to time. Any changes will be posted on this page with
                        the updated effective date. By continuing to use our services after these changes, you accept the
                        revised policy.
                    </p>
                </section>

                <section>
                    <h2 className="text-2xl font-semibold mb-4">9. Governing Law</h2>
                    <p className="text-lg">
                        This Privacy Policy is governed by the laws of Bangladesh. Any disputes relating to your privacy
                        rights will be resolved in the courts located in Dhaka.
                    </p>
                </section>
            </div>

            <div className="mt-8 text-center">
                <p className="text-lg">
                    Thank you for trusting oakcan with your personal information. If you have any questions or concerns,
                    please contact us at <a href={"."}>oakcan</a>.
                </p>
            </div>
        </div>
    );
};

export default PrivacyPolicy;
