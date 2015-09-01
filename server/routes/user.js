/**
 * Created by WoodHome on 2014/10/9.
 */
var express = require('express')
    ,db = require('../models')
    ,BaiduPush = require('baidupush')
    ,UUID = require('uuid-js');

var router = express.Router()
    ,baiduPushClient = BaiduPush.buildBaseApi({apiKey: 'QjFH8nzVBiGY28ikdU3Upf15', secretKey: '2hKaFNH7dmE6gew0fow19h5sEGkIh36r'})
    ,baiduAdvancedApi = BaiduPush.buildAdvancedApi({apiKey: 'QjFH8nzVBiGY28ikdU3Upf15', secretKey: '2hKaFNH7dmE6gew0fow19h5sEGkIh36r'});

var xinge = require('xinge')
    ,xingeApp = new xinge.XingeApp(2100058989,'6296048c24e7c94ce7a3001cc89a9adc');



router.use('/register',function(req,res){
    var imei = req.query.imei,
        pushid = req.query.pushid;
    if(imei && pushid){
        db.User.find({where:{imei:imei}}).success(function(user){
            if(user){
                user.updateAttributes({pushid:pushid},['pushid']).success(function(){
                    res.json({error:0,message:'update success',nickname:user.nickname ? user.nickname : ''});
                })
            }
            else{
                db.User.create({imei:imei,pushid:pushid}).success(function(user){
                    res.json({error:0,message:'create success',nickname:''});
                });
            }
        });
    }
    else{
        res.json({error:1,message:'imei and pushid cannt be null'});
    }
});

router.use('/setnickname',function(req,res){
   var imei = req.query.imei
       ,nickname = req.query.nickname;
    db.User.find(imei).success(function(user){
       if(user){
           user.updateAttributes({nickname:nickname},['nickname']).success(function(){
               res.json({error:0,message:'set nickname success'});
           })
       }
       else{
           res.json({error:1,message:'user not exist'});
       }
    });
});

router.use("/getnickname",function(req,res){
    var imei = req.query.imei;
    db.User.findOrCreate({where:{imei:imei}}).success(function(user,created){
        if(user){
            res.json({error:0,nickname:user.nickname?user.nickname:"",message:"get nickname success"});
        }
    });
})

router.use('/creategroup',function(req,res){
    var name = req.query.name
        ,comment = req.query.comment
        ,chattype = req.query.chattype ? req.query.chattype : 1;
    if(!name){
        res.json({error:2,message:'name is null'});
        return;
    }
    db.Group.find(name).success(function(group){
       if(group){
            res.json({error:1,message:'name of group already existed'});
       }
       else{
           db.Group.create({chattype:chattype,name:name,comment:comment,tag:UUID.create().toString()}).success(function(){
               res.json({error:0,message:'create success'});
           })
       }
    });
});

router.use('/getplay',function(req,res){
    res.json({error:0,url:'http://bcs.duapp.com/demovoice/media/%E7%88%B6%E4%BA%B2.mp3',name:'父亲'});
});

router.use('/getmygroup',function(req,res){
   var imei = req.query.imei;
    db.User.findOrCreate({where:{imei:imei}}).success(function(user,created){
        user.getGroups().success(function(groups){
            res.json({error:0,message:"successed",groups:groups});
        })
    });
});

router.use('/allgroup',function(req,res){
    var begin = req.query.begin
        ,count = req.query.count;
    db.Group.findAll({limit:count,offset:begin}).success(function(groups){
       res.json({error:0,groups:groups,message:"success"});
    });
});

router.use('/join',function(req,res){
    var name = req.query.name,
        imei = req.query.imei;
    db.Group.find(name).success(function(group){
        if(group){
            db.User.find(imei).success(function(user){
                if(user){
                    group.addUser(user).success(function () {
                        //baiduAdvancedApi.setTag({tag:group.tag,user_id:user.pushid},function(err,body){
                        //    res.json({error:0,message:'join success'});
                       // });
                        xingeApp.setTags([[group.tag,user.pushid]],function(){
                            res.json({error:0,message:'join success'});
                        });
                    });
                }
                else{
                    res.json({error:1,message:'user not existed'});
                }
            })
        }
        else{
            res.json({error:2,message:'group not existed'});
        }
    })
});

router.use('/quit',function(req,res){
    var imei = req.query.imei
        ,name = req.query.name;
    db.User.find(imei).success(function(user){
       if(user){
           db.Group.find(name).success(function(group){
               if(group){
                   xingeApp.deleteTags([[group.tag,user.pushid]]);
                   user.removeGroup(group).success(function(){
                       res.json({error:0,message:"success"});
                   })
               }
               else{
                   res.json({error:1,message:"group not exist"});
               }
           })
       }
        else{
           res.json({error:2,message:"user not exist"});
       }
    });
});

router.use('/speak',function(req,res){
   var name = req.query.name,
       imei = req.query.imei,
       voiceurl = req.query.voiceurl,
       voicetext = req.query.text;
    db.Group.find(name).success(function (group) {
       if(group){
           db.User.find(imei).success(function(user){
               if(user){
                   /*baiduPushClient.pushMsg({push_type:2,
                       tag:group.tag,
                       message_type:0,
                       messages:{type:0,imei:user.imei,user:user.nickname,group:group.name,voice:voiceurl,voicetext:voicetext},
                       msg_keys:UUID.create().toString()},function(err,body){
                       res.json({error:0,message:'push message success'});
                   });*/
                   var message = new xinge.AndroidMessage();
                   message.type = xinge.MESSAGE_TYPE_MESSAGE;
                   message.title = "message";
                   message.acceptTime.push(new xinge.TimeInterval(0, 0, 23, 59));
                   message.content = JSON.stringify({type:0,imei:user.imei,user:user.nickname,group:group.name,voice:voiceurl,voicetext:voicetext});
                   xingeApp.pushByTags(4,[group.tag],xinge.TAG_OPERATION_OR,message,function(err,result){
                       console.log(err);
                       console.log(result);
                       res.json({error:0,message:'push message success'});
                   });
               }
               else{
                   res.json({error:1,message:'user not exist'});
               }
           })
       }
        else{
           res.json({error:2,message:'group not exist'});
       }
    });
});

module.exports = router;