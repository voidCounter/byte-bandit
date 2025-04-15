"use client";
import useSession from "@/hooks/useSession";
import {useAuthStore} from "@/store/AuthStore";
import Loading from "@/components/ui/loading";
import Link from "next/link";

export default function ProtectedLayout({
                                            children,
                                        }: {
    children: React.ReactNode;
}) {
    const {data, isLoading} = useSession();
    const {authenticatedUser} = useAuthStore();

    if (isLoading) {
        return (
            <div className="flex flex-col h-screen justify-center items-center">
                <Loading text={""}/>
            </div>
        );
    }

    if (!authenticatedUser) {
        return (
            <div className="flex flex-col h-screen justify-center items-center">
                <h1 className="text-2xl font-bold p-4">
                    Access denied! Please <Link href="/login"
                                                className={"text-2xl underline"}>login</Link> to
                    continue.
                </h1>
            </div>
        );
    }

    return (
        <>
            {children}
        </>

    );
}
