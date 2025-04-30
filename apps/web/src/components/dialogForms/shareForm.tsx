import {Form, FormControl, FormField, FormItem, FormLabel, FormMessage} from '@/components/ui/form';
import {Input} from '@/components/ui/input';
import {Button} from '@/components/ui/button';
import {useForm} from 'react-hook-form';
import {zodResolver} from '@hookform/resolvers/zod';
import {FileSystemItem} from '@/types/Files/FileSystemItem';
import {z} from 'zod';
import {shareSchema} from "@/utils/dialogUtils";

interface ShareFormProps {
    item: FileSystemItem;
    onSubmit: (item: FileSystemItem, data: z.infer<typeof shareSchema>) => void;
    onCancel: () => void;
}

export const ShareForm = ({item, onSubmit, onCancel}: ShareFormProps) => {
    const form = useForm<z.infer<typeof shareSchema>>({
        resolver: zodResolver(shareSchema),
        defaultValues: {email: '', link: ''},
    });

    return (
        <Form {...form}>
            <form onSubmit={form.handleSubmit((data) => onSubmit(item, data))} className="space-y-4">
                <FormField
                    control={form.control}
                    name="email"
                    render={({field}) => (
                        <FormItem>
                            <FormLabel>Email</FormLabel>
                            <FormControl>
                                <Input {...field} />
                            </FormControl>
                            <FormMessage/>
                        </FormItem>
                    )}
                />
                <FormField
                    control={form.control}
                    name="link"
                    render={({field}) => (
                        <FormItem>
                            <FormLabel>Link</FormLabel>
                            <FormControl>
                                <Input {...field} />
                            </FormControl>
                            <FormMessage/>
                        </FormItem>
                    )}
                />
                <div className="flex justify-end gap-2">
                    <Button type="button" variant="outline" onClick={onCancel}>
                        Cancel
                    </Button>
                    <Button type="submit">Share</Button>
                </div>
            </form>
        </Form>
    );
};