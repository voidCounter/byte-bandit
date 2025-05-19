import {Form, FormControl, FormField, FormItem, FormLabel, FormMessage} from '@/components/ui/form';
import {Input} from '@/components/ui/input';
import {Button} from '@/components/ui/button';
import {useForm} from 'react-hook-form';
import {zodResolver} from '@hookform/resolvers/zod';
import {FileSystemItem} from '@/types/Files/FileSystemItem';
import {z} from 'zod';
import {renameSchema} from "@/utils/dialogUtils";

interface RenameFormProps {
    item: FileSystemItem;
    onSubmit: (item: FileSystemItem, data: z.infer<typeof renameSchema>) => void;
    onCancel: () => void;
}

export const RenameForm = ({item, onSubmit, onCancel}: RenameFormProps) => {
    const form = useForm<z.infer<typeof renameSchema>>({
        resolver: zodResolver(renameSchema),
        defaultValues: {name: item.name},
    });

    return (
        <Form {...form}>
            <form onSubmit={form.handleSubmit((data) => onSubmit(item, data))} className="space-y-4">
                <FormField
                    control={form.control}
                    name="name"
                    render={({field}) => (
                        <FormItem>
                            <FormLabel>Name</FormLabel>
                            <FormControl>
                                <Input {...field} autoFocus/>
                            </FormControl>
                            <FormMessage/>
                        </FormItem>
                    )}
                />
                <div className="flex justify-end gap-2">
                    <Button type="button" variant="outline" onClick={onCancel}>
                        Cancel
                    </Button>
                    <Button type="submit">Save</Button>
                </div>
            </form>
        </Form>
    );
};