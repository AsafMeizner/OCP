import { Router } from 'express';
import multer from 'multer';
import { listPlugins, uploadPlugin, downloadPlugin } from '../controllers/pluginController';
import { authenticateToken } from '../middleware/authMiddleware';

const upload = multer({ dest: 'uploads/' });
const router = Router();

router.get('/', listPlugins);
router.post('/', authenticateToken, upload.single('pluginFile'), uploadPlugin);
router.get('/:id/download', downloadPlugin);

export default router;
