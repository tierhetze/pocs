<?php

# Example DPOST PHP client

# Feel free to use and modify this client
# For additional clients info read clients.pdf



#---------------------------Common stuff (written 1-ce in the APP, better be in an include file, here written only for clear example)---------------------
class DpostMail {
   public $subjMap = "";
   public $bodyMap = "";
   public $toMail = "";
   public $template = "";
   public $lang = "";
}

function convertToJson($subjMap, $bodyMap, $toMail, $template, $lang ){
	$mail = new DpostMail();
    $mail->subjMap  = $subjMap;
    $mail->bodyMap  = $bodyMap;
	$mail->toMail   = $toMail;
	$mail->template = $template;
	$mail->lang     = $lang;
	return json_encode($mail);
}

function dpost_send($data_string) {
     #URL is better to make configurable and load 1-ce in your app.
     #but it's up to you
     #note /dpost part is obligatory
     $url = "http://127.0.0.1:9000/dpost";
     $ch = curl_init($url);                                                                      
     curl_setopt($ch, CURLOPT_CUSTOMREQUEST, "POST");
         
     $param_value =  $data_string;
                                                                     
     curl_setopt($ch, CURLOPT_POSTFIELDS, $param_value); 
     
     curl_setopt($ch, CURLOPT_RETURNTRANSFER, true); 
                                                                          
     curl_setopt($ch, CURLOPT_HTTPHEADER, array(                                                                          
       'Content-Type: application/json; charset=UTF-8',                                                                                
       'Content-Length: ' . strlen($param_value))                                                                       
     );                                                                                                                   
 
     $result = curl_exec($ch);
     
     return $result;
     
}
#---------------------------End Of Common stuff, which written once---------------------


#-----Example message that you are going to send, suppose, to client, that interested in your product:--

#Subject line:

#Welcome {$username}

#Body content:

#Welcome to our company

#Dear {$username} ,

#thank you for your interest in our product: {$product}

#Please, contact me for any further question

#contact email: {$salesemail}


#---------------------------Start of sending------------------------

#sending for particular subject

#construct subject parameters map
$subjMap = array("username" => "Peter");

#construct body parameters map
$bodyMap = array("username" => "Peter", "product" => "Samsung Note II", "salesemail" => "peter@peter.com");

#finally create a json string

#method parameters:
#1-par - subject params
#2-par - body params
#3-par - client e-mail
#4-par - template id
#5-par - template's language id
$jsonout = convertToJson( $subjMap, $bodyMap, "yourclient@gmail.com" , "welcome" , "en-US" );


#send (by http post) to the DPost 
#retured result: message accepted or not
#parameters: 
#1-dpost url
#2-json content, including subject and body parameters and other e-mail parameters
$status_accepted = dpost_send($jsonout);

echo $status_accepted;
?>