import React from 'react';
import {Button} from "@/components/ui/button";
import {X} from 'lucide-react';

interface StatusProps {
    status: 'success' | 'error',
    message: string,
}

export const FormStatus = ({status, message}: StatusProps) => {
    const [close, setClose] = React.useState(false);
    return (
        !close ?
            <div className={"relative"}>
                <div
                    className={`${status == 'success' ? 'bg-success text-success-foreground' : 'bg-destructive/20'} p-4 mb-4 text-sm rounded-md font-normal border-2 border-destructive/30 text-destructive-foreground`}>
                    {message}
                </div>
                <Button variant={"ghost"} size={"icon"} onClick={() => setClose(true)}
                        className={"absolute top-0 right-0 rounded-full hover:bg-transparent text-destructive"}>
                    <X/>
                </Button>
            </div> : <div></div>
    )
}