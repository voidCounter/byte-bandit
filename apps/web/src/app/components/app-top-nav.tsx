import {Input} from "@/components/ui/input";
import {PlusIcon, Search} from "lucide-react";
import {Button} from "@/components/ui/button";

export default function AppTopNav() {
    return (
        <div className={"flex flex-row items-center gap-2 w-full justify-center h-16 px-8"}>
            <div className={"flex items-center gap-4 w-full md:w-[500px] lg:w-[600px]"}>
                <Input startIcon={Search} type={"text"} placeholder={"Search"} className={"w-full"}/>
            </div>
            <div className={"flex items-center"}>
                <Button variant={"secondary"}>
                    <PlusIcon/>
                    New</Button>
            </div>
        </div>
    );
}