import Link from 'next/link';
import {
    Card,
    CardContent,
    CardDescription,
    CardFooter,
    CardHeader,
    CardTitle,
} from "@/components/ui/card";
import {Button} from "@/components/ui/button";
import {RefreshCw} from 'lucide-react';

export default function SessionExpiredCard() {
    return (
        <div className="flex min-h-screen flex-col items-center justify-center bg-background p-4">
            <Card className="w-full max-w-md shadow-lg">
                <CardHeader className="items-center text-center p-6">
                    <RefreshCw
                        className="h-20 w-20 text-muted-foreground/80 my-4"
                        strokeWidth={1.5}
                    />
                    <CardTitle className="text-2xl font-bold">
                        Session Expired
                    </CardTitle>
                    <CardDescription className="mt-2">
                        Your session has timed out. Please log in again to continue.
                    </CardDescription>
                </CardHeader>
                <CardFooter className="flex justify-center p-6 pt-2">
                    <Button asChild size="default">
                        <Link href="/login">
                            Login
                        </Link>
                    </Button>
                </CardFooter>
            </Card>
        </div>
    );
}