"use client";

import * as React from "react";
import {
    ColumnDef,
    flexRender,
    getCoreRowModel,
    useReactTable,
} from "@tanstack/react-table";
import {
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableHeader,
    TableRow,
} from "@/components/ui/table";
import {
    ContextMenu,
    ContextMenuContent,
    ContextMenuItem,
    ContextMenuTrigger,
    ContextMenuSeparator,
} from "@/components/ui/context-menu";
import {FileSystemItem} from "@/types/Files/FileSystemItem";
import {Trash2, Download, Share2, Edit, Grid, List, File, Folder, LayoutGrid, Rows3} from "lucide-react";

// Placeholder functions for context menu actions
const handleRename = (item: FileSystemItem) => {
    console.log(`Rename: ${item.name}`);
    // TODO: Implement rename logic (e.g., open a modal or input field)
};

const handleDelete = (item: FileSystemItem) => {
    console.log(`Delete: ${item.name}`);
    // TODO: Implement delete logic (e.g., API call to delete item)
};

const handleDownload = (item: FileSystemItem) => {
    console.log(`Download: ${item.name}`);
    // TODO: Implement download logic (e.g., trigger file download)
};

const handleShare = (item: FileSystemItem) => {
    console.log(`Share: ${item.name}`);
    // TODO: Implement share logic (e.g., copy link or open share dialog)
};

interface FileExplorerTableProps {
    data: FileSystemItem[];
    columns: ColumnDef<FileSystemItem, any>[];
}

export function FileExplorerTable({data, columns}: FileExplorerTableProps) {
    const [viewMode, setViewMode] = React.useState<"table" | "grid">("table");
    const table = useReactTable({
        data,
        columns,
        getCoreRowModel: getCoreRowModel(),
    });

    const toggleViewMode = () => {
        setViewMode((prev) => (prev === "table" ? "grid" : "table"));
    };

    // Split data into folders and files
    const folders = data.filter((item) => item.type === "folder");
    const files = data.filter((item) => item.type === "file");

    return (
        <div className="w-full">
            {/* Toggle Button */}
            <div className="flex justify-end mb-4">
                <button
                    onClick={toggleViewMode}
                    className="p-2 rounded-md hover:bg-accent transition-colors"
                    aria-label={`Switch to ${viewMode === "table" ? "grid" : "table"} view`}
                >
                    {viewMode === "table" ? (
                        <LayoutGrid className="h-5 w-5"/>
                    ) : (
                        <Rows3 className="h-5 w-5"/>
                    )}
                </button>
            </div>

            {/* Table View */}
            {viewMode === "table" && (
                <div className="rounded-md">
                    <Table>
                        <TableHeader>
                            {table.getHeaderGroups().map((headerGroup) => (
                                <TableRow key={headerGroup.id} className="hover:bg-accent">
                                    {headerGroup.headers.map((header) => (
                                        <TableHead key={header.id}>
                                            {header.isPlaceholder
                                                ? null
                                                : flexRender(
                                                    header.column.columnDef.header,
                                                    header.getContext()
                                                )}
                                        </TableHead>
                                    ))}
                                </TableRow>
                            ))}
                        </TableHeader>
                        <TableBody>
                            {table.getRowModel().rows.length ? (
                                table.getRowModel().rows.map((row) => (
                                    <ContextMenu key={row.id}>
                                        <ContextMenuTrigger asChild>
                                            <TableRow
                                                className="hover:bg-accent w-full"
                                                data-state={row.getIsSelected() && "selected"}
                                                onDoubleClick={() => {
                                                    if (row.original.type === "folder") {
                                                        console.log(`Navigate to folder: ${row.original.name}`);
                                                        // TODO: Implement folder navigation
                                                    }
                                                }}
                                            >
                                                {row.getVisibleCells().map((cell) => (
                                                    <TableCell key={cell.id}>
                                                        {flexRender(
                                                            cell.column.columnDef.cell,
                                                            cell.getContext()
                                                        )}
                                                    </TableCell>
                                                ))}
                                            </TableRow>
                                        </ContextMenuTrigger>
                                        <ContextMenuContent className="w-64">
                                            <ContextMenuItem onClick={() => handleRename(row.original)}>
                                                <Edit className="mr-2 h-4 w-4"/>
                                                Rename
                                            </ContextMenuItem>
                                            {row.original.type === "file" && (
                                                <ContextMenuItem
                                                    onClick={() => handleDownload(row.original)}
                                                >
                                                    <Download className="mr-2 h-4 w-4"/>
                                                    Download
                                                </ContextMenuItem>
                                            )}
                                            <ContextMenuItem onClick={() => handleShare(row.original)}>
                                                <Share2 className="mr-2 h-4 w-4"/>
                                                Share
                                            </ContextMenuItem>
                                            <ContextMenuSeparator/>
                                            <ContextMenuItem
                                                onClick={() => handleDelete(row.original)}
                                                className="text-red-600"
                                            >
                                                <Trash2 className="mr-2 h-4 w-4"/>
                                                Delete
                                            </ContextMenuItem>
                                        </ContextMenuContent>
                                    </ContextMenu>
                                ))
                            ) : (
                                <TableRow>
                                    <TableCell
                                        colSpan={columns.length}
                                        className="h-24 text-center"
                                    >
                                        No results.
                                    </TableCell>
                                </TableRow>
                            )}
                        </TableBody>
                    </Table>
                </div>
            )}

            {/* Grid View */}
            {viewMode === "grid" && (
                <div className="space-y-8">
                    {/* Folders Section */}
                    {folders.length > 0 && (
                        <div>
                            <h2 className="text-lg font-semibold mb-4">Folders</h2>
                            <div className="grid gap-2" style={{
                                gridTemplateColumns: "repeat(auto-fill, minmax(8rem, 1fr))",
                            }}>
                                {folders.map((item) => (
                                    <ContextMenu key={item.id}>
                                        <ContextMenuTrigger asChild>
                                            <div
                                                className=" p-4 rounded-md border bg-card hover:bg-accent
                                 transition-colors cursor-pointer flex flex-col items-center text-center"
                                                onDoubleClick={() => {
                                                    console.log(`Navigate to folder: ${item.name}`);
                                                    // TODO: Implement folder navigation
                                                }}
                                            >
                                                <Folder className=" h-12 w-12 mb-2 " strokeWidth={1}/>
                                                <span className=" text-sm truncate w-full">{item.name}</span>
                                            </div>
                                        </ContextMenuTrigger>
                                        <ContextMenuContent className=" w-64">
                                            <ContextMenuItem onClick={() => handleRename(item)}>
                                                <Edit className=" mr-2 h-4 w-4"/>
                                                Rename
                                            </ContextMenuItem>
                                            <ContextMenuItem onClick={() => handleShare(item)}>
                                                <Share2 className=" mr-2 h-4 w-4"/>
                                                Share
                                            </ContextMenuItem>
                                            <ContextMenuSeparator/>
                                            <ContextMenuItem
                                                onClick={() => handleDelete(item)}
                                                className=" text-destructive-foreground"
                                            >
                                                <Trash2 className=" mr-2 h-4 w-4"/>
                                                Delete
                                            </ContextMenuItem>
                                        </ContextMenuContent>
                                    </ContextMenu>
                                ))}
                            </div>
                        </div>
                    )}

                    {/* Files Section */}
                    {files.length > 0 && (
                        <div>
                            <h2 className=" text-lg font-semibold mb-4">Files</h2>
                            <div className="grid gap-2" style={{
                                gridTemplateColumns: "repeat(auto-fill, minmax(8rem, 1fr))",
                            }}>
                                {files.map((item) => (
                                    <ContextMenu key={item.id}>
                                        <ContextMenuTrigger asChild>
                                            <div
                                                className="p-4 rounded-md border bg-card hover:bg-accent
                                 transition-colors cursor-pointer flex flex-col items-center text-center"
                                                onDoubleClick={() => {
                                                    console.log(`Open file: ${item.name}`);
                                                    // TODO: Implement file preview or download
                                                }}
                                            >
                                                <File className=" h-12 w-12 mb-2" strokeWidth={1}/>
                                                <span className="text-sm truncate w-full">{item.name}</span>
                                            </div>
                                        </ContextMenuTrigger>
                                        <ContextMenuContent className=" w-64">
                                            <ContextMenuItem onClick={() => handleRename(item)}>
                                                <Edit className=" mr-2 h-4 w-4"/>
                                                Rename
                                            </ContextMenuItem>
                                            <ContextMenuItem onClick={() => handleDownload(item)}>
                                                <Download className=" mr-2 h-4 w-4"/>
                                                Download
                                            </ContextMenuItem>
                                            <ContextMenuItem onClick={() => handleShare(item)}>
                                                <Share2 className=" mr-2 h-4 w-4"/>
                                                Share
                                            </ContextMenuItem>
                                            <ContextMenuSeparator/>
                                            <ContextMenuItem
                                                onClick={() => handleDelete(item)}
                                                className=" text-red-600"
                                            >
                                                <Trash2 className=" mr-2 h-4 w-4"/>
                                                Delete
                                            </ContextMenuItem>
                                        </ContextMenuContent>
                                    </ContextMenu>
                                ))}
                            </div>
                        </div>
                    )}

                    {/* Empty State */}
                    {folders.length === 0 && files.length === 0 && (
                        <div className=" text-center py-10">
                            No results.
                        </div>
                    )}
                </div>
            )}
        </div>
    );
}