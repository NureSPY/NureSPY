'use strict'

const http = require('http')
const path = require('path')
const express = require('express')
const bodyParser = require('body-parser')
const socketIo = require('socket.io')

const app = express()
const server = http.createServer(app)
const io = socketIo(server)

//lists
const locationMap = new Map()
const onlineUsers = new Map()
//object-types = classes
const User = require('./User.js')

app.use(express.static(path.join(__dirname, 'public')))

app.get('/', (req, res) => {
  res.send('Hello!')
})

const db = require('./dbConnector.js')

io.on('connection', socket => {
  console.log("A user connected", socket.id)
  let user = new User("gues-mail")
  onlineUsers.set(socket.id, user)

  socket.on('signIn', data =>{
  console.log(`[SING IN] ${user.getMail()} is on Singing In!`);
  db.query("SELECT * FROM user WHERE mail = '" + data.mail + "';", res =>{
    if(res.length>0)
    {
      if(data.password == res[0].password){
        socket.emit('signIn',{err:0, 
          fullname: res[0].fullname,
          mail: res[0].mail, 
          password: res[0].password, 
          group: res[0].group, 
          stay_in: res[0].stay_in,
        status: res[0].status})
      
        user.setId(res[0].id)
        user.setMail(res[0].mail)
        console.log(onlineUsers) 
        console.log(res)       
      }else{
        socket.emit('signIn',{err:2});// wrong password 
      }
    }    
    else
    {
        socket.emit('signIn',{err:1});// wrong mail         
    }
  })
})

  socket.on('signUp', data =>{
  console.log(`[SING UP] ${user.getMail()} is on Singing Up!`);
  db.query("SELECT id FROM user WHERE mail = '" + data.mail + "';", res =>{
      if(res.length > 0)     
      {
        socket.emit('signUp',{err:1});//mail already used
      } else db.query("INSERT INTO user SET fullname = '" + data.fullname + "',password ='" + data.password + "', mail = '" + data.mail + "', phone = '" + data.phone +"', `group` = '8', status = 'student';", res =>{
         // console.log(res)
          socket.emit('signUp',{err:0});// success register - SUCCESS
        })
      })
    })

   socket.on('singOut', data =>{
     console.log(`[SING OUT] ${user.getMail()} is on Singing Out!`);
      let b = onlineUsers.delete(socket.id);
      socket.emit('signOut',{isSignedOut: b});
  });
 
  socket.on('chatCreate', data => {
    console.log(`[CHAT CREATE] ${user.getMail()} is on Chat create!`);
        db.query("INSERT INTO chat SET name = '" + data.chat_name +"' ,user_id = '"+ user.getId()+ "';", (res) =>{
          socket.emit('chatCreate',{err: 0});
        });
    });

    socket.on('chatJoin', data =>
    {
      console.log(`[CHAT JOIN] ${user.getMail()} is on Singing Out!`);
      db.query("SELECT id FROM user WHERE mail ='"+data.mail+"';", table =>{
      if(table.length > 0){
        db.query("INSERT INTO chat_user SET chat_id = " + data.chat_id + ",user_id = " + data.user_id + ",access = '" + data.access + "';", res =>{
          socket.emit('chatJoin',{err: 0});
        })
      }
      })
    });

    socket.on('chatBan', (data)=>
    {
      console.log(`[CHAT BAN] ${user.getMail()} is on BANING!`);
      db.query("SELECT id FROM user WHERE mail ='"+data.mail+"';", table =>{
        if(table.length > 0){
        db.query("DELETE FROM chat_user WHERE user_id = " + data.user_id + " AND chat_id = " + data.chat_id + ";", res =>{
          socket.emit('chatBan',{});
        });
       }
      }) 
    });
    socket.on('chatDelete', data =>
    {
      console.log(`[CHAT DELETE] ${user.getMail()} is on CHAT DELETE!`);
        db.query("DELETE FROM chat WHERE chat = " + data.chat_id + ";", res =>{
          socket.emit('chatDelete',{});
        });
        
    });
    socket.on('chatGetMine', data =>{
      console.log(`[SING OUT] ${user.getMail()} is on Singing Out!`);
        db.query("SELECT id FROM chat,user WHERE user_id = user.id AND user.email = '"+data.mail+"';", res => {
           socket.emit('chatGetMine', {id:res});
        });
    });
    //chatGetInfo = Get name of chat and members
    socket.on('chatGetInfo', (data)=>
    {
      console.log(user.getMail() + ' on chatGetInfo');
      db.query("SELECT name FROM chat WHERE id = " + data.id + ";",name =>
      {
        if(name.length > 0){
        db.query("SELECT * FROM user,chat,chat_user WHERE chat_user.user_id = user.id AND chat_user.chat_id = " + data.id + ";", users =>
        {
          socket.emit('chatGetInfo',{'name':name[0].name,'users':users});
        });
      }
      });
    });
    
    socket.on('chatSend', (data)=>
    {
      console.log(user.getMail() + ' on chatSend');
      db.query("SELECT id FROM user WHERE mail = '" + user.getMail() + "';",
      table=>
      {
        if(table.length > 0){
          db.query("INSERT INTO message SET user_id = " + table[0].id + ",chat_id = " + data.id + ",text = '" + data.text + "',datetime = '" + data.datetime + "';",
          table =>
          {
              dbcon.query("SELECT mail FROM user,chat_user WHERE chat_user.user_id = user.id AND chat_user.chat_id = " + data.id + ";",
              table =>
              {
                onlineUsers.forEach((val, key)=>{
                        for(var t  in table)
                        {
                          if(val.getMail() == t.login)
                          {
                              io.sockets.socket(key).emit('chatMessage',{chat_id:data.id,from:user.getMail(),text:data.text,datetime:data.datetime});
                          }
                        }
                      });
                      socket.emit('chatSend',{});
                  });
              });
            }//if mail exist
          });            
        });

        socket.on('chatGetMessages', (data)=>
        {
            db.query("SELECT user.mail,text,datetime FROM message,user WHERE message.user_id = user.id AND message.chat_id = " +  data.id+ ";",
            table =>
            {
              if(table.length > 0){
                    socket.emit('chatGetMessages',{messages:table});
              }
            });
        });

  socket.on('userMove', pos => {
  //  console.log(socket.id, socket.client.id);
     user.setCoords(pos.lat, pos.lng)
    let coords = user.getCoords()
    console.log(socket.id, coords.lat, coords.lng)
    locationMap.set(socket.id, pos)
  })

  socket.on('requestLocations', () => {
    socket.emit('locationsUpdate', Array.from(locationMap))
  })

  socket.on('disconnect', () => {
    console.log('A user disconnected.');
    user.spayed_by.forEach((value, index, array)=>{
      socket.to(index).emit(('userSpyedTargetDisconnected',{mail:user.mail}))
      array.splice()
    })
      for(var spy in users[socket.id].spyed_by)      
      {          
              io.sockets.socket(spy).emit('userSpyedTargetDisconnected',{mail:users[socket.id].mail});
              users[socket.id].StopSpy(spy);
      }
    onlineUsers.delete(socket.id)
    locationMap.delete(socket.id)
  })
})
server.listen(3001,"0.0.0.0", err => {

 /*  db.query("SELECT password FROM user WHERE mail = 'nope'", res =>{
    console.log(res);
  }) */ // test queries

  if (err) {
    throw err
  }
/*   setInterval(()=>{
    db.query('SELECT 1', res =>{
      console.log(res);
    })
  }, 5000) */
  console.log("Server works")
})
