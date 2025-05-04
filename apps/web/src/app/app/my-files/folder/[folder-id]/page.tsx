'use client';
import {useParams, useRouter} from 'next/navigation'

import {useQuery} from '@tanstack/react-query';
import {FileSystemItem} from '@/types/Files/FileSystemItem';
import {columns} from "../../columns";
import {FileExplorerTable} from "@/app/file-explorer/file-explorer-table";
import {fetchFolderContents} from "@/lib/api";
import Loading from "@/components/ui/loading";
import {useState} from "react";

export default function FolderPage() {
    const router = useRouter();
    const params = useParams();
    const [parent, setParent] = useState<{ name: string, id: string } | null>(null);
    const folderId = params['folder-id'];
    console.log(`Folder ID: ${folderId}`);

    type RawFileSystemItem = {
        item_id: string;
        name: string;
        item_type: string;
        owner_email: string;
        created_at: string;
        updated_at: string;
        s3url: string;
        mime_type: string;
        is_starred: string;
        parent_id: string;
        permission: string;
        is_item_password_protected: boolean;
        size: number;
    }
    const {data, isLoading} = useQuery<FileSystemItem[]>({
        queryKey: ['folder', folderId],
        queryFn: async () => {
            const response = await fetchFolderContents(folderId as string, null, false);
            setParent({
                name: response.data.name,
                id: folderId as string,
            })
            return response.data.children.map((item: RawFileSystemItem) => ({
                id: item.item_id,
                name: item.name,
                type: item.item_type,
                owner: item.owner_email,
                createdAt: item.created_at,
                updatedAt: item.updated_at,
                s3Url: item.s3url,
                mimeType: item.mime_type,
                isStarred: item.is_starred === 'true',
                parentId: item.parent_id,
                permission: item.permission,
                isPasswordProtected: item.is_item_password_protected,
                size: item.size,
            }));
        },
        enabled: !!folderId,
    });

    const handleFolderClick = (folderId: string) => {
        router.push(`/app/my-files/folder/${folderId}`);
    };

    return (
        <div>
            <div>{
                parent ? (
                    <div className="flex items-center">
                        <span className="ml-2 font-semibold">{parent.name}</span>
                    </div>
                ) : (
                    <div className="text-sm text-gray-500">Loading...</div>
                )
            }</div>
            {isLoading ? (
                <div className={"w-full h-full flex justify-center items-center"}>
                    <Loading text={""}/>
                </div>
            ) : (
                <FileExplorerTable
                    data={data || []}
                    onFolderClick={handleFolderClick}
                    columns={columns}
                />
            )}
        </div>
    );
}