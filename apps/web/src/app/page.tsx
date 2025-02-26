import Image from "next/image";
import {ModeToggle} from "@/components/theme-toggler";
import {Button} from "@/components/ui/button";

export default function Home() {
    return (
        <div className="h-screen w-full">
            <ModeToggle></ModeToggle>
            <section className={'flex flex-col text-center justify-center items-center gap-4 w-full h-[75vh]'}>
                <section className={" flex flex-col gap-2"}>
                    <h1 className={"text-5xl font-black"}> Your files, always accessible,<br/>
                        anytime, anywhere.</h1>
              <h5 className={"text-foreground/55"}>Store, access, and share files with speed</h5>
                </section>
                <Button className={"w-min"}>Get Started</Button>
            </section>
        </div>
    );
}
