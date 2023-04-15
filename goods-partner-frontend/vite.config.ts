import {defineConfig} from 'vite';
import react from '@vitejs/plugin-react';
import viteTsconfigPaths from 'vite-tsconfig-paths';
import { dependencies } from './package.json';


import svgrPlugin from 'vite-plugin-svgr';
function renderChunks(deps: Record<string, string>) {
    let chunks = {};
    Object.keys(deps).forEach((key) => {
        if (['react', 'react-router-dom', 'react-dom'].includes(key)) return;
        // @ts-ignore
        chunks[key] = [key];
    });
    return chunks;
}
// https://vitejs.dev/config/
export default defineConfig({
    plugins: [react(), viteTsconfigPaths(), svgrPlugin()],
    server: {
        port: 3000,
    },
    build: {
        outDir: 'build',
        sourcemap: false,
        // rollupOptions: {
        //     output: {
        //         manualChunks: {
        //             vendor: ['react', 'react-router-dom', 'react-dom'],
        //             ...renderChunks(dependencies),
        //         },
        //     },
        // },
    },
})