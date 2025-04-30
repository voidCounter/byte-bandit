"use client";
import useSession from "@/hooks/useSession";
import {useAuthStore} from "@/store/AuthStore";
import Loading from "@/components/ui/loading";
import SessionExpiredCard from "@/app/components/session-expired-card";

export default function ProtectedLayout({
                                            children,
                                        }: {
    children: React.ReactNode;
}) {
    const {isLoading} = useSession();
    const {authenticatedUser} = useAuthStore();

    if (isLoading) {
        return (
            <div className="flex flex-col w-full h-screen justify-center items-center">
                <Loading text={""}/>
            </div>
        );
    }

    if (!authenticatedUser) {
        return (
            <div className="flex flex-col w-full h-screen justify-center items-center">
                <SessionExpiredCard/>
            </div>
        );
    }

    return (
        <>
            {children}
        </>

    );
}
