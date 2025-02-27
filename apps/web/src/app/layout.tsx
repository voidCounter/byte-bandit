import type {Metadata} from "next";
import "./globals.css";
import {ThemeProvider} from "next-themes";
import {dm_Sans} from "@/app/fonts";


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
        <html lang="en">
        <body
            className={`${dm_Sans.className} antialiased flex flex-col justify-center items-center`}
        >
        <ThemeProvider attribute={"class"} defaultTheme={"system"} enableSystem={true} disableTransitionOnChange={true}>
            <div className={"max-w-[900px]"}>
                {children}
            </div>
        </ThemeProvider>
        </body>
        </html>
    );
}
