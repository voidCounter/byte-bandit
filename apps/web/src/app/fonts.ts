import {DM_Mono, DM_Sans, Inter} from "next/font/google";

export const dm_Sans = DM_Sans({
    subsets: ["latin"],
});

export const inter = Inter({
    subsets: ["latin"],
})

export const dm_Mono = DM_Mono({
    weight: ['400', '500', '300'],
    subsets: ['latin'],
});
