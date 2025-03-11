"use client"

import * as React from "react"
import {Moon, Sun} from "lucide-react"
import {useTheme} from "next-themes"

import {Button} from "@/components/ui/button"
import {cn} from "@/lib/utils";

export function ModeToggle({className}: { className?: string }) {
    const {theme, setTheme} = useTheme()
    const [mounted, setMounted] = React.useState(false);


    React.useEffect(() => {
        setMounted(true);
    }, []);
    if (!mounted) {
        return null;
    }
    const toggleTheme = () => {
        setTheme(theme === 'dark' ? 'light' : 'dark');
    }

    return (
        <Button className={cn(className)} variant={"outline"} size={"icon"} onClick={toggleTheme}
        >
            <Sun
                className={cn("h-[1.2rem] w-[1.2rem]", theme === 'light' ? 'scale-100' : 'scale-0', "rotate-0 transition-all")}/>
            <Moon
                className={cn("absolute h-[1.2rem] w-[1.2rem]", theme === 'dark' ? 'scale-100' : 'scale-0', "transition-all dark:text-foreground")}/>
            <span className="sr-only">Toggle theme</span>
        </Button>
    )
}
