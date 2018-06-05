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
    'Fill':function (login)
    {
        this.login = login;
    }
};

var users = [];

io.on('connection', (socket) =>
{
    users[socket.id] = user;
    users[socket.id].Fill('Guest');
    console.log('Connected:' + users[socket.id].login);

    socket.on('message',function (data)
    {  
        io.emit('message',{user:users[socket.id].login,text:data.data});
    });

    socket.on('signIn', (data)=>
    {
        console.log(users[socket.id].login + ' on singIn');
        dbcon.query("SELECT id FROM user WHERE login = '" + data.login + "' AND password = '"+ data.password + "';",function (err,table){
            if(!err)
            {
                if(table.length>0)
                {
                    socket.emit('signInSuccess',{id:table[0].id});
                    users[socket.id].Fill(data.login);        
                }    
                else
                {
                    socket.emit('signInSuccess',{id:-1});               
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
        console.log(users[socket.id].login + ' on singIn');
        dbcon.query("SELECT id FROM user WHERE '" + data.mail + "';",(err,table)=>
        {
            if(!err)
            {
                if(table.length > 0)     
                socket.emit('signUpError',{err:'Mail is reserved'}); 
            }
            return;          
        });
        dbcon.query("INSERT INTO user SET fullname = '" + data.fullname + "', login = '" + data.login + "',password ='" + data.password + "', mail = '" + data.mail + "';",(err)=>
        {
            if(!err)
            dbcon.query("SELECT id FROM user WHERE mail = '" + data.mail + "';",(err,table)=>
            {
                socket.emit('signUpSuccess',{id:table[0].id});
            });
            else
            {
                console.log(err);
            }
        });
    });
});