import express from 'express';
import cors from 'cors';
import dotenv from 'dotenv';

import authRoutes from './routes/authRoutes';
import pluginRoutes from './routes/pluginRoutes';
import pipelineRoutes from './routes/pipelineRoutes';

dotenv.config();

const app = express();
const port = process.env.PORT || 3000;

app.use(cors());
app.use(express.json());
app.use('/uploads', express.static('uploads')); // Serve uploaded files

app.use('/auth', authRoutes);
app.use('/plugins', pluginRoutes);
app.use('/pipelines', pipelineRoutes);

app.get('/', (req, res) => {
    res.json({ message: 'OCP Server is running', version: '1.0.0' });
});

app.get('/health', (req, res) => {
    res.json({ status: 'ok', timestamp: new Date().toISOString() });
});

app.listen(port, () => {
    console.log(`Server running at http://localhost:${port}`);
});
