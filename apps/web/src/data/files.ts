

import {FileSystemItem} from "@/types/Files/FileSystemItem";

export const dummyData: FileSystemItem[] = [
    {
        id: "1",
        name: "Documents",
        type: "folder",
        owner: "You",
        itemCount: 15,
        lastModified: new Date("2023-10-27T10:00:00Z"),
    },
    {
        id: "2",
        name: "Photos",
        type: "folder",
        owner: "You",
        itemCount: 250,
        lastModified: new Date("2023-11-15T14:30:00Z"),
    },
    {
        id: "3",
        name: "MyReport.docx",
        type: "file",
        owner: "You",
        size: 15000, // bytes
        lastModified: new Date("2023-10-26T09:15:00Z"),
    },
    {
        id: "4",
        name: "presentation.pptx",
        type: "file",
        owner: "Collaborator A",
        size: 550000, // bytes
        lastModified: new Date("2023-11-20T11:00:00Z"),
    },
    {
        id: "5",
        name: "Setup.exe",
        type: "file",
        owner: "Admin",
        size: 1200000, // bytes
        lastModified: new Date("2023-09-01T08:00:00Z"),
    },
    {
        id: "6",
        name: "Spreadsheets",
        type: "folder",
        owner: "You",
        itemCount: 5,
        lastModified: new Date("2023-11-18T16:00:00Z"),
    },
];