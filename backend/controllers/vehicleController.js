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
    console.log('Fetching vehicles for user:', req.user.id);
    const vehicle = await Vehicle.find({ owner: req.user.id });
    console.log('Vehicles found:', vehicles);
    res.json(vehicle);
  } catch (err) {
    console.error('Error getting vehicle:', err.message);
    res.status(500).send('Server error');
  }
};

exports.updateVehicleByToken = async (req, res) => {
    try {
        const userId = req.user.id; // The ID of the authenticated user

        const updatedVehicle = await Vehicle.findOneAndUpdate(
            { owner: userId },
            {
                plateNo: req.body.plateNo,
                engineNo: req.body.engineNo,
                chassisNo: req.body.chassisNo,
                yearModel: req.body.yearModel,
                makeSeries: req.body.makeSeries,
                mvType: req.body.mvType,
                color: req.body.color,
                classification: req.body.classification,
            },
            { new: true }
        );

        if (!updatedVehicle) {
            return res.status(404).json({ error: 'Vehicle not found' });
        }

        res.json(updatedVehicle);
    } catch (err) {
        res.status(500).json({ error: 'Server error' });
    }
};
