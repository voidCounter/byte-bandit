"use client";
import {
    Sidebar,
    SidebarContent,
    SidebarFooter,
    SidebarGroup,
    SidebarGroupContent,
    SidebarGroupLabel,
    SidebarMenu, SidebarMenuAction,
    SidebarMenuButton,
    SidebarMenuItem,
    SidebarSeparator,
} from "@/components/ui/sidebar";
import {BarChart, Clock, Home, SettingsIcon, Star, Trash, Users} from "lucide-react";
import Link from "next/link";
import {NavUser} from "@/app/components/nav-user";
import {Progress} from "@/components/ui/progress";
import {cn} from "@/lib/utils";

interface NavItem {
    title: string;
    icon: React.ReactNode;
    link: string;
}

import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogHeader,
    DialogTitle,
    DialogTrigger,
} from "@/components/ui/dialog"
import Settings from "@/app/components/settings";
import {usePathname} from "next/navigation";

const items =
    [
        {
            title: "My files",
            icon: <Home className="mr-2 h-4 w-4"/>,
            link: "/app/my-files",
        },
        {
            title: "Shared with me",
            icon: <Users className="mr-2 h-4 w-4"/>,
            link: "/app/shared-with-me",
        },
        {
            title: "Recent",
            icon: <Clock className="mr-2 h-4 w-4"/>,
            link: "/app/recent",
        },
        {
            title: "Starred",
            icon: <Star className="mr-2 h-4 w-4"/>,
            link: "/app/starred",
        },
        {
            title: "Trash",
            icon: <Trash className="mr-2 h-4 w-4"/>,
            link: "/app/trash",
        },
    ];

export function AppSidebar() {
    const pathname = usePathname();
    return (
        <Sidebar side="left" variant="sidebar" collapsible="icon">
            <SidebarContent>
                {/* Primary Navigation */}
                <SidebarGroup>
                    <SidebarGroupLabel>Navigation</SidebarGroupLabel>
                    <SidebarGroupContent>
                        <SidebarMenu>
                            {
                                items.map((item: NavItem) => (
                                    <SidebarMenuItem key={item.title}
                                                     className={`${pathname.startsWith(item.link) && "bg-primary text-background dark:text-foreground rounded-md"}`}>
                                        <SidebarMenuButton asChild>
                                            <Link href={item.link}>
                                                {item.icon}
                                                <span>{item.title}</span>
                                            </Link>
                                        </SidebarMenuButton>
                                    </SidebarMenuItem>
                                ))
                            }
                        </SidebarMenu>
                    </SidebarGroupContent>
                </SidebarGroup>

                <SidebarSeparator/>

                {/* Storage Summary */
                }
                <SidebarGroup>
                    <SidebarGroupLabel>Storage</SidebarGroupLabel>
                    <SidebarGroupContent>
                        <SidebarMenu>
                            <SidebarMenuItem
                                className={`${pathname === "/app/storage" && "bg-primary text-background dark:text-foreground rounded-md"}`}>
                                <SidebarMenuButton asChild>
                                    <Link href="/app/storage" className={"h-fit"}>
                                        <BarChart className="mr-2 h-4 w-4"/>
                                        <div className={"w-full"}>
                                            <span>Storage Used</span>
                                            <div className={"flex flex-col gap-1 mt-2"}>
                                                <span
                                                    className={"text-xs dark:text-muted-foreground"}>5GB of 10GB</span>
                                                <Progress value={50} className={""}
                                                          indicatorColor={`${pathname === "/app/storage" ? "bg-background dark:bg-foreground" : "bg-foreground"}`}>
                                                </Progress>
                                            </div>
                                        </div>
                                    </Link>
                                </SidebarMenuButton>
                            </SidebarMenuItem>
                        </SidebarMenu>
                    </SidebarGroupContent>
                </SidebarGroup>
            </SidebarContent>

            <SidebarFooter>
                <SidebarMenu>
                    <Dialog>
                        <DialogTrigger>
                            <SidebarMenuItem>
                                <SidebarMenuButton asChild>
                                    <div>
                                        <SettingsIcon/>
                                        Settings
                                    </div>
                                </SidebarMenuButton>
                            </SidebarMenuItem>
                        </DialogTrigger>
                        <DialogContent
                            className={"max-w-[90vw] min-h-[90vh] flex flex-col justify-start items-start gap-4 p-4"}>
                            <DialogTitle>Settings</DialogTitle>
                            <Settings/>
                        </DialogContent>
                    </Dialog>
                    <NavUser/>
                </SidebarMenu>
            </SidebarFooter>
        </Sidebar>
    )
        ;
}
