import { Router } from 'express';
import { listPipelines, savePipeline } from '../controllers/pipelineController';
import { authenticateToken } from '../middleware/authMiddleware';

const router = Router();

router.get('/', authenticateToken, listPipelines);
router.post('/', authenticateToken, savePipeline);

export default router;
