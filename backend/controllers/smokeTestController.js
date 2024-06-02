const SmokeTest = require('../models/SmokeTest');
const Vehicle = require('../models/Vehicle');

// @desc    Get most recent smoke test
// @route   POST /api/smokeTests
// @access  Private
// Add Smoke Test
exports.addSmokeTest = async (req, res) => {
  console.log('Received body:', req.body); // Log the request body
  const { opacity } = req.body;

  if (!opacity) {
      return res.status(400).json({ message: 'Opacity is required' });
    }


  const smoke_result = opacity >= 2.4 ? 'Failed' : 'Passed';
  const createdAt = new Date().toISOString();

  try {
    const smokeTest = new SmokeTest({
      opacity,
      smoke_result,
      createdAt,
      owner: req.user.id  // Use req.user.id extracted from the token
    });

    await smokeTest.save();
    console.log('Smoke test saved:', smokeTest);
    res.status(201).json(smokeTest);
  } catch (error) {
    console.error('Error saving smoke test:', error.message);
    res.status(500).json({ message: 'Server error', error });
  }
};

// @desc    Get all smoke tests history
// @route   GET /api/smokeTests/history
// @access  Private
exports.getHistory = async (req, res) => {
    try {
        const userId = req.user.id;
        console.log('Fetching smoke test history for user ID:', userId); // Log user ID
        const history = await SmokeTest.find({ owner: userId }).sort({ createdAt: -1 });
        console.log('Smoke test history:', history); // Log the result
        res.json(history);
    } catch (err) {
        console.error(err.message);
        res.status(500).send('Server Error');
    }
};
