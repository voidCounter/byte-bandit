'use client';

import * as React from 'react';
import {
    ColumnDef,
    flexRender,
    getCoreRowModel,
    useReactTable,
} from '@tanstack/react-table';
import {
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableHeader,
    TableRow,
} from '@/components/ui/table';
import {
    ContextMenu,
    ContextMenuContent,
    ContextMenuItem,
    ContextMenuTrigger,
    ContextMenuSeparator,
} from '@/components/ui/context-menu';
import {FileSystemItem} from '@/types/Files/FileSystemItem';
import {Trash2, Download, Share2, Edit, LayoutGrid, Rows3, File, Folder} from 'lucide-react';
import {Button} from '@/components/ui/button';
import {useDialogStore} from "@/store/DialogStore";

// Action handlers (to be replaced with actual API calls)
const handleRename = (item: FileSystemItem) => {
    console.log(`Rename: ${item.name}`);
};

const handleDelete = (item: FileSystemItem) => {
    console.log(`Delete: ${item.name}`);
};

const handleDownload = (item: FileSystemItem) => {
    console.log(`Download: ${item.name}`);
    // Example: Trigger download using s3Url
    if (item.s3Url) {
        window.open(item.s3Url, '_blank');
    }
};

const handleShare = (item: FileSystemItem) => {
    console.log(`Share: ${item.name}`);
};


interface FileExplorerTableProps {
    data: FileSystemItem[];
    onFolderClick: (folderId: string) => void;
    columns: ColumnDef<FileSystemItem>[];
}

export function FileExplorerTable({data, onFolderClick, columns}: FileExplorerTableProps) {

    const {openDialog} = useDialogStore();
    const [viewMode, setViewMode] = React.useState<'table' | 'grid'>('table');
    const table = useReactTable({
        data,
        columns,
        getCoreRowModel: getCoreRowModel(),
    });


// Shared Context Menu Component
    const ItemContextMenu: React.FC<{ item: FileSystemItem; onDownload: () => void }> = ({item, onDownload}) => (
        <ContextMenuContent className="w-64">
            <ContextMenuItem onClick={() => openDialog('RENAME', item)}>
                <Edit className="mr-2 h-4 w-4"/>
                Rename
            </ContextMenuItem>
            {item.type === 'FILE' && (
                <ContextMenuItem onClick={onDownload}>
                    <Download className="mr-2 h-4 w-4"/>
                    Download
                </ContextMenuItem>
            )}
            <ContextMenuItem onClick={() => openDialog('SHARE', item)}>
                <Share2 className="mr-2 h-4 w-4"/>
                Share
            </ContextMenuItem>
            <ContextMenuSeparator/>
            <ContextMenuItem onClick={() => handleDelete(item)} className="text-red-600">
                <Trash2 className="mr-2 h-4 w-4"/>
                Delete
            </ContextMenuItem>
        </ContextMenuContent>
    );

    const toggleViewMode = () => {
        setViewMode((prev) => (prev === 'table' ? 'grid' : 'table'));
    };

    // Filter folders and files using correct type values
    const folders = data.filter((item) => item.type === 'FOLDER');
    const files = data.filter((item) => item.type === 'FILE');

    return (
        <div className="w-full overflow-y-scroll max-h-screen no-scrollbar">
            {/*Dialog for options */}
            {/* Toggle Button */}
            <div className="flex justify-end mb-4">
                <Button
                    variant="ghost"
                    size="sm"
                    onClick={toggleViewMode}
                    aria-label={`Switch to ${viewMode === 'table' ? 'grid' : 'table'} view`}
                    className="p-2 hover:bg-accent transition-colors"
                >
                    {viewMode === 'table' ? (
                        <LayoutGrid className="h-5 w-5"/>
                    ) : (
                        <Rows3 className="h-5 w-5"/>
                    )}
                </Button>
            </div>

            {/* Table View */}
            {viewMode === 'table' && (
                <div className="rounded-md border">
                    <Table>
                        <TableHeader>
                            {table.getHeaderGroups().map((headerGroup) => (
                                <TableRow key={headerGroup.id} className="hover:bg-accent">
                                    {headerGroup.headers.map((header) => (
                                        <TableHead key={header.id}>
                                            {header.isPlaceholder
                                                ? null
                                                : flexRender(header.column.columnDef.header, header.getContext())}
                                        </TableHead>
                                    ))}
                                </TableRow>
                            ))}
                        </TableHeader>
                        <TableBody>
                            {table.getRowModel().rows.length ? (
                                table.getRowModel().rows.map((row) => (
                                    <ContextMenu key={row.id} modal={false}>
                                        <ContextMenuTrigger asChild>
                                            <TableRow
                                                className="hover:bg-accent cursor-pointer"
                                                data-state={row.getIsSelected() && 'selected'}
                                                onDoubleClick={() => {
                                                    if (row.original.type === 'FOLDER') {
                                                        onFolderClick(row.original.id);
                                                    } else {
                                                        handleDownload(row.original);
                                                    }
                                                }}
                                            >
                                                {row.getVisibleCells().map((cell) => (
                                                    <TableCell key={cell.id}>
                                                        {flexRender(cell.column.columnDef.cell, cell.getContext())}
                                                    </TableCell>
                                                ))}
                                            </TableRow>
                                        </ContextMenuTrigger>
                                        <ItemContextMenu item={row.original}
                                                         onDownload={() => handleDownload(row.original)}/>
                                    </ContextMenu>
                                ))
                            ) : (
                                <TableRow>
                                    <TableCell colSpan={columns.length} className="h-24 text-center">
                                        No items found.
                                    </TableCell>
                                </TableRow>
                            )}
                        </TableBody>
                    </Table>
                </div>
            )}

            {/* Grid View */}
            {viewMode === 'grid' && (
                <div className="space-y-8">
                    {/* Folders Section */}
                    {folders.length > 0 && (
                        <div>
                            <h2 className="text-lg font-semibold mb-4">Folders</h2>
                            <div
                                className="grid gap-4"
                                style={{gridTemplateColumns: 'repeat(auto-fill, minmax(120px, 1fr))'}}
                            >
                                {folders.map((item) => (
                                    <ContextMenu key={item.id} modal={false}>
                                        <ContextMenuTrigger asChild>
                                            <div
                                                className="p-4 rounded-md border bg-card hover:bg-accent transition-colors cursor-pointer flex flex-col items-center text-center"
                                                onDoubleClick={() => onFolderClick(item.id)}
                                                role="button"
                                                aria-label={`Open folder ${item.name}`}
                                            >
                                                <Folder className="h-12 w-12 mb-2" strokeWidth={1}/>
                                                <span className="text-sm truncate w-full">{item.name}</span>
                                            </div>
                                        </ContextMenuTrigger>
                                        <ItemContextMenu item={item} onDownload={() => handleDownload(item)}/>
                                    </ContextMenu>
                                ))}
                            </div>
                        </div>
                    )}

                    {/* Files Section */}
                    {files.length > 0 && (
                        <div>
                            <h2 className="text-lg font-semibold mb-4">Files</h2>
                            <div
                                className="grid gap-4"
                                style={{gridTemplateColumns: 'repeat(auto-fill, minmax(120px, 1fr))'}}
                            >
                                {files.map((item) => (
                                    <ContextMenu key={item.id} modal={false}>
                                        <ContextMenuTrigger asChild>
                                            <div
                                                className="p-4 rounded-md border bg-card hover:bg-accent transition-colors cursor-pointer flex flex-col items-center text-center"
                                                onDoubleClick={() => handleDownload(item)}
                                                role="button"
                                                aria-label={`Download file ${item.name}`}
                                            >
                                                <File className="h-12 w-12 mb-2" strokeWidth={1}/>
                                                <span className="text-sm truncate w-full">{item.name}</span>
                                            </div>
                                        </ContextMenuTrigger>
                                        <ItemContextMenu item={item} onDownload={() => handleDownload(item)}/>
                                    </ContextMenu>
                                ))}
                            </div>
                        </div>
                    )}

                    {/* Empty State */}
                    {folders.length === 0 && files.length === 0 && (
                        <div className="text-center py-10 text-muted-foreground">
                            No items found.
                        </div>
                    )}
                </div>
            )}
            <div className={"py-20"}/>
        </div>
    );
}