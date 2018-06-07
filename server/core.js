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
    'login':'',
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
    'Fill':function (id,login)
    {
        this.id = id;
        this.login = login;
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
        console.log(users[socket.id].login + ' on singIn');
        dbcon.query("SELECT id FROM user WHERE login = '" + data.login + "' AND password = '"+ data.password + "';",
        (err,table)=> 
        {
            if(!err)
            {
                if(table.length>0)
                {
                    socket.emit('signIn',{err:0});
                    users[socket.id].Fill(data.login);        
                }    
                else
                {
                    socket.emit('signIn',{err:1});//wrong login or password           
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
        console.log(users[socket.id].login + ' on singUp');
        var isErr = false;
        dbcon.query("SELECT id FROM user WHERE '" + data.mail + "';",(err,table)=>
        {
            if(!err)
            {
                if(table.length > 0)     
                {
                    socket.emit('signUp',{err:1});//mail already used
                    isErr = true;
                }
            }        
        });
        if(!isErr)
        {
            dbcon.query("SELECT login FROM user WHERE login = '" + data.login + "';",(err,table)=>
            {
                if(!err)
                {
                    socket.emit('signUp',{err:2});//login already reserved
                    isErr = true;
                }
            });
        }
        if(!isErr)
        {
            dbcon.query("INSERT INTO user SET fullname = '" + data.fullname + "', login = '" + data.login + "',password ='" + data.password + "', mail = '" + data.mail + "';",(err)=>
            {
                socket.emit('signUp',{err:0});
            });
        }
    });

    socket.on('singOut', (data)=>
    {
        console.log(users[socket.id].login + ' on signOut');
        users[socket.id].Fill('Guest');
        socket.emit('signOut',{});
    });

    socket.on('chatCreate', (data)=>
    {
        console.log(users[socket.id].login + ' on chatCreate');
        dbcon.query("INSERT INTO chat SET name = '" + data.name + "';");
        socket.emit('chatCreate',{});
    });

    socket.on('chatJoin', (data)=>
    {
        console.log(users[socket.id] + ' on chatJoin');
        dbcon.query("INSERT INTO chat_user SET chat_id = " + data.chat_id + ",user_id = " + data.user_id + ",access = '" + data.access + "';");
        socket.emit('chatJoin',{});
    });

    socket.on('chatBan', (data)=>
    {
        console.log(users[socket.id].login + ' on chatBan');
        dbcon.query("DELETE FROM chat_user WHERE user_id = " + data.user_id + " AND chat_id = " + data.chat_id + ";");
        socket.emit('chatBan',{});
    });

    socket.on('chatDelete', (data)=>
    {
        console.log(users[socket.id].login + ' on chatDelete');
        dbcon.query("DELETE FROM chat WHERE chat = " + data.chat_id + ";");
        socket.emit('chatDelete',{});
    });

    socket.on('chatGetMine', (data)=>
    {
        console.log(users[socket.id] + ' on chatGetMine');
        dbcon.query("SELECT id FROM chat,user WHERE user_id = user.id AND user.login = '" + users[socket.id].login + "';", (err,table)=>
        {
            if(!err)
            {
                socket.emit('chatGetMine', {id:table});
            }
        });
    });

    socket.on('chatGetInfo', (data)=>
    {
        console.log(users[socket.id].login + ' on chatGetInfo');
        dbcon.query("SELECT name FROM chat WHERE id = " + data.id + ";",(err,name)=>
        {
            if(!err)
            {
                dbcon.query("SELECT * FROM user,chat,chat_user WHERE chat_user.user_id = user.id AND chat_user.chat_id = " + data.id + ";", (err,users)=>
                {
                    if(!err)
                    {
                        socket.emit('chatGetInfo',{'name':name,'users':users});
                    }
                });
            }
        });
    });

    socket.on('chatSend', (data)=>
    {
        console.log(users[socket.id].login + ' on chatSend');
        dbcon.query("INSERT INTO message SET user_id = " + data.from + ",chat_id = " + data.chat_id + ",text = '" + data.text + "',datetime = '" + data.datetime + "';");
        socket.emit('chatSend',{});
        dbcon.query("SELECT login FROM user,chat_user WHEER chat_user.user_id = user.id AND chat_user.chat_id = " + data.id + ";",(err,table)=>
        {
            if(!err)
            {
                for(var u in users)
                {
                    for(var t  in table)
                    {
                        if(u.login == t.login)
                        {
                            io.sockets.socket(u.id).emit('chatMessage',{chat_id:data.id,from:data.from,text:data.text,datetime:data.datetime});
                        }
                    }
                }
            }
        });
    });
});