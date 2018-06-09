document.addEventListener('DOMContentLoaded', () => {
  const socket = io('/')
    socket.emit('signIn', {mail: 'nope', password: 'asd'});
  socket.on('signIn', res =>{
    document.writeln(` Sign In code = ${res.err}`);
    switch (res.err) {
      case 0:
        document.writeln(` Sign In code = ${res.err} Fullname = ${res.fullname}`);
        break;
        case 1:
        document.writeln(` Sign In code = ${res.err} WRONG EMAIL`);
        break;
        case 2:
        document.writeln(` Sign In code = ${res.err} WRONG PASSWORD`);
        break;
      default:
        break;
    }
    //socket.emit('chatCreate', {chat_name: 'PZPI-16-3'});
  });
/*   socket.on('chatCreate', res =>{
    document.writeln(`CHAT CREATED CODE = ${res.err}`);
  }) */
  const positionOptions = {
    enableHighAccuracy: true,
    maximumAge: 0
  }
   /*setInterval(() => {
    navigator.geolocation.getCurrentPosition(pos => {
      const { latitude: lat, longitude: lng } = pos.coords
      socket.emit('userMove', { lat, lng })
    }, error => {
      console.error(error)
      switch(err.code){
        case error.TIMEOUT:
        alert("Browser geolocation error !\n\nTimeout.");
        break;
      case error.PERMISSION_DENIED:
        if(error.message.indexOf("Only secure origins are allowed") == 0) {
        }
        break;
      case error.POSITION_UNAVAILABLE:
        alert("Browser geolocation error !\n\nPosition unavailable.");
        break;
      }
    }, positionOptions)
  }, 5000) 
*/
})

