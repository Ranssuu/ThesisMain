const express = require('express');
const router = express.Router();
const { registerVehicle, getVehicle, updateVehicle } = require('../controllers/vehicleController');
const auth = require('../middleware/authMiddleware');

router.post('/register', auth, registerVehicle);
router.get('/', auth, getVehicle);
router.put('/:id', auth, updateVehicle);

module.exports = router;
