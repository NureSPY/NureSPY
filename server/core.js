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

var user = {
    'id':'',//socket_id
    'mail':'',
    'spyed_by':'',
    'coords':{
        'latitude':'',
        'longitude':'',
        'height':'',
        'Fill':function (lat,long,h)
        {
            this.latitude = lat;
            this.longitude = long;
            this.height = h;
        }
    },
    'Fill':function (id,mail)
    {
        this.id = id;
        this.mail = mail;
    },
    'Spy':function (id)
    {
        this.spyed_by.push(id);
    },
    'StopSpy':function (id)
    {
        this.spyed_by.slice(this.spyed_by.indexOf(id),1);
    }
};

var users = [];

io.on('connection', (socket) =>
{
    users[socket.id] = user;
    users[socket.id].Fill(socket.id,'Guest');
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
                    socket.emit('signIn',{err:0});
                    users[socket.id].Fill(socket.id,data.login);        
                }    
                else
                {
                    socket.emit('signIn',{err:1});         
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
                                dbcon.query("INSERT INTO user SET fullname = '" + data.fullname + "',password = '" + data.password 
                                + "', mail = '" + data.mail + "',phone = '" + data.phone + "',status = '" + data.status 
                                + "',group = '" + data.group + "';",
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
        console.log(users[socket.id].mail + ' on signOut');
        users[socket.id].Fill(socket.id,'Guest');
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
                dbcon.query("INSERT INTO chat_user SET chat_id = " + data.chat_id + ",user_id = " + table[0].id + ",access = '" + data.access + "';",
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
                dbcon.query("DELETE FROM chat_user WHERE user_id = " + table[0].id + " AND chat_id = " + data.chat_id + ";",
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
});