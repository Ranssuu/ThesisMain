const express = require('express');
const router = express.Router();
const { registerVehicle, getVehicle, updateVehicleByToken } = require('../controllers/vehicleController');
const auth = require('../middleware/authMiddleware');

router.post('/register', auth, registerVehicle);
router.get('/', auth, getVehicle);
router.put('/updateByToken', auth, updateVehicleByToken);


module.exports = router;
