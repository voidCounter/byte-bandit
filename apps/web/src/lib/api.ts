import {AxiosInstance} from "@/utils/AxiosInstance";
import {AxiosError} from "axios";


export interface ItemViewRequest {
    itemId: string;
    password?: string | null;
}

export const fetchFolderContents = async (itemId: string, password: string | null, isPublic: boolean) => {
    try {
        const itemViewRequest: ItemViewRequest = {
            itemId,
            ...(password && {password}),
        };

        const endpoint = isPublic ? `/public/folders/${itemId}` : `/api/v1/file/view`;

        const response = await AxiosInstance.post(endpoint, itemViewRequest);

        return response.data;
    } catch (error) {
        console.error(`[fetchFolderContents] Error fetching contents for item ${itemId}:`, error);
        if (error instanceof AxiosError) {
            console.error('Response data:', error.response);
            console.error('Response status:', error.response);
            console.error('Response headers:', error.response);
        }
        throw error; // Rethrow to let TanStack Query handle the error
    }
};

export const createFolder = async (name: string, parentId: string) => {
    try {
        const response = await AxiosInstance.post('/api/v1/file/create', {
            name,
            parentId,
        });
        return response.data;
    } catch (error) {
        console.error(`[createFolder] Error creating folder:`, error);
        throw error;
    }
}