import {LoaderCircle} from "lucide-react";
import {cn} from "@/lib/utils";

export default function Loading({text = "Loading", className}: {
    text?: string,
    className?: string
}) {
    return <div className={cn(className, "flex w-full h-full justify-center py-12 items-center")}>
        <LoaderCircle className={"animate-spin mr-2"}
                      strokeWidth={2}/>
        {text}
    </div>
}
