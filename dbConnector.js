'use strict'
const mysql = require('mysql');
let pool = mysql.createPool({
  host: "den1.mysql4.gear.host",
  user: "nurespy",
  password: "Ko6yLZ_0-voR",
  database:"nurespy"
});

exports.query = function(sql, cb)
{
  pool.getConnection((err, connection) =>{
  connection.query(sql, (err, result) =>{
    if(typeof cb === 'function' && !err)  cb(result);
    
    connection.release();

    if (err) throw err;
    });
  })
}

/* exports.query = function (sql,cb){
  con.connect()
  con.query(sql, function (err, result){
      if (err) console.log(err)
      if(typeof cb === 'function' && !err)  cb(result);
  });
}
exports.MultiQuery = function (sql,values,cb){
    con.query(sql,[values], function (err, result){
       if (err) console.log(err)
       if(typeof cb === 'function' && !err)  cb(result);
        console.log("Number of records inserted: " + result.affectedRows);
    });
}
exports.endc = ()=>{
  con.end((err)=>{
  if(err) throw err;
  });
} */
