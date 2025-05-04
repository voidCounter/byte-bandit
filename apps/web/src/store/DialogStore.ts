import {create} from 'zustand';
import {FileSystemItem} from '@/types/Files/FileSystemItem';
import {DialogType} from "@/utils/dialogUtils";


interface DialogState {
    isOpen: boolean;
    type: DialogType | null;
    loading: boolean;
    setLoading: (loading: boolean) => void;
    item: FileSystemItem | null;
    openDialog: (type: DialogType, item: FileSystemItem | null) => void;
    closeDialog: () => void;
}

export const useDialogStore = create<DialogState>((set) => ({
    isOpen: false,
    type: null,
    item: null,
    loading: false,
    setLoading: (loading) => set({loading}),
    openDialog: (type, item) => set({isOpen: true, type, item}),
    closeDialog: () => set({isOpen: false, type: null, item: null}),
}));