/**
 * Created by WoodHome on 2014/10/9.
 */
module.exports = function(sequelize, DataTypes) {
    var User =  sequelize.define("User", {
        imei:{type:DataTypes.STRING ,primaryKey:true},
        pushid:DataTypes.STRING,
        nickname:DataTypes.STRING
    },{
        classMethods:{
            associate:function(models){
                User.hasMany(models.Group);
            }
        }
    })

    return User;
}