import axios from "axios";

// Axios Interceptor Instance
export const AxiosInstance = axios.create({
    baseURL: process.env.NEXT_PUBLIC_BACKEND_API,
    headers: {
        "Content-Type": "application/json",
    },
    withCredentials: true,
});
