/**
 * Created by WoodHome on 2014/9/26.
 */

var express = require('express')
var userruter = require('./user');

var router = express.Router();

router.use('/user',userruter);

router.use('/hello',function(req,res){
    res.send('hello Welcome!!');
})

module.exports = router;