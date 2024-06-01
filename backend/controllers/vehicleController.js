const Vehicle = require('../models/Vehicle');

exports.registerVehicle = async (req, res) => {
  const { plateNo, engineNo, chassisNo, yearModel, makeSeries, mvType, color, classification } = req.body;
  try {
    const newVehicle = new Vehicle({
      plateNo,
      engineNo,
      chassisNo,
      yearModel,
      makeSeries,
      mvType,
      color,
      classification,
      owner: req.user.id
    });
    const vehicle = await newVehicle.save();
    res.json(vehicle);
  } catch (err) {
    console.error('Error registering vehicle:', err.message);
    res.status(500).send('Server error');
  }
};


exports.getVehicle = async (req, res) => {
  try {
    const vehicle = await Vehicle.find({ owner: req.user.id });
    res.json(vehicle);
  } catch (err) {
    console.error('Error getting vehicle:', err.message);
    res.status(500).send('Server error');
  }
};

exports.updateVehicle = async (req, res) => {
    try {
        const vehicleId = req.params.id; // Log the received id
        console.log('Update Vehicle Params ID:', vehicleId);

        const updates = req.body;
        console.log('Update Vehicle Request Body:', updates);

        const updatedVehicle = await Vehicle.findByIdAndUpdate(vehicleId, updates, { new: true });

        if (!updatedVehicle) {
            return res.status(404).json({ message: 'Vehicle not found' });
        }

        res.json(updatedVehicle);
    } catch (error) {
        console.error('Error updating vehicle:', error.message);
        res.status(500).json({ message: 'Server error' });
    }
};
