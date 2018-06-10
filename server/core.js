var server = require('http').createServer( function (request, response)
    {
        console.log(request.url);
        response.end('Hello Node.js Server!');
    });
var io = require('socket.io')(server);

server.listen(3306);
console.log('Server is working');

var mysql  = require('mysql');
var dbcon = mysql.createConnection({
    host:'den1.mysql4.gear.host',
    user:'nurespy',
    password:'Ko6yLZ_0-voR',
    database:'nurespy'
});

dbcon.connect();

var User = require('./util/user').User;

var users = [];

io.on('connection', (socket) =>
{
    users[socket.id] = new User(socket.id,'Guest');
    console.log('Someone connected');

    socket.on('signIn', (data)=>
    {
        console.log(users[socket.id].mail + ' on singIn');
        dbcon.query("SELECT password FROM user WHERE mail = '" + data.mail + "';",
        (err,table)=> 
        {
            if(!err)
            {
                if(table[0].password == data.password)
                {
                    dbcon.query("SELECT * FROM user WHERE mail = '" + data.mail + "';",
                    (err,table)=>
                    {
                        socket.emit('signIn',{mail:table[0].mail,fullname:table[0].fullname,phone:table[0].phone,status:table[0].status,group:table[0].group,stay_in:table[0].stay_in});
                        users[socket.id].mail = data.mail;
                    });       
                }    
                else
                {
                    socket.emit('signIn',{mail:-1});         
                }        
            }
            else
            {
                console.log(err);
            }
        });
    });

    socket.on('signUp', (data)=>
    {
        console.log(users[socket.id].mail + ' on singUp');
        dbcon.query("SELECT id FROM user WHERE mail = '" + data.mail + "';",
        (err,table)=>
        {
            if(!err)
            {
                if(table.length > 0)     
                {
                    socket.emit('signUp',{err:1});//mail already used
                }
                else
                {
                    dbcon.query("SELECT id FROM user WHERE phone = '" + data.phone + "';",
                    (err,table)=>
                    {
                        if (!err) 
                        {
                            if (table.length > 0) 
                            {
                                socket.emit('signUp', { err: 2 });//phone already used
                            }
                            else
                            {
                                dbcon.query("INSERT INTO user(fullname,mail,password,phone,`group`,status) VALUES('" + data.fullname + "','" + data.mail + "','" + data.password + "','" + data.phone + "','" + data.group + "','student');",
                                (err)=>
                                {
                                    if(!err)
                                    {                                   
                                         socket.emit('signUp',{err:0});
                                    }
                                    else
                                    {
                                        console.log(err);
                                    }
                                });
                            }
                        }
                        else
                        {
                            console.log(err);
                        }
                    });
                }
            } 
            else
            {
                console.log(err);
            }       
        });
    });

    socket.on('singOut', (data)=>
    {
        console.log(users[socket.id].mail+ ' on signOut');
        users[socket.id].setMail('Guest');
        while(users[socket.id].GetSpies().length > 0)
        {
            io.sockets.socket(users[socket.id].spyed_by[0]).emit('userSpyedTargetDisconnected',{mail:users[socket.id].mail});
        }
        users[socket.id].spyed_by = [];
        socket.emit('signOut',{});
    });

    socket.on('chatCreate', (data)=>
    {
        console.log(users[socket.id].mail + ' on chatCreate');
        dbcon.query("INSERT INTO chat SET name = '" + data.name + "';",
        (err)=>
        {
            socket.emit('chatCreate',{});
        })
    });

    socket.on('chatJoin', (data)=>
    {
        console.log(users[socket.id].mail + ' on chatJoin');
        dbcon.query("SELECT id FROM user WHERE mail = '" + data.mail + "';",(err,table)=>
        {
            if(!err)
            {
                dbcon.query("INSERT INTO chat_user SET chat_id = " + data.id + ",user_id = " + table[0].id + ",access = '" + data.access + "';",
                (err)=>
                {
                    if(!err)
                    {                   
                         socket.emit('chatJoin',{});
                    }
                    else
                    {
                        console.log(err);
                    }
                });
            }
            else
            {
                console.log(err);
            }
        });
    });

    socket.on('chatBan', (data)=>
    {
        console.log(users[socket.id].mail + ' on chatBan');
        dbcon.query("SELECT id FROM user WHERE mail = '" + data.mail + "';",
        (err,table)=>
        {
            if(!err)
            {
                dbcon.query("DELETE FROM chat_user WHERE user_id = " + table[0].id + " AND chat_id = " + data.id + ";",
                (err)=>
                {
                    if(!err)
                    {
                        socket.emit('chatBan',{});
                    }
                    else
                    {
                        console.log(err);
                    }
                });

            }
            else
            {
                console.log(err);
            }
        });
    });

    socket.on('chatDelete', (data)=>
    {
        console.log(users[socket.id].mail + ' on chatDelete');
        dbcon.query("DELETE FROM chat WHERE chat = " + data.id + ";",
        (err)=>
        {
            if(!err)
            {
                socket.emit('chatDelete',{});
            }
            else
            {
                console.log(err);
            }
        });
    });

    socket.on('chatGetMine', (data)=>
    {
        console.log(users[socket.id].mail + ' on chatGetMine');
        dbcon.query("SELECT chat.id FROM chat,user WHERE user.mail = '" + data.mail + "' AND chat.user_id = user.id;", 
        (err,table)=>
        {
            if(!err)
            {
                socket.emit('chatGetMine', {id:table});
            }
            else
            {
                console.log(err);
            }
        });
    });

    socket.on('chatGetInfo', (data)=>
    {
        console.log(users[socket.id].mail + ' on chatGetInfo');
        dbcon.query("SELECT name FROM chat WHERE id = " + data.id + ";",
        (err,name)=>
        {
            if(!err)
            {
                dbcon.query("SELECT user.mail,access FROM user,chat,chat_user WHERE chat_user.user_id = user.id AND chat_user.chat_id = " + data.id + ";", 
                (err,users)=>
                {
                    if(!err)
                    {
                        socket.emit('chatGetInfo',{name:name[0].name,users:users});
                    }
                    else
                    {
                        console.log(err);
                    }
                });
            }
            else
            {
                console.log(err);
            }
        });
    });

    socket.on('chatSend', (data)=>
    {
        console.log(users[socket.id].mail + ' on chatSend');
        dbcon.query("SELECT id FROM user WHERE mail = '" + users[socket.id].mail + "';",
        (err,table)=>
        {
            dbcon.query("INSERT INTO message SET user_id = " + table[0].id + ",chat_id = " + data.id + ",text = '" + data.text + "',datetime = '" + data.datetime + "';",
            (err)=>
            {
                if(!err)
                {
                    dbcon.query("SELECT mail FROM user,chat_user WHERE chat_user.user_id = user.id AND chat_user.chat_id = " + data.id + ";",
                    (err,table)=>
                    {
                        if(!err)
                        {
                            for(var u in users)
                            {
                                for(var t  in table)
                                {
                                    if(u.login == t.login)
                                    {
                                        io.sockets.socket(u.id).emit('chatMessage',{chat_id:data.id,from:users[socket.id].mail,text:data.text,datetime:data.datetime});
                                    }
                                }
                            }
                            socket.emit('chatSend',{});
                        }
                        else
                        {
                            console.log(err);
                        }
                    });
                }
                else
                {
                    console.log(err);
                }
            });            
        });
    });

    socket.on('charGetMessages', (data)=>
    {
        console.log(users[socket.id].mail + ' on chatGetMessages');
        dbcon.query("SELECT user.mail,text,datetime FROM message,user WHERE message.user_id = user.id AND message.chat_id = " +  data.id+ ";",
        (err,table)=>
        {
            if(!err)
            {
                socket.emit('charGetMessages',{messages:table});
            }
            else
            {
                console.log(err);
            }
        });
    });

    socket.on('chatChangeAccess', (data)=>
    {
        console.log(users[socket.id].mail + ' on chatChangeAccess');
        dbcon.query("SELECT id FROM user WHERE mail = '" + data.mail + "';", 
        (err,table)=>
        {
            dbcon.query("UPDATE chat_user SET access = '" + data.access + "' WHERE chat_id = " + data.id + " AND user_id = " + table[0].id + ";", 
            (err)=>
            {
                if(!err)
                {
                    socket.emit('chatChangeAccess',{});
                }
                else
                {
                    console.log(err);
                }
            });
        });
    });

    socket.on('userEditProfile', (data)=>
    {
        console.log(users[socket.id].mail + ' on userEditProfile');
        dbcon.query("UPDATE user SET fullname = '" + data.fullname + "', phone = '" + data.phone + "', status = '" + data.status + "' `group` = '" + data.group + "';", 
        (err)=>
        {
            socket.emit('userEditProfile',{});
        });
    });

    socket.on('userGetInfo', (data)=>
    {
        console.log(users[socket.id].mail + ' on userGetInfo');
        dbcon.query("SELECT * FROM user WHERE mail = '" + data.mail + "';", 
        (err, table)=>
        {
            if(err)
            {
                socket.emit('userGetInfo',{err:1});
            }
            else
            {
                socket.emit('userGetInfo',{err:0, fullname:table[0].fullname, phone:table[0].phone, group:table[0].group, status:table[0].status});
            }
        });
    });

    socket.on('userMove', (data) => 
    {
        console.log(users[socket.id].mail + ' on userMove');
        users[socket.id].latitude = data.latitude;
        users[socket.id].longitude = data.longitude;
        users[socket.id].height = data.height;
        for(var u in users)
        {
            io.sockets.socket(u.id).emit('userMove',{spyed:0,mail:users[socket.id].mail,latitude:data.latitude, longitude:data.longitude, height:data.height});
        }
        for(var spy in users[socket.id].spyed_by)
        {
            io.sockets.socket(spy).emit('userMove',{spyed:1,mail:users[socket.id].mail,latitude:data.latitude, longitude:data.longitude, height:data.height});
        }
    });
    
    socket.on('userDelete', (data) =>
    {
        console.log(users[socket.id].mail + ' on userDelete');
        dbcon.query("DELETE FROM user WHERE mail = '" + data.mail + "';");
    })

    socket.on('userSpy', (data) =>
    {
        console.log(users[socket.id].mail + ' on userPsy');
        var b = 1;
        for(var u in users)
        {
            if(u.mail == data.mail)
            {
                u.spyed_by.push(socket.id);
                b = 0;
                break;
            }
        }
        socket.emit('userSpy',{err:b});
    });

    socket.on('userSpyStop', (data) =>
    {
        console.log(users[socket.id].mail + ' on userSpyStop');
        for(var u in users)
        {
            if(u.mail == data.mail)
            {
                u.spyed_by.splice(indexOf(scoket.id),1);
                socket.emit('userSpyStop',{});
                break;
            }
        }
    });

    socket.on('userFilter', (data)=>
    {
        console.log(users[socket.id].mail + ' on userFilter');
        dbcon.query("SELECT mail FROM user WHERE status = '" + data.status + "' AND `group` = '" + data.group + "';",
        (err,table)=>
        {
            if(!err)
            {
                socket.emit('userFilter',{users:table});
            }
            else
            {
                console.log(err);
            }
        });
    });

    socket.io('userGetGroups', (data)=>
    {
        console.log(users[socket.id].mail + ' on userGetGroups');
        dbcon.query("SELECT DISTINCT `group` FROM user WHERE status = '" + data.status + "';", 
        (err,table)=>
        {
            if(!err)
            {
                socket.emit('userGetGroups',{groups:table});
            }
            else
            {
                console.log(err);
            }
        });
    });

    socket.on('eventCreate', (data) =>
    {
        console.log(users[socket.id].mail + ' on eventCreate');
        dbcon.query("SELECT id FROM user WHERE mail = '" + users[socket.id].mail + "';", 
        (err,user)=>
        {
            dbcon.query("INSERT INTO event SET name = '" + data.name + "', user_id = '" + user[0].id
            + "', datetime = '" + data.datetime + "', location = '" + data.location 
            + "', duration = '" + data.duration + "',  description = '" + data.description + "';", 
            (err) =>
            {
                socket.emit('eventCreate', {err:err});
            });
        });
    });

    socket.on('eventGetAll', (data) =>
    {
        console.log(users[socket.id].mail + ' on eventGetAll');
        dbcon.query("SELECT id, name, datetime FROM event;", 
        (err) =>
        {
            socket.emit('eventGetAll', {err:err});
        });
    });

    socket.on('eventGetInfo', (data) =>
    {
        console.log(users[socket.id].mail + ' on eventGetInfo');
        dbcon.query("SELECT description, location, duration FROM event WHERE id = '" + data.id + "';", 
        (err,table) =>
        {
            socket.emit('eventGetInfo', {description:table[0].description,location:table[0].location,duration:table[0].duration});
        });
    });

    socket.on('eventValidate', (data) =>
    {
        console.log(users[socket.id].mail + ' on eventValidate');
        dbcon.query("UPDATE event SET confirmed = 1 WHERE id = " + data.id + ";", 
        (err) =>
        {
            if(!err)
            {
                socket.emit('eventValidate',{});
            }
            else
            {
                console.log(err);
            }
        });
    });

    socket.on('eventDismiss', (data) =>
    {
        console.log(users[socket.id].mail + ' on eventDismiss');
        dbcon.query("DELETE FROM event WHERE id = '" + data.id + "';", 
        (err)=>
        {
            if(!err)
            {
                socket.emit('eventDismiss',{});
            }
            else
            {
                console.log(err);
            }
        });
    });

    socket.on('disconnect', (data)=>
    {
        console.log(users[socket.id].mail + ' on disconnect');
        for(var spy in users[socket.id].spyed_by)
        {
            io.sockets.socket(spy).emit('userSpyedTargetDisconnected',{mail:users[socket.id].mail});
        }
        users.splice(users.id,1);
    });
});