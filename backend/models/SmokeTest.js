const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const SmokeTestSchema = new Schema({
  opacity: {
      type: String,
      required: true
    },
  smoke_result: {
    type: String,
    required: true,
  },
  createdAt: {
    type: Date,
    required: true,
  },
  userEmail: {
      type: String,
      required: true,
  },
  owner: {
    type: Schema.Types.ObjectId,
    ref: 'User'
  }
});

module.exports = mongoose.model('SmokeTest', SmokeTestSchema);
