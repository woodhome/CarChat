var express = require('express')
    ,http = require('http')
    ,db = require('./models')
    ,router = require('./routes');
var app = express();

app.use(router);
app.get('/', function(req, res){
    res.send('hello world');
});
app.set('port',18080);

db.sequelize.sync({force:false}).complete(function(err){
    if(err){
        throw err[0];
    }else{
        http.createServer(app).listen(app.get('port'), function(){
            console.log('Express server listening on port ' + app.get('port'))
        })
    }
});