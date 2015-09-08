var express = require('express')
    ,http = require('http')
    ,db = require('./models')
    ,onlinelogger = require('onlinelogger')
    ,morgan = require('morgan')
    ,router = require('./routes');
var app = express();

app.use(morgan("short",{stream:{write:function(str){
    onlinelogger.logger.log('info','http',{detail:str});
}}}));

app.use(router);
app.get('/', function(req, res){
    res.send('hello world');
});
app.set('port',18080);

db.sequelize.sync({force:false}).then(function(){
    http.createServer(app).listen(app.get('port'), function(){
        onlinelogger.start(7706, {db: 'mongodb://@127.0.0.1:27017/carchat'});
        onlinelogger.logger.log('info', 'started', {});
    })
}).catch(function(err){
}).done();