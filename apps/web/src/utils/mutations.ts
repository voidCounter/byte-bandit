// useFileMutations.ts
import {useMutation} from '@tanstack/react-query';
import {AxiosInstance} from '@/utils/AxiosInstance';
import {queryClient} from '@/lib/react-query';
import {APISuccessResponse} from '@/types/APISuccessResponse';
import {AxiosResponse} from 'axios';
import {z} from 'zod';
import {newFolderSchema} from "@/utils/dialogUtils";

export const useFileMutations = (folderId: string | string[] | undefined, closeDialog: () => void) => {
    const createFolder = useMutation({
        mutationFn: (data: z.infer<typeof newFolderSchema>) =>
            AxiosInstance.post('/api/v1/file/create', {
                name: data.name,
                parentId: folderId,
                type: 'FOLDER',
                status: 'UPLOADED',
            }),
        onSuccess: (data: AxiosResponse<APISuccessResponse<boolean>>) => {
            queryClient.invalidateQueries({queryKey: ['folder', folderId]});
            closeDialog();
        },
        onError: (error) => {
            console.error('Folder creation failed:', error);
            // TODO: Show toast notification
        },
    });

    return {createFolder};
};
export const useItemRenameMutation = (folderId: string | string[] | undefined, closeDialog: () => void) => {
    const renameItem = useMutation({
        mutationFn: ({data, itemId}: { data: z.infer<typeof newFolderSchema>, itemId: string }) =>
            AxiosInstance.post('/api/v1/file/update/rename', {
                name: data.name,
                itemId: itemId,
            }),
        onSuccess: (data: AxiosResponse<APISuccessResponse<boolean>>) => {
            queryClient.invalidateQueries({queryKey: ['folder', folderId]});
            closeDialog();
        },
        onError: (error) => {
            console.error('Folder creation failed:', error);
            // TODO: Show toast notification
        },
    });

    return {renameItem};
};

