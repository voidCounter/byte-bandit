"use client"

import * as React from "react"
import {Moon, Sun} from "lucide-react"
import {useTheme} from "next-themes"

import {Button} from "@/components/ui/button"

export function ModeToggle() {
    const {theme, setTheme, systemTheme} = useTheme()

    const toggleTheme = () => {
        setTheme(theme == 'dark' ? 'light' : 'dark');
    }

    return (
        <Button variant={"outline"} size={"icon"} onClick={() => toggleTheme()}>
            <Sun
                className={`h-[1.2rem] w-[1.2rem] ${((theme == 'system' && systemTheme == 'dark') || (theme == 'dark')) ? 'scale-100' : 'scale-0'} rotate-0 transition-all dark:text-red-200`}/>
            <Moon
                className={`absolute h-[1.2rem] w-[1.2rem]  ${((theme == 'system' && systemTheme == 'light') || (theme == 'light')) ? 'scale-100' : 'scale-0'} scale-0 transition-all dark:text-blue-200`}/>
            <span className="sr-only">Toggle theme</span>
        </Button>
    )
}
