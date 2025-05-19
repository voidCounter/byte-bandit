import type {Metadata} from "next";
import "./globals.css";
import {ThemeProvider} from "next-themes";
import {dmSans} from "@/app/fonts";
import QueryProvider from "@/layouts/QueryProvider";
import {Toaster} from "@/components/ui/Toaster";


export const metadata: Metadata = {
    title: "Oakcan",
    description: "Store, access, and share files with speed",
};

export default function RootLayout({
                                       children,
                                   }: Readonly<{
    children: React.ReactNode;
}>) {
    return (
        <html lang="en" suppressHydrationWarning={true}>
        <body
            className={`${dmSans.className} antialiased`}
        >
        <ThemeProvider attribute={"class"} defaultTheme={"system"} enableSystem={true} disableTransitionOnChange={true}>
            <div
                className={"w-full"}>
                <QueryProvider>
                    <Toaster/>
                    {children}
                </QueryProvider>
            </div>
        </ThemeProvider>
        </body>
        </html>
    );
}
