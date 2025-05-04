'use client';

import {ColumnDef} from '@tanstack/react-table';
import {Folder, File} from 'lucide-react';
import {formatBytes} from '@/utils/size-formatter';
import {FileSystemItem} from '@/types/Files/FileSystemItem';
import {FileActions} from "@/app/components/FileActions";

export const columns: ColumnDef<FileSystemItem>[] = [
    {
        accessorKey: 'name',
        header: 'Name',
        cell: ({row}) => {
            const item = row.original;
            return (
                <div className="flex items-center">
                    {item.type === 'FOLDER' ? (
                        <Folder className="h-4 w-4 mx-2"/>
                    ) : (
                        <File className="h-4 w-4 mx-2"/>
                    )}
                    {item.name}
                </div>
            );
        },
    },
    {
        accessorKey: 'owner',
        header: 'Owner',
    },
    {
        accessorKey: 'updatedAt',
        header: 'Last Modified',
    },
    {
        accessorKey: 'size',
        header: 'File Size',
        cell: ({row}) => {
            const item = row.original;
            return item.type === 'FILE'
                ? item.size
                    ? item.size
                    : 'N/A'
                : item.itemCount
                    ? `${item.itemCount} items`
                    : '_';
        },
    },
    {
        id: 'actions',
        enableHiding: false,
        cell: ({row}) => <FileActions item={row.original}/>,
    },
];
