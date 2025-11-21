import { Response } from 'express';
import prisma from '../db';
import { AuthRequest } from '../middleware/authMiddleware';

export const listPipelines = async (req: AuthRequest, res: Response) => {
    try {
        const userId = req.user?.userId;
        if (!userId) return res.status(401).json({ error: 'Unauthorized' });

        const pipelines = await prisma.pipeline.findMany({
            where: { authorId: userId },
        });
        res.json(pipelines);
    } catch (error) {
        res.status(500).json({ error: 'Failed to fetch pipelines' });
    }
};

export const savePipeline = async (req: AuthRequest, res: Response) => {
    try {
        const { name, definition } = req.body;
        const userId = req.user?.userId;
        if (!userId) return res.status(401).json({ error: 'Unauthorized' });

        const pipeline = await prisma.pipeline.create({
            data: {
                name,
                definition,
                authorId: userId,
            },
        });

        res.status(201).json(pipeline);
    } catch (error) {
        res.status(500).json({ error: 'Failed to save pipeline' });
    }
};
