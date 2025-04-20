"use client";

import {ColumnDef} from "@tanstack/react-table";
import {MoreVertical, Folder, File, Trash2, Download, Share2, Edit} from "lucide-react"; // Example icons
import {Button} from "@/components/ui/button";
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuLabel,
    DropdownMenuSeparator,
    DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import {formatBytes} from "@/utils/size-formatter"
import {formatDate} from "@/utils/date-formatter";
import {FileSystemItem} from "@/types/Files/FileSystemItem"; // Adjust import path

const handleRename = (item: FileSystemItem) => {
    console.log("Rename", item.name);
};

const handleDelete = (item: FileSystemItem) => {
    console.log("Delete", item.name);
};

const handleDownload = (item: FileSystemItem) => {
    console.log("Download", item.name);
};

const handleShare = (item: FileSystemItem) => {
    console.log("Share", item.name);
};


export const columns: ColumnDef<FileSystemItem>[] = [
    {
        accessorKey: "name",
        header: "Name",
        cell: ({row}) => {
            const item = row.original;
            return (
                <div className="flex items-center">
                    {item.type === 'folder' ? (
                        <Folder className="h-4 w-4 mr-2"/> // Folder icon
                    ) : (
                        <File className="h-4 w-4 mr-2"/> // File icon
                    )}
                    {item.name}
                </div>
            );
        },
    },
    {
        accessorKey: "owner",
        header: "Shared by",
    },
    {
        accessorKey: "size",
        header: "Size",
        cell: ({row}) => {
            const item = row.original;
            return item.type === 'file' ? formatBytes(item.size) : `${item.itemCount} items`;
        },
    },
    {
        accessorKey: "sharedDate",
        header: "Shared on",
        cell: ({row}) => {
            const item = row.original;
            return formatDate(item.sharedDate);
        },
    },
    {
        id: "actions",
        enableHiding: false,
        cell: ({row}) => {
            const item = row.original;

            return (
                <DropdownMenu>
                    <DropdownMenuTrigger asChild>
                        <Button variant="ghost" className="h-8 w-8 p-0">
                            <span className="sr-only">Open menu</span>
                            <MoreVertical className="h-4 w-4"/>
                        </Button>
                    </DropdownMenuTrigger>
                    <DropdownMenuContent align="end">
                        <DropdownMenuLabel>Actions</DropdownMenuLabel>
                        <DropdownMenuItem onClick={() => handleRename(item)}>
                            <Edit className="h-4 w-4 mr-2"/>
                            Rename
                        </DropdownMenuItem>
                        {item.type === 'file' && ( // Only show download for files
                            <DropdownMenuItem onClick={() => handleDownload(item)}>
                                <Download className="h-4 w-4 mr-2"/>
                                Download
                            </DropdownMenuItem>
                        )}
                        <DropdownMenuItem onClick={() => handleShare(item)}>
                            <Share2 className="h-4 w-4 mr-2"/>
                            Share
                        </DropdownMenuItem>
                        <DropdownMenuSeparator/>
                        <DropdownMenuItem onClick={() => handleDelete(item)} className="text-destructive-foreground">
                            <Trash2 className="h-4 w-4 mr-2"/>
                            Delete
                        </DropdownMenuItem>
                    </DropdownMenuContent>
                </DropdownMenu>
            );
        },
    },
];