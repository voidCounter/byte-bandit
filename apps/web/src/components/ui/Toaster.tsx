'use client';

import {Toaster as SonnerToaster, type ToasterProps} from 'sonner';
import {useTheme} from 'next-themes';

export function Toaster() {
    const {resolvedTheme} = useTheme();

    return <SonnerToaster closeButton={true} theme={resolvedTheme as ToasterProps['theme']}/>;
}