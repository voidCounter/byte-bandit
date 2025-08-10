"use client";
import {z} from "zod";
import React from "react";
import {useAuthStore} from "@/store/AuthStore";
import {useRouter} from "next/navigation";
import {useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import {AxiosError, AxiosResponse} from "axios";
import {User} from "@/types/User/User";
import {AxiosInstance} from "@/utils/AxiosInstance";
import {Form, FormControl, FormField, FormItem, FormMessage} from "@/components/ui/form";
import {Input} from "@/components/ui/input";
import {PasswordInput} from "@/components/ui/PasswordInput";
import Link from "next/link";
import {Button} from "@/components/ui/button";
import {useMutation} from "@tanstack/react-query";
import Loading from "@/components/ui/loading";
import {APIErrorResponse} from "@/types/APIErrorResponse";
import {FormStatus} from "@/app/components/ui/status";
import SignInWithGoogle from "@/app/components/ui/sign-in-google";
import {APISuccessResponse} from "@/types/APISuccessResponse";

const loginSchema = z.object({
    email: z.string().email(),
    password: z.string().min(8, {message: "Must have at least 8 character."}).max(16),
});

export default function Login() {
    const {deleteAuthenticatedUser} = useAuthStore();
    const [errorMessage, setErrorMessage] = React.useState<string | null>(null);
    const router = useRouter();
    const loginForm = useForm<z.infer<typeof loginSchema>>({
        resolver: zodResolver(loginSchema),
        defaultValues: {
            email: "",
            password: "",
        },
    })


    const {mutate: login, isPending} = useMutation({
        mutationFn: (data: z.infer<typeof loginSchema>) => AxiosInstance.post("/api/v1/auth/login", data),
        onSuccess: data => {
            const response: AxiosResponse<APISuccessResponse<boolean>> = data;
            if (response.data.data) {
                deleteAuthenticatedUser();
                router.push("/app");
            }
        },
        onError: error => {
            if (error instanceof AxiosError && error.response) {
                const apiError = error.response.data as APIErrorResponse;
                setErrorMessage(apiError.details || 'Registration failed. Please try again.');
            }
        }
    })

    function onLoginFormSubmit(data: z.infer<typeof loginSchema>) {
        setErrorMessage(null);
        login(data);
    }

    return (
        <div className={"w-full px-4 h-screen no-scrollbar flex" +
            " justify-center overflow-y-scroll" +
            " items-center"}>
            <div className={"flex flex-col w-full rounded-lg" +
                " max-w-80"}>
                <div className={"flex-col mb-16"}>
                    <h2 className={"text-4xl font-bold"}>{`Welcome back to `}
                        <span
                            className={" bg-gradient-to-br" +
                                " from-amber-400 to-yellow-900 bg-clip-text" +
                                " text-transparent"}>Oakcan</span>
                    </h2>
                </div>
                {
                    errorMessage && <FormStatus status={'error'} message={errorMessage}/>
                }
                <div className={"flex flex-col"}><Form {...loginForm}>
                    <form onSubmit={loginForm.handleSubmit(onLoginFormSubmit)}
                          className={"space-y-3  w-full"}>
                        <FormField control={loginForm.control} name={"email"}
                                   render={({field}) => (
                                       <FormItem>
                                           <FormControl
                                           >
                                               <Input
                                                   type={"email"}
                                                   placeholder={"Email"} {...field}/>
                                           </FormControl>
                                           <FormMessage
                                               className={"text-destructive text-xs rounded-md" +
                                                   " font-normal"}/>
                                       </FormItem>
                                   )}>
                        </FormField>
                        <div className={"flex flex-col gap-2"}>
                            <FormField control={loginForm.control}
                                       name={"password"}
                                       render={({field}) => (
                                           <FormItem>
                                               <FormControl
                                               >
                                                   <PasswordInput
                                                       placeholder={"Password"}
                                                       autoComplete="current-password" {...field}
                                                   />
                                               </FormControl>
                                               <FormMessage
                                                   className={"text-destructive text-xs rounded-md" +
                                                       " font-normal"}/>
                                           </FormItem>
                                       )}>
                            </FormField>
                            <Link href={"/forgot-password"}
                                  className={"active:underline text-right" +
                                      " text-foreground/60 text-sm" +
                                      " hover:underline"}>Forgot
                                password?</Link>
                        </div>
                        <div className={"w-full pt-4"}>
                            <Button type={"submit"} className={"w-full"}
                                    disabled={isPending}
                            >{isPending ? <Loading
                                text={"Logging in"}/> : "Login"}</Button>
                        </div>
                    </form>
                </Form>
                    <div>
                        <div className="flex flex-row my-2 justify-center items-center">
                            <hr className="flex-1 border border-foreground/20"/>
                            <span className="px-2 text-sm text-foreground/20">or</span>
                            <hr className="flex-1 border border-foreground/20"/>
                        </div>
                        <SignInWithGoogle/>
                    </div>
                    <Link
                        href={"/register"}
                        className={"mt-6 hover:underline active:underline"}>{`Don't have an account? `}<span
                        className={"font-medium"}>Register</span></Link>
                    <h3 className={"mt-10 text-sm text-foreground/40 text-center"}>
                        By logging in, you agree to our <Link
                        href="/terms-of-service" className={"underline" +
                        " font-medium"}>Terms
                        of Service</Link> and <Link
                        href="/privacy-policy"
                        className={"underline font-medium"}>Privacy
                        Policy</Link>.
                    </h3>
                </div>
            </div>
        </div>
    );
}
