import {FileExplorerTable} from "@/app/file-explorer/file-explorer-table";
import {dummyData} from "@/data/files";
import {columns} from "@/app/app/my-files/columns";

export default function MyFilesPage() {
    return (
        <div className={"w-full h-full flex pt-8"}>
            <FileExplorerTable data={dummyData} columns={columns}/>
        </div>
    );
}