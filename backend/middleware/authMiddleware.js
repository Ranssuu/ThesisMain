const jwt = require('jsonwebtoken');

module.exports = function(req, res, next) {
  const token = req.header('Authorization').replace('Bearer ', '');
  console.log('Token received:', token);
  if (!token) {
    return res.status(401).json({ msg: 'No token, authorization denied' });
  }

  try {
    const decoded = jwt.verify(token, process.env.JWT_SECRET);
    req.user = decoded.user;
    console.log('Decoded user:', req.user);
    next();
  } catch (err) {
    console.log('Token is not valid');
    res.status(401).json({ msg: 'Token is not valid' });
  }
};
