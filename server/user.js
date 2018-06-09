class User
{ 
    constructor(id,mail)
    { 
        this.spayed_by = []; 
        this.mail = mail; 
        this.id = id; 
        this.coords = 
        { 
            latitude : 0,
            longitude : 0,
            height : 0
        } 
    } 

    StartSpy(id)
    { 
        this.spyed_by.push(id);
    } 

    StopSpy(id)
    { 
        this.spyed_by.splice(this.spyed_by.indexOf(id),1);
    } 

    getId()
    { 
        return this.id; 
    } 

    setId(id)
    { 
        this.id = id; 
    } 

    getMail()
    { 
        return this.mail; 
    } 

    setMail(mail)
    { 
        this.mail = mail; 
    } 

    getCoords()
    { 
        return this.coords; 
    } 
    
    setCoords(latitude, longitude, height)
    { 
        this.coords.latitude = latitude; 
        this.coords.longitude = longitude; 
        this.coords.height = height; 
    } 
};