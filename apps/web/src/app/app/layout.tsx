import ProtectedLayout from "@/layouts/ProtectedLayout";

export default function AppLayout({
                                      children,
                                  }: {
    children: React.ReactNode;
}) {
    return (
        <div className={"flex justify-center items-center h-screen"}>
            <ProtectedLayout>
                {children}
            </ProtectedLayout>
        </div>
    );
}