import {Form, FormControl, FormField, FormItem, FormLabel, FormMessage} from '@/components/ui/form';
import {Button} from '@/components/ui/button';
import {useForm} from 'react-hook-form';
import {zodResolver} from '@hookform/resolvers/zod';
import {z} from 'zod';
import {uploadSchema} from "@/utils/dialogUtils";
import {useState} from 'react';

interface UploadFormProps {
    onSubmit: (data: z.infer<typeof uploadSchema>) => void;
    onCancel: () => void;
}

export const UploadForm = ({onSubmit, onCancel}: UploadFormProps) => {
    const [isDragging, setIsDragging] = useState(false);
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

    return (
        <Form {...form}>
            <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
                <FormField
                    control={form.control}
                    name="file"
                    render={({field: {onChange, value, ...field}}) => (
                        <FormItem>
                            <FormLabel>File</FormLabel>
                            <FormControl>
                                <div
                                    className={`border-2 border-dashed rounded-lg p-6 text-center transition-colors ${
                                        isDragging ? 'border-blue-500 bg-blue-50' : 'border-gray-300 bg-gray-50'
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
                                            className="w-12 h-12 text-gray-400 mb-2"
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
                                        <p className="text-gray-600">
                                            {isDragging
                                                ? 'Drop your file here'
                                                : 'Drag and drop your file here or click to select'}
                                        </p>
                                        {value && (
                                            <p className="text-sm text-gray-500 mt-2">
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
                    <Button type="submit">Upload</Button>
                </div>
            </form>
        </Form>
    );
};