const cron = require('node-cron');
const User = require('../models/User');
const Vehicle = require('../models/Vehicle');

// Run this job every hour
cron.schedule('0 * * * *', async () => {
    console.log('Running cleanup job for incomplete user accounts');
    try {
        const users = await User.find();
        for (let user of users) {
            const vehicles = await Vehicle.find({ owner: user._id });
            if (vehicles.length === 0) {
                await User.findByIdAndDelete(user._id);
                console.log(`Deleted user ${user.email} due to no vehicles`);
            }
        }
    } catch (err) {
        console.error('Error during cleanup job', err);
    }
});
