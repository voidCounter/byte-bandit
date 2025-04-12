import axios from "axios";
import Cookies from "js-cookie";


const fetchCsrfToken = async () => {
    try {
        const response = await AxiosInstance.get("/auth/csrf");
        if (response.status == 200) {
            return response.data;
        }
    } catch (error) {
        console.error("Error fetching CSRF token:", error);
    }
}

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
                console.warn("[CSRF] Token missing. Attempting recovery...");

                try {
                    await fetchCsrfToken();
                    csrfToken = Cookies.get("XSRF-TOKEN");
                } catch (fetchError) {
                    console.error("[CSRF] Token fetch failed:", fetchError);
                }
            }

            if (csrfToken) {
                config.headers["X-XSRF-TOKEN"] = csrfToken;
                return config;
            }
            console.error("[CSRF] Token not found after recovery attempt. Blocking request.");
            if (typeof window !== "undefined") {
                window.dispatchEvent(new CustomEvent("csrf-token-missing", {
                    detail: {url: config.url, method: config.method}
                }));
            }
            return Promise.reject(new axios.Cancel("[CSRF] Missing token. Request cancelled."));
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
)
