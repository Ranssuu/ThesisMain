const express = require('express');
const router = express.Router();
const auth = require('../middleware/authMiddleware');
const { addSmokeTest, getHistory } = require('../controllers/smokeTestController');

router.post('/', auth, addSmokeTest);
router.get('/history', auth, getHistory);

module.exports = router;
