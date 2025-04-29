"use client";

import * as React from "react";
import {Tabs, TabsContent, TabsList, TabsTrigger} from "@/components/ui/tabs";
import {Label} from "@/components/ui/label";
import {Select, SelectContent, SelectItem, SelectTrigger, SelectValue} from "@/components/ui/select";
import {Sun, Moon} from "lucide-react";
import {useTheme} from "next-themes";

export default function Settings() {
    const [mounted, setMounted] = React.useState(false);
    const {theme, setTheme} = useTheme();
    React.useEffect(() => {
        setMounted(true);
    }, []);


    if (!mounted) {
        return null;
    }

    return (
        <div className="flex bg-background">
            {/* Sidebar with Tabs */}
            <Tabs defaultValue="general" className="flex">
                <TabsList className="flex flex-col h-full w-64 bg-muted/40 p-4 space-y-2">
                    <TabsTrigger
                        value="general"
                        className="w-full text-left justify-start data-[state=active]:bg-accent data-[state=active]:text-accent-foreground py-2 px-4 rounded-md"
                    >
                        General
                    </TabsTrigger>
                    {/* Add more tabs here in the future */}
                </TabsList>

                {/* Content Area */}
                <div className="flex-1 p-4">
                    <TabsContent value="general">
                        <div className="space-y-6">
                            {/* Appearance Setting */}
                            <div className="space-y-2">
                                <Label htmlFor="theme" className="text-base font-medium">
                                    Appearance
                                </Label>
                                <Select
                                    value={theme}
                                    onValueChange={(value: "light" | "dark") => setTheme(value)}
                                >
                                    <SelectTrigger id="theme" className="w-48">
                                        <SelectValue placeholder="Select theme"/>
                                    </SelectTrigger>
                                    <SelectContent>
                                        <SelectItem value="light">
                                            <div className="flex items-center">
                                                <Sun className="h-4 w-4 mr-2"/>
                                                Light
                                            </div>
                                        </SelectItem>
                                        <SelectItem value="dark">
                                            <div className="flex items-center">
                                                <Moon className="h-4 w-4 mr-2"/>
                                                Dark
                                            </div>
                                        </SelectItem>
                                    </SelectContent>
                                </Select>
                            </div>
                        </div>
                    </TabsContent>
                </div>
            </Tabs>
        </div>
    );
}