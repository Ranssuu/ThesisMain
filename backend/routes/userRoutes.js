const express = require('express');
const { uploadProfilePicture } = require('../controllers/userController');
const auth = require('../middleware/authMiddleware');

const router = express.Router();

router.post('/profilePicture', auth, uploadProfilePicture);

module.exports = router;
