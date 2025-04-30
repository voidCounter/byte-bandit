"use client";
import {redirect} from "next/navigation";
import {useAuthStore} from "@/store/AuthStore";

export default function MyFilesPage() {
    const {home} = useAuthStore();
    if (home) {
        redirect("/app/my-files/folder/" + home);
    } else {
        return (
            <div className="flex flex-col w-full h-screen justify-center items-center">
                <h1 className="text-2xl font-bold">No home folder found</h1>
                <p>Please contact support.</p>
            </div>
        );
    }
}
