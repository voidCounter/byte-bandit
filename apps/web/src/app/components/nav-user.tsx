"use client"

import {
    BellIcon,
    CreditCardIcon,
    LogOutIcon,
    MoreVerticalIcon, Settings, SettingsIcon,
    UserCircleIcon,
} from "lucide-react"

import {
    Avatar,
    AvatarFallback,
    AvatarImage,
} from "@/components/ui/avatar"
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuGroup,
    DropdownMenuItem,
    DropdownMenuLabel,
    DropdownMenuSeparator,
    DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"
import {
    SidebarMenu,
    SidebarMenuButton,
    SidebarMenuItem,
    useSidebar,
} from "@/components/ui/sidebar"
import {useAuthStore} from "@/store/AuthStore";
import Link from "next/link";
import {AxiosInstance} from "@/utils/AxiosInstance";
import {APISuccessResponse} from "@/types/APISuccessResponse";
import {useMutation} from "@tanstack/react-query";
import {APIErrorResponse} from "@/types/APIErrorResponse";
import {toast} from "sonner";

export function NavUser() {
    const {isMobile} = useSidebar()
    const {authenticatedUser} = useAuthStore();
    const user = {
        name: authenticatedUser?.fullName,
        email: authenticatedUser?.email,
        avatarUrl: authenticatedUser?.avatarUrl
    }
    const {mutate: logout, isPending} = useMutation({
        mutationFn: (data) => AxiosInstance.post("/api/v1/auth/logout", data),
        onSuccess: data => {
            window.location.href = "/login";
        },
        onError: error => {
            toast.error("Logout failed. Please try again.");
        }
    })

    return (
        <SidebarMenu>
            <SidebarMenuItem>
                <DropdownMenu>
                    <DropdownMenuTrigger asChild>
                        <SidebarMenuButton
                            size="lg"
                            className="data-[state=open]:bg-sidebar-accent data-[state=open]:text-sidebar-accent-foreground"
                        >
                            <Avatar className="h-8 w-8 rounded-lg grayscale">
                                <AvatarImage src={user.avatarUrl} alt={user.name}/>
                                <AvatarFallback className="rounded-lg">CN</AvatarFallback>
                            </Avatar>
                            <div className="grid flex-1 text-left text-sm leading-tight">
                                <span className="truncate font-medium">{user.name}</span>
                                <span className="truncate text-xs text-muted-foreground">
                  {user.email}
                </span>
                            </div>
                            <MoreVerticalIcon className="ml-auto size-4"/>
                        </SidebarMenuButton>
                    </DropdownMenuTrigger>
                    <DropdownMenuContent
                        className="w-[--radix-dropdown-menu-trigger-width] min-w-56 rounded-lg z-20"
                        side={isMobile ? "bottom" : "right"}
                        align="end"
                        sideOffset={4}
                    >
                        <DropdownMenuLabel className="p-0 font-normal">
                            <div className="flex items-center gap-2 px-1 py-1.5 text-left text-sm">
                                <Avatar className="h-8 w-8 rounded-lg">
                                    <AvatarImage src={user.avatarUrl} alt={user.name}/>
                                    <AvatarFallback className="rounded-lg">{user.name?.at(0)}</AvatarFallback>
                                </Avatar>
                                <div className="grid flex-1 text-left text-sm leading-tight">
                                    <span className="truncate font-medium">{user.name}</span>
                                    <span className="truncate text-xs text-muted-foreground">
                    {user.email}
                  </span>
                                </div>
                            </div>
                        </DropdownMenuLabel>
                        <DropdownMenuSeparator/>
                        <DropdownMenuGroup>
                            <DropdownMenuItem>
                                <UserCircleIcon/>
                                Account
                            </DropdownMenuItem>
                            <DropdownMenuItem>
                                <CreditCardIcon/>
                                Billing
                            </DropdownMenuItem>
                            <DropdownMenuItem>
                                <BellIcon/>
                                Notifications
                            </DropdownMenuItem>
                        </DropdownMenuGroup>
                        <DropdownMenuSeparator/>
                        <DropdownMenuItem onClick={() => {
                            logout();
                        }}>
                            <LogOutIcon/>
                            Log out
                        </DropdownMenuItem>
                    </DropdownMenuContent>
                </DropdownMenu>
            </SidebarMenuItem>
        </SidebarMenu>
    )
}
