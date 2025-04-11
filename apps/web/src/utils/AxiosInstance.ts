import axios from "axios";
import Cookies from "js-cookie";
import {fetchCsrfToken} from "@/utils/csrf";

// TODO: Axios Interceptor Instance
export const AxiosInstance = axios.create({
    baseURL: process.env.NEXT_PUBLIC_BACKEND_API,
    headers: {
        "Content-Type": "application/json",
    },
    withCredentials: true,
    timeout: 10000,
});

AxiosInstance.interceptors.request.use(
    async (config) => {
        if (config.method != undefined && ["post", "put", "delete"].includes(config.method)) {
            let csrfToken = Cookies.get("XSRF-TOKEN");

            if (!csrfToken) {
                await fetchCsrfToken();
                csrfToken = Cookies.get("XSRF-TOKEN");
            }

            if (csrfToken) {
                config.headers["X-XSRF-TOKEN"] = csrfToken;
            } else {
                console.warn("CSRF token not found");
            }
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
)
