"use client";
// GlobalDialog.tsx
import {Dialog, DialogContent, DialogHeader, DialogTitle, DialogDescription} from '@/components/ui/dialog';
import {useParams} from 'next/navigation';
import {useDialogStore} from '@/store/DialogStore';
import {FileSystemItem} from '@/types/Files/FileSystemItem';
import {dialogTypes} from '@/utils/dialogUtils';
import {useFileMutations} from '@/utils/mutations';
import {RenameForm} from "@/components/dialogForms/renameForms";
import {ShareForm} from "@/components/dialogForms/shareForm";
import {UploadForm} from "@/components/dialogForms/uploadForm";
import {CreateFolderForm} from "@/components/dialogForms/createFolderForm";

/**
 * GlobalDialog component to handle various dialog types (rename, share, upload, create folder).
 */
export function GlobalDialog() {
    const {isOpen, type, item, closeDialog} = useDialogStore();
    const params = useParams();
    const folderId = params['folder-id'];
    const {createFolder, createFolder: {isPending: creatingFolder}} = useFileMutations(folderId, closeDialog);

    const handleRename = (item: FileSystemItem, data: { name: string }) => {
        console.log(`Rename ${item.name} to ${data.name}`);
        closeDialog();
    };

    const handleShare = (item: FileSystemItem, data: { email?: string; link?: string }) => {
        console.log(`Share ${item.name} with`, data);
        closeDialog();
    };

    const handleUpload = (data: { file?: File }) => {
        if (data.file) {
        }
        closeDialog();
    };

    const handleCreateFolder = (data: { name: string }) => {
        createFolder.mutate(data);
    };

    return (
        <Dialog open={isOpen} onOpenChange={closeDialog}>
            <DialogContent>
                {type === 'RENAME' && item && (
                    <>
                        <DialogHeader>
                            <DialogTitle>Rename {item.name}</DialogTitle>
                            <DialogDescription>Enter a new name for this item.</DialogDescription>
                        </DialogHeader>
                        <RenameForm item={item} onSubmit={handleRename} onCancel={closeDialog}/>
                    </>
                )}
                {type === 'SHARE' && item && (
                    <>
                        <DialogHeader>
                            <DialogTitle>Share {item.name}</DialogTitle>
                            <DialogDescription>Provide email or shareable link.</DialogDescription>
                        </DialogHeader>
                        <ShareForm item={item} onSubmit={handleShare} onCancel={closeDialog}/>
                    </>
                )}
                {type === 'CREATE_FILE' && (
                    <>
                        <DialogHeader>
                            <DialogTitle>Add New File</DialogTitle>
                            <DialogDescription>Upload a new file to the current folder.</DialogDescription>
                        </DialogHeader>
                        <UploadForm onSubmit={handleUpload} onCancel={closeDialog}/>
                    </>
                )}
                {type === 'CREATE_FOLDER' && (
                    <>
                        <DialogHeader>
                            <DialogTitle>Create Folder</DialogTitle>
                            <DialogDescription>Create a new folder in the current directory.</DialogDescription>
                        </DialogHeader>
                        <CreateFolderForm
                            onSubmit={handleCreateFolder}
                            onCancel={closeDialog}
                            isLoading={creatingFolder}
                        />
                    </>
                )}
            </DialogContent>
        </Dialog>
    );
}