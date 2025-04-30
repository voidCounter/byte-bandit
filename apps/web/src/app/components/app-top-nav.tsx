"use client";
import {Input} from "@/components/ui/input";
import {PlusIcon, Search} from "lucide-react";
import {Button} from "@/components/ui/button";
import {Folder, File} from 'lucide-react';
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuLabel,
    DropdownMenuTrigger
} from "@/components/ui/dropdown-menu";
import {useDialogStore} from "@/store/DialogStore";

export default function AppTopNav() {
    const {openDialog} = useDialogStore();
    return (
        <div className={"flex flex-row items-center gap-2 w-full justify-center h-16 px-8"}>
            <div className={"flex items-center gap-4 w-full md:w-[500px] lg:w-[600px]"}>
                <Input startIcon={Search} type={"text"} placeholder={"Search"} className={"w-full"}/>
            </div>
            <div className={"flex items-center"}>
                <DropdownMenu modal={false}>
                    <DropdownMenuTrigger asChild>
                        <Button variant={"secondary"}>
                            <PlusIcon/>
                            New</Button>
                    </DropdownMenuTrigger>
                    <DropdownMenuContent align="end">
                        <DropdownMenuItem onClick={() => openDialog('CREATE_FOLDER', null)}>
                            <Folder className="h-4 w-4 mx-2"/>
                            Folder
                        </DropdownMenuItem>
                        <DropdownMenuItem onClick={() => openDialog('CREATE_FILE', null)}>
                            <File className="h-4 w-4 mx-2"/>
                            File
                        </DropdownMenuItem>
                    </DropdownMenuContent>
                </DropdownMenu>

            </div>
        </div>
    );
}