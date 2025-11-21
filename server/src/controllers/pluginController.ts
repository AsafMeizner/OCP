import { Request, Response } from 'express';
import prisma from '../db';
import { AuthRequest } from '../middleware/authMiddleware';

export const listPlugins = async (req: Request, res: Response) => {
    try {
        const plugins = await prisma.plugin.findMany({
            include: { author: { select: { username: true } } },
        });
        res.json(plugins);
    } catch (error) {
        res.status(500).json({ error: 'Failed to fetch plugins' });
    }
};

export const uploadPlugin = async (req: AuthRequest, res: Response) => {
    try {
        const { name, description, price, version } = req.body;
        const file = req.file;
        const userId = req.user?.userId;

        if (!file || !userId) {
            return res.status(400).json({ error: 'Missing file or user' });
        }

        const plugin = await prisma.plugin.create({
            data: {
                name,
                description,
                price: parseFloat(price || '0'),
                version,
                fileUrl: file.path,
                authorId: userId,
            },
        });

        res.status(201).json(plugin);
    } catch (error) {
        console.error(error);
        res.status(500).json({ error: 'Failed to upload plugin' });
    }
};

export const downloadPlugin = async (req: Request, res: Response) => {
    try {
        const { id } = req.params;
        const plugin = await prisma.plugin.findUnique({ where: { id } });

        if (!plugin) {
            return res.status(404).json({ error: 'Plugin not found' });
        }

        res.download(plugin.fileUrl);
    } catch (error) {
        res.status(500).json({ error: 'Failed to download plugin' });
    }
};
