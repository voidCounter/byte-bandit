// ProtectedLayout.tsx
"use client";
import useSession from "@/hooks/useSession";
import Loading from "@/components/ui/loading";
import SessionExpiredCard from "@/app/components/session-expired-card";

export default function ProtectedLayout({
                                            children,
                                        }: {
    children: React.ReactNode;
}) {
    const {isSuccess, isLoading, isError} = useSession();

    if (isLoading) {
        return (
            <div className="flex flex-col w-full h-screen justify-center items-center">
                <Loading text={""}/>
            </div>
        );
    }

    if (isError) {
        return (
            <div className="flex flex-col w-full h-screen justify-center items-center">
                <SessionExpiredCard/>
            </div>
        );
    }

    return <>{children}</>;
}