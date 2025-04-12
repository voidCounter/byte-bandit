import axios from "axios";
import Cookies from "js-cookie";

const fetchCsrfToken = async () => {
    try {
        const response = await axios.get(`${process.env.NEXT_PUBLIC_BACKEND_API}/api/v1/auth/csrf`);
        return response.data;
    } catch (error) {
        console.error("Error fetching CSRF token:", error);
        throw error; // Rethrow to handle in interceptor
    }
};
const CSRF_COOKIE_NAME = process.env.NEXT_PUBLIC_CSRF_COOKIE_NAME || "XSRF-TOKEN";
const CSRF_HEADER_NAME = process.env.NEXT_PUBLIC_CSRF_HEADER_NAME || "X-XSRF-TOKEN";

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
        if (config.method !== undefined && ["post", "put", "delete"].includes(config.method)) {
            let csrfToken = Cookies.get(CSRF_COOKIE_NAME);
            const MAX_RETRIES = 3;

            if (csrfToken) {
                config.headers[CSRF_HEADER_NAME] = csrfToken;
                return config;
            }

            // Retry fetching token with delay
            for (let i = 0; i < MAX_RETRIES && !csrfToken; i++) {
                console.warn(`[CSRF] Token missing. Attempt ${i + 1}/${MAX_RETRIES}`);
                try {
                    await fetchCsrfToken();
                    csrfToken = Cookies.get(CSRF_COOKIE_NAME);
                    if (csrfToken) {
                        config.headers[CSRF_HEADER_NAME] = csrfToken;
                        return config;
                    }
                } catch (fetchError) {
                    console.error(`[CSRF] Fetch attempt ${i + 1} failed for ${config.url}:`, fetchError);
                }
                // Delay to avoid hammering server
                await new Promise((resolve) => setTimeout(resolve, 500));
            }
            if (typeof window !== "undefined") {
                window.dispatchEvent(new CustomEvent("csrf-token-missing", {
                    detail: {url: config.url, method: config.method}
                }));
            }
            return Promise.reject(new axios.CanceledError("[CSRF] Missing token. Request cancelled."));
        }
        return config;
    }
    ,
    (error) => {
        return Promise.reject(error);
    }
)
