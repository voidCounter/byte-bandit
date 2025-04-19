import ProtectedLayout from "@/layouts/ProtectedLayout";
import {cookies} from "next/headers";
import {SidebarProvider, SidebarTrigger} from "@/components/ui/sidebar";
import {AppSidebar} from "@/app/app/app-sidebar";

export default async function AppLayout({
                                            children,
                                        }: {
    children: React.ReactNode;
}) {
    const cookieStore = await cookies();
    const defaultOpen = cookieStore.get("sidebar_state")?.value === "true"
    return (
        <div className={"flex relative w-full h-screen"}>
            <ProtectedLayout>
                <SidebarProvider defaultOpen={defaultOpen}>
                    <AppSidebar/>
                    <SidebarTrigger/>
                    <main className={"w-full pr-8"}>
                        {children}
                    </main>
                </SidebarProvider>
            </ProtectedLayout>
        </div>
    );
}