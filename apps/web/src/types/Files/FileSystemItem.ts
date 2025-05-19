export interface FileSystemItem {
    id: string; // item_id
    name: string;
    type: 'FOLDER' | 'FILE'; // item_type
    owner: string; // owner_email
    createdAt: string; // created_at
    updatedAt: string; // updated_at
    s3Url: string | null; // s3url
    mimeType: string | null; // mime_type
    isStarred: boolean; // is_starred (stored as string "true"/"false" in API)
    parentId: string | null; // parent_id
    permission: 'OWNER' | 'VIEWER' | 'EDITOR'; // permission
    isPasswordProtected: boolean; // is_item_password_protected
    itemCount?: number; // For folders, to show number of items (not in API, may need to calculate)
    size?: number; // For files, size in bytes (not in API, may need backend update)
}