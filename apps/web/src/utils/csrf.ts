import {AxiosInstance} from "@/utils/AxiosInstance";

export const fetchCsrfToken = async () => {
    try {
        await AxiosInstance.get("/auth/csrf");
    } catch (error) {
        console.error("Error fetching CSRF token:", error);
    }
}