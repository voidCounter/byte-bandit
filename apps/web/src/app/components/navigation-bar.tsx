"use client";
import React from 'react';
import {Button} from "@/components/ui/button";
import Image from "next/image";
import logo from "../../../public/logo.svg";
import Link from "next/link";
import {motion} from 'framer-motion';

const variants = {
    hidden: {opacity: 0, y: -20, scale: 1.2, filter: 'blur(10px)'},
    visible: {opacity: 1, y: 0, scale: 1, filter: 'blur(0px)'}
};
function NavigationBar() {
    return (
        <motion.div
            className={"w-full flex flex-row gap-4 justify-center items-center p-2"}
            initial="hidden"
            animate="visible"
            variants={variants}
            transition={{duration: 0.5, ease: "easeInOut"}}
        >
            <nav
                className={"w-full max-w-[400px] bg-primary/[0.1] flex justify-between items-center p-2 rounded-xl"}>
                <Link href={"/"} className={"flex flex-row gap-2 justify-center items-center"}>
                    <Image src={logo} alt={"logo"} width={30} height={30}/>
                    <h1 className={"text-base font-bold"}>Oakcan</h1>
                </Link>
                <div className={"flex flex-row gap-4 text-foreground/55 text-sm"}>
                    <Link href={"/docs"}>Docs</Link>
                    <Link href={"/pricing"}>Pricing</Link>
                </div>
                <Link href="/login" className={"flex gap-4"}>
                    <Button className={"text-foreground"} variant="link">Log in</Button>
                </Link>
            </nav>
        </motion.div>
    );
}

export default NavigationBar;