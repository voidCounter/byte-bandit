import {Form, FormControl, FormField, FormItem, FormLabel, FormMessage} from '@/components/ui/form';
import {Input} from '@/components/ui/input';
import {Button} from '@/components/ui/button';
import {useForm} from 'react-hook-form';
import {zodResolver} from '@hookform/resolvers/zod';
import {z} from 'zod';
import {newFolderSchema} from "@/utils/dialogUtils";

interface CreateFolderFormProps {
    onSubmit: (data: z.infer<typeof newFolderSchema>) => void;
    onCancel: () => void;
    isLoading: boolean;
}

export const CreateFolderForm = ({onSubmit, onCancel, isLoading}: CreateFolderFormProps) => {
    const form = useForm<z.infer<typeof newFolderSchema>>({
        resolver: zodResolver(newFolderSchema),
        defaultValues: {name: ''},
    });

    return (
        <Form {...form}>
            <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
                <FormField
                    control={form.control}
                    name="name"
                    render={({field}) => (
                        <FormItem>
                            <FormLabel>Folder Name</FormLabel>
                            <FormControl>
                                <Input {...field} autoFocus/>
                            </FormControl>
                            <FormMessage/>
                        </FormItem>
                    )}
                />
                <div className="flex justify-end gap-2">
                    <Button type="button" variant="outline" onClick={onCancel} disabled={isLoading}>
                        Cancel
                    </Button>
                    <Button type="submit" disabled={isLoading}>
                        {isLoading ? 'Creating...' : 'Create'}
                    </Button>
                </div>
            </form>
        </Form>
    );
};