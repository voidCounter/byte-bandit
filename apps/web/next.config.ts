import type {NextConfig} from "next";

const nextConfig: NextConfig = {
    /* config options here */
    pageExtensions: ['js', 'jsx', 'ts', 'tsx', 'md', 'mdx'],
    eslint: {
        ignoreDuringBuilds: true
    }
};

export default nextConfig;
