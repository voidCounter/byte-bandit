// dialogUtils.ts
import {z} from 'zod';
import {FileSystemItem} from '@/types/Files/FileSystemItem';

export const dialogTypes = {
    RENAME: 'rename',
    SHARE: 'share',
    CREATE_FILE: 'create-file',
    CREATE_FOLDER: 'create-folder',
} as const;

export type DialogType = keyof typeof dialogTypes;

export const renameSchema = z.object({
    name: z.string().min(1, 'Name is required'),
});

export const shareSchema = z.object({
    email: z.string().email().optional(),
    link: z.string().url().optional(),
});

export const uploadSchema = z.object({
    file: z.instanceof(File).optional(),
});

export const newFolderSchema = z.object({
    name: z.string().min(1, 'Folder name is required'),
});

export interface DialogData {
    type: DialogType;
    item?: FileSystemItem;
}