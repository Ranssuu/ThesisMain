const mongoose = require('mongoose');

const UserSchema = new mongoose.Schema({
  firstName: {
    type: String,
    required: true
  },
  lastName: {
    type: String,
    required: true
  },
  email: {
    type: String,
    required: true,
    unique: true
  },
  phoneNumber: {
    type: String,
    required: true
  },
  password: {
    type: String,
    required: true
  },
  profilePictureUrl: {
    type: String
  },
  vehicleInformation: [{
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Vehicle'
  }]
});

module.exports = mongoose.model('User', UserSchema);
