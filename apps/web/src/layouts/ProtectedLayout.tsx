"use client";
import useSession from "@/hooks/useSession";
import {useAuthStore} from "@/store/AuthStore";
import {Button} from "@/components/ui/button";
import Loading from "@/components/ui/loading";

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
                <h1 className="text-xl font-medium"></h1>
                <Loading text={""}/>
            </div>
        );
    }

    if (!authenticatedUser) {
        return (
            <div className="flex flex-col h-screen justify-center items-center">
                <h1 className="text-2xl font-bold">
                    Access denied! Please <Button variant={"link"}>login</Button> to continue.
                </h1>
            </div>
        );
    }

    return (
        <div>
            {children}
        </div>
    );
}
