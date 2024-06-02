const express = require('express');
const router = express.Router();
const auth = require('../middleware/authMiddleware');
const { addSmokeTest, getHistory } = require('../controllers/smokeTestController');

router.post('/smokeTests', auth, addSmokeTest);
router.get('/smokeTests/history', auth, getHistory);

module.exports = router;
