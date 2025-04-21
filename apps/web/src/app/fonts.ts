import {DM_Mono, DM_Sans, Inter} from "next/font/google";

export const dmSans = DM_Sans({
    subsets: ["latin"],
});

export const inter = Inter({
    subsets: ["latin"],
});

export const dmMono = DM_Mono({
    weight: ['300', '400', '500'],
    subsets: ['latin'],
});
