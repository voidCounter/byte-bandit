'use client';

import {FileSystemItem} from '@/types/Files/FileSystemItem';
import {
    DropdownMenu,
    DropdownMenuTrigger,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuLabel,
} from '@/components/ui/dropdown-menu';
import {Button} from '@/components/ui/button';
import {MoreVertical, Edit, Download, Share2, Trash2} from 'lucide-react';
import {useDialogStore} from "@/store/DialogStore";

export function FileActions({item}: { item: FileSystemItem }) {
    const {openDialog} = useDialogStore();

    const handleDownload = (item: FileSystemItem) => {
        console.log(`Download: ${item.name}`);
        // Example: Trigger download using s3Url
        if (item.s3Url) {
            window.open(item.s3Url, '_blank');
        }
    };

    const handleDelete = (item: FileSystemItem) => {
        console.log(`Delete: ${item.name}`);
    };
    return (
        <DropdownMenu modal={false}>
            <DropdownMenuTrigger asChild>
                <Button variant="ghost" className="h-8 w-8 p-0">
                    <span className="sr-only">Open menu</span>
                    <MoreVertical className="h-4 w-4"/>
                </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="end">
                <DropdownMenuLabel>Actions</DropdownMenuLabel>
                <DropdownMenuItem onClick={() => openDialog('RENAME', item)}>
                    <Edit className="h-4 w-4 mr-2"/>
                    Rename
                </DropdownMenuItem>
                {item.type === 'FILE' && (
                    <DropdownMenuItem onClick={() => handleDownload(item)}>
                        <Download className="h-4 w-4 mr-2"/>
                        Download
                    </DropdownMenuItem>
                )}
                <DropdownMenuItem onClick={() => openDialog('SHARE', item)}>
                    <Share2 className="h-4 w-4 mr-2"/>
                    Share
                </DropdownMenuItem>
                <DropdownMenuItem onClick={() => handleDelete(item)}>
                    <Trash2 className="mr-2 h-4 w-4"/>
                    Delete
                </DropdownMenuItem>
            </DropdownMenuContent>
        </DropdownMenu>
    );
}