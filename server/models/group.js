/**
 * Created by WoodHome on 2014/10/9.
 */
module.exports = function(sequelize, DataTypes) {
    var Group =  sequelize.define("Group", {
        name:{type:DataTypes.STRING ,primaryKey:true},
        tag:DataTypes.STRING,
        chattype:{type:DataTypes.INTEGER,defaultValue:1},
        comment:DataTypes.STRING
    },{
        classMethods:{
            associate:function(models){
                Group.hasMany(models.User);
            }
        }
    })

    return Group;
}