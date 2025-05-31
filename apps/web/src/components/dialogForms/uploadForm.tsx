import {Form, FormControl, FormField, FormItem, FormLabel, FormMessage} from '@/components/ui/form';
import {Button} from '@/components/ui/button';
import {useForm} from 'react-hook-form';
import {zodResolver} from '@hookform/resolvers/zod';
import {z} from 'zod';
import {uploadSchema} from "@/utils/dialogUtils";
import {useState} from 'react';
import {AxiosInstance} from '@/utils/AxiosInstance';
import Loading from '../ui/loading';
import {queryClient} from "@/lib/react-query";

interface UploadFormProps {
    folderId: string,
    onSubmit: (data: z.infer<typeof uploadSchema>) => void;
    onCancel: () => void;
}

export const UploadForm = ({folderId, onSubmit, onCancel}: UploadFormProps) => {
    const [isDragging, setIsDragging] = useState(false);
    const [isUploading, setIsUploading] = useState(false);
    const form = useForm<z.infer<typeof uploadSchema>>({
        resolver: zodResolver(uploadSchema),
        defaultValues: {file: undefined},
    });

    const handleDragOver = (e: React.DragEvent<HTMLDivElement>) => {
        e.preventDefault();
        setIsDragging(true);
    };

    const handleDragLeave = (e: React.DragEvent<HTMLDivElement>) => {
        e.preventDefault();
        setIsDragging(false);
    };

    const handleDrop = (e: React.DragEvent<HTMLDivElement>) => {
        e.preventDefault();
        setIsDragging(false);
        const file = e.dataTransfer.files?.[0];
        if (file) {
            form.setValue('file', file);
        }
    };

    const handleSubmit = async (data: z.infer<typeof uploadSchema>) => {
        if (!data.file) return;

        setIsUploading(true);
        try {
            // Fetch presigned URL
            const response = await AxiosInstance.post('/api/v1/file/upload/presigned-url', {fileName: data.file.name});
            console.log("presigned_url: ", response);
            const presignedUrl = response.data.data;

            // Upload file to S3
            await AxiosInstance.put(presignedUrl, data.file, {
                headers: {'Content-Type': data.file.type},
            });

            const metadataResponse = await AxiosInstance.post('/api/v1/file/create', {
                name: data.file.name, // Use provided name or fallback to file name
                parentId: folderId, // From your context or props
                type: 'FILE',
                status: 'UPLOADED',
                mimeType: data.file.type,
                chunks: {}, // Empty object as specified
                s3Url: ``, // Construct S3 URL
                size: data.file.size,
            });
            console.log("Metadata created: ", metadataResponse.data);

            form.reset();
            queryClient.invalidateQueries({queryKey: ['folder', folderId]});
            onSubmit(data);
        } catch (error) {
            console.error('Upload failed:', error);
            form.setError('file', {message: 'Upload failed. Please try again.'});
        } finally {
            setIsUploading(false);
        }
    };

    return (
        <Form {...form}>
            <form onSubmit={form.handleSubmit((data) => handleSubmit(data))} className="space-y-4">
                <FormField
                    control={form.control}
                    name="file"
                    render={({field: {onChange, value, ...field}}) => (
                        <FormItem>
                            <FormLabel>File</FormLabel>
                            <FormControl>
                                <div
                                    className={`border-2 border-dashed rounded-lg p-6 text-center transition-colors ${
                                        isDragging ? 'border-primary bg-primary/10' : 'border-border bg-background'
                                    }`}
                                    onDragOver={handleDragOver}
                                    onDragLeave={handleDragLeave}
                                    onDrop={handleDrop}
                                >
                                    <input
                                        type="file"
                                        accept=".txt,.pdf,.docx"
                                        onChange={(e) => onChange(e.target.files?.[0])}
                                        className="hidden"
                                        id="file-upload"
                                        {...field}
                                    />
                                    <label
                                        htmlFor="file-upload"
                                        className="cursor-pointer flex flex-col items-center justify-center"
                                    >
                                        <svg
                                            className="w-12 h-12 text-muted-foreground mb-2"
                                            fill="none"
                                            stroke="currentColor"
                                            viewBox="0 0 24 24"
                                            xmlns="http://www.w3.org/2000/svg"
                                        >
                                            <path
                                                strokeLinecap="round"
                                                strokeLinejoin="round"
                                                strokeWidth="2"
                                                d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12"
                                            ></path>
                                        </svg>
                                        <p className="text-foreground">
                                            {isDragging
                                                ? 'Drop your file here'
                                                : 'Drag and drop your file here or click to select'}
                                        </p>
                                        {value && (
                                            <p className="text-sm text-muted-foreground mt-2">
                                                Selected: {value.name}
                                            </p>
                                        )}
                                    </label>
                                </div>
                            </FormControl>
                            <FormMessage/>
                        </FormItem>
                    )}
                />
                <div className="flex justify-end gap-2">
                    <Button type="button" variant="outline" onClick={onCancel}>
                        Cancel
                    </Button>
                    <Button type="submit">{isUploading ? <Loading text="Uploading"/> : "Upload"}
                    </Button>
                </div>
            </form>
        </Form>
    );
};
