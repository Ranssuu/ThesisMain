const mongoose = require('mongoose');

const VehicleSchema = new mongoose.Schema({
  plateNo: {
    type: String,
    required: true
  },
  engineNo: {
    type: String,
    required: true
  },
  chassisNo: {
    type: String,
    required: true
  },
  yearModel: {
    type: String,
    required: true
  },
  makeSeries: {
    type: String,
    required: true
  },
  mvType: {
    type: String,
    required: true
  },
  color: {
    type: String,
    required: true
  },
  classification: {
    type: String,
    required: true
  },
  owner: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User'
  },
  smokeTest: [{
    type: mongoose.Schema.Types.ObjectId,
    ref: 'SmokeTest'
  }]
});

module.exports = mongoose.model('Vehicle', VehicleSchema);
