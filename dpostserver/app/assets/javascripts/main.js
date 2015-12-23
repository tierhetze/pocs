(function (app) {

    function create(elt) { return window.document.createElement(elt); }

    function LineSpeedOMeter (config) {
    	this.displayName = config.displayName;
    	this.maxVal = config.maxVal;
        this.unit = config.unit ? config.unit + " " : "";
        this.name = config.name;
        this.container = config.container;
        
        
        var title = create("span");
        title.innerHTML = this.displayName;
        title.className = 'titlelinemachinery';
        this.container.appendChild(title);
        
        
        this.elt    = create("div");
        this.elt.id = "progressbar"+this.name;
        this.container.appendChild(this.elt);
        
        var divLabel = create("div");
        divLabel.className ="progress-label"+this.name;
        divLabel.innerHTML = "Loading..."; 
        this.elt.appendChild(divLabel);
        
        var bottomDiv = create("div");
        bottomDiv.innerHTML = "<BR>"; 
        this.container.appendChild(bottomDiv);
        
    }
    
    
    function CounterStatusPanel (config) {
    	this.name      = config.name;
        this.container = config.container;
        this.status    = create("div");
        this.status.id = "counter" + this.name;
        this.container.appendChild(this.status);
    }
    
    function CommonStatusPanel (config) {
    	this.name      = config.name;
        this.container = config.container;
        this.status    = create("div");
        this.status.id = "status" + this.name;
        this.container.appendChild(this.status);
    }
    
    function StorageStatusPanel (config) {
    	this.name      = config.name;
        this.container = config.container;
        this.status    = create("div");
        this.status.id = "status" + this.name;
        this.container.appendChild(this.status);
    }
    
    function UpdateStatusPanel (config) {
    	this.name      = config.name;
        this.container = config.container;
        this.status    = create("div");
        this.status.id = "status" + this.name;
        this.container.appendChild(this.status);
    }
    
    CounterStatusPanel.prototype.update = function (val) {
    	if("success" == this.name){
    	    var counterDiv = this.status;
    	    counterDiv.innerHTML = "Successfully sent: "+val;
    	}
    	if("failure" == this.name){
    	    var counterDiv = this.status;
    	    counterDiv.innerHTML = "Failed to send: "+val ;
    	}
    }
    
    CommonStatusPanel.prototype.update = function (val) {
    	/**
    	 * 0 - OK, 1 - ERROR, 2 - UNKNOWN, 3 - WARNING
    	 * 
    	 * */
    	var text = "";
    	
    	if(val == 0){
    		text = "No problems discovered";
    	}
    	
		if(val == 1){
			text = "<span class=\"failure\">Failure</span>";		
		}
		
		if(val == 2){
			text = "Looks ok, but not fully checked";
		}
		
		if(val == 3){
			text = "<span class=\"warning\">Some e-mails may not be sent</span>";
		}
    	
    	var counterDiv = this.status;
    	counterDiv.innerHTML = text;
    }
    
    StorageStatusPanel.prototype.update = function (val) {
    	/**
    	 * true - problems, false - OK
    	 * 
    	 * */
    	var text = "";
    	
    	if(val == "true"){
    		text = "<span class=\"failure\">Storage failure</span>";
    	}
    	
		if(val == "false"){
			text = "Storage is OK";		
		}
		
		var counterDiv = this.status;
    	counterDiv.innerHTML = text;
    }
    
    UpdateStatusPanel.prototype.update = function (val) {
    	var counterDiv = this.status;
    	counterDiv.innerHTML = val;
    }
    
    /**
     * This nice speedOMeter is not in use now
     * */
    function SpeedOMeter (config) {
        this.maxVal = config.maxVal;
        this.unit = config.unit ? config.unit + " " : "";
        this.name = config.name;
        this.container = config.container;
        this.elt = create("div");
        this.elt.className = "monitor";

        var title = create("span");
        title.innerHTML = this.name;
        title.className = 'titlewhitebold';
        this.elt.appendChild(title);

        this.screenCurrent = create("span");
        this.screenCurrent.className = 'screencurrent';
        this.elt.appendChild(this.screenCurrent);

        this.screenMax = create("span");
        this.screenMax.className = 'screenmax';
        this.screenMax.innerHTML = this.maxVal + this.unit;
        this.elt.appendChild(this.screenMax);

        this.needle = create("div");
        this.needle.className = "needle";
        this.elt.appendChild(this.needle);

        this.light = create("div");
        this.light.className = "green light";
        this.elt.appendChild(this.light);

        var wheel = create("div");
        wheel.className = "wheel";
        this.elt.appendChild(wheel);

        this.container.appendChild(this.elt);
    }

    SpeedOMeter.prototype.red = function () {
        this.light.className = "red light";
    };

    SpeedOMeter.prototype.green = function () {
        this.light.className = "red green";
    };

    SpeedOMeter.prototype.update = function (val) {
    	Zanimo.transition(
            this.needle,
            "transform",
            "rotate(" + (val > this.maxVal ? 175 : val * 170 / this.maxVal) + "deg)",
            500,
            "ease-in"
        );
        this.screenCurrent.innerHTML = val + this.unit;
    }
    
    LineSpeedOMeter.prototype.update = function (val) {
    	 
    	 var division = this.maxVal / 100;
    	 if(division <= 0){
    		 division = 1;
    	 }
    	 
    	 var v =  parseInt(val / division, 10) ;
    	 
    	 
    	 var progressbar = $( "#progressbar" + this.name ),
    	 progressLabel = $( ".progress-label" + this.name);
    	 
    	 
    	 progressbar.progressbar({
		     value: false,
			 change: function() {
			    progressLabel.text( val + "" );
			 },
			 complete: function() {
			     progressLabel.text( "Maximum reached!" );
			 }
		 });
    	 
    	 
    	 progressbar.progressbar( "value", v );
    	 
    }

    function init() {

        window.document.addEventListener('touchmove', function (evt) {
            evt.preventDefault();
        }, false);

        /*app.rps = new SpeedOMeter({
            name : "RPH",
            maxVal : 50000,
            container : window.document.getElementById("monitoring")
        });*/
        
        app.rps = new LineSpeedOMeter({
        	displayName: "DPost requests per hour(avg)",
            name : "RPH",
            maxVal : 50000,
            container : window.document.getElementById("monitoring")
        });
        

       /* app.memory = new SpeedOMeter({
            name : "MEMORY",
            maxVal : app.totalMemory,
            unit : "MB",
            container : window.document.getElementById("monitoring")
        });*/
        
        app.memory = new LineSpeedOMeter({
        	displayName: "Memory used from heap (Maximum="+app.totalMemory+" Mb)",
            name : "MEMORY",
            maxVal : app.totalMemory,
            unit : "MB",
            container : window.document.getElementById("monitoring")
        });
        
        app.cpu = new LineSpeedOMeter({
        	displayName: "CPU utilization",
            name : "CPU",
            maxVal : 100,
            unit : "%",
            container : window.document.getElementById("monitoring")
        });
        
        
        app.successCounter = new CounterStatusPanel(
    		{
            	name : "success",
                container : window.document.getElementById("successCounterParent")
            }
        );
        
        app.failureCounter = new CounterStatusPanel(
    		{
            	name : "failure",
                container : window.document.getElementById("failureCounterParent")
            }
        );
        
        app.commonstatus = new CommonStatusPanel(
    		{
            	name : "common",
                container : window.document.getElementById("commonStatusParent")
            }
        );
        
        app.storagestatus= new StorageStatusPanel(
    		{
            	name : "storage",
                container : window.document.getElementById("storageStatusParent")
            }
        );
        
        app.updatestatus= new UpdateStatusPanel(
    		{
            	name : "update",
                container : window.document.getElementById("updateStatusParent")
            }
        );

        /*app.cpu = new SpeedOMeter({
            name : "CPU",
            maxVal : 100,
            unit : "%",
            container : window.document.getElementById("monitoring")
        });*/
        
        

//        var button = create("button");
//        button.className = "gc";
//        button.innerHTML = "GARBAGE COLLECT";
//
//        button.addEventListener(
//            button.ontouchstart === null ? "touchstart" : "click",
//            function (evt){
//                evt.target.className += " touch";
//                var xhr = new XMLHttpRequest();
//                xhr.open("POST", "/gc!", true);
//                xhr.onreadystatechange = function (){
//                    if(xhr.readyState == 4) {
//                        evt.target.className = "gc";
//                        xhr.status == 200 ? console.log(xhr.responseText) : console.log(xhr.status);
//                    }
//                };
//                xhr.send();
//            },
//            false
//        );

        //window.document.body.appendChild(button);

        var iframe = create("iframe");
        iframe.src = "/monitoring";
        iframe.style.display = "none";

        window.message = function (msg) {
            var d = msg.split(":");
            app.lastCall = (new Date()).getTime();
            if (d.length == 2) {
                app[d[1]].update(d[0]);
            }
        }

        setTimeout(function () {
            app.lastCall = (new Date()).getTime();
            window.document.body.appendChild(iframe);
        }, 100);

        setInterval(function () {
            if ((new Date()).getTime() - app.lastCall > 5000) {
                app.rps.red();
                app.memory.red();
                app.cpu.red();
            }
        },1000);
    }

    window.document.addEventListener("DOMContentLoaded", init, false);

})(window.App);
