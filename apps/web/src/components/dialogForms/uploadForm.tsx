import {Form, FormControl, FormField, FormItem, FormLabel, FormMessage} from '@/components/ui/form';
import {Input} from '@/components/ui/input';
import {Button} from '@/components/ui/button';
import {useForm} from 'react-hook-form';
import {zodResolver} from '@hookform/resolvers/zod';
import {z} from 'zod';
import {uploadSchema} from "@/utils/dialogUtils";

interface UploadFormProps {
    onSubmit: (data: z.infer<typeof uploadSchema>) => void;
    onCancel: () => void;
}

export const UploadForm = ({onSubmit, onCancel}: UploadFormProps) => {
    const form = useForm<z.infer<typeof uploadSchema>>({
        resolver: zodResolver(uploadSchema),
        defaultValues: {file: undefined},
    });

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
                                <Input
                                    type="file"
                                    accept=".txt,.pdf,.docx"
                                    onChange={(e) => onChange(e.target.files?.[0])}
                                    {...field}
                                />
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