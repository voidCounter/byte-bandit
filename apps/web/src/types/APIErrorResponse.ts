type APIErrorResponse = {
    errorId: string;
    timestamp: string;
    status: number;
    error: string;
    message: string;
    errorCode: string;
    details?: string;
    path: string;
};
