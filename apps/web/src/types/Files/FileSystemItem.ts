export type FileSystemItem = {
    id: string;
    name: string;
    type: 'file' | 'folder';
    owner: string;
    size?: number;
    itemCount?: number;
    lastModified: Date;
    shared?: boolean;
    sharedDate?: Date;
    accessLevel?: 'viewer' | 'editor';
};